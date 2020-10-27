package com.jutiyunyuan.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.jutiyunyuan.filter.MyFormAuthenticationFilter;
import com.jutiyunyuan.realm.MyRealm;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.REUtil;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.cache.config.CacheManagementConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import sun.misc.Cache;

import javax.annotation.ManagedBean;
import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * @Author Kong
 * @Date 2020/10/26 19:20
 * @Version 1.0
 */
@Configuration
public class ShiroConfig {

    /*配置Shiro的核心过滤器工厂Bean ShiroFilterFactoryBean*/
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        //配置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        //配置认证失败以后的跳转页面,共享认证失败的错误信息(必须是控制器url)
        shiroFilterFactoryBean.setLoginUrl("/admin/loginError");
        //配置认证成功后的跳转页面
        shiroFilterFactoryBean.setSuccessUrl("/index");
        //配置认证成功后访问没有权限的资源是的提示页面(使用注解配置权限,必须放在templates下)
        //注解配置失效
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized.html");


        Map<String, Filter> filters = new HashMap<>();
        filters.put("logout",logoutFilter());
        filters.put("authc",myFormAuthenticationFilter());
        //设置自定义过滤器
        shiroFilterFactoryBean.setFilters(filters);
        /*
        * 配置Shiro框架的过滤器链
        *   Shiro框架和Web项目集成后, 所有的请求多先经过Shiro框架过滤器
        *       1.直接放行 --> 静态资源
        *       2.只需要认证(登录)的 --> 后台首页
        *       3.既需要认证也需要权限的资源 --> admin:list, student:insert
        *   Shiro针对不同资源类设计了多种过滤器(11个)
        *
        *   anon    org.apache.shiro.web.filter.authc.AnonymousFilter
        *           匿名过滤器,  经过此过滤器的直接放行
        *   authc   org.apache.shiro.web.filter.authc.FormAuthenticationFilter
        *           表单认证过滤器 ,经过此过滤器的,必要通过验证
        *   logout	org.apache.shiro.web.filter.authc.LogoutFilter
        *           退出认证过滤器,会自动清空Session和Cookie中的数据,并跳转到项目的根路径
        *   perms	org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter
        *           权限授全过滤器, 只需要将访问的地址设置对应的权限表达式, 如perms[user:list],shiro 底层会
        *           调用自定义的realm中的daGeAuthorizationInfo方法
        *
        *           缺点: 需要将每一个访问资源都配置一个权限表达式,导致项目配置文件臃肿
        *           解决方案: 将注解打在每一个拥有权限的Springmvc控制器方法(增删改查)
        *
        *   user    org.apache.shiro.web.filter.authc.UserFilter
        *           rememberMe以后可以访问的资源
         * */

        //创建过滤器链Map集合   ,   访问的资源匹配过滤器从上到下匹配, 如果匹配成功,就不会继续匹配
        Map<String,String> chainMap = new HashMap<>();
        //匿名过滤器
        chainMap.put("/js/**","anon");
        chainMap.put("/css/**","anon");
        chainMap.put("/images/**","anon");
        chainMap.put("/admin/login","anon");
        //配置表单认证过滤器
        chainMap.put("/**","authc");
        //配置退出过滤器
        chainMap.put("/loginOut","logout");
        //配置user过滤器
        chainMap.put("/index","user");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(chainMap);

        return shiroFilterFactoryBean;
    }

    /*创建对SpringMVC 抛出异常处理解析器*/
    @Bean
    public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
        Properties properties = new Properties();
        /*配置符合SpringBoot的视图解析前后缀规则*/
        properties.put("org.apache.shiro.authz.UnauthorizedException","/unauthorized");
        SimpleMappingExceptionResolver simpleMappingExceptionResolver = new SimpleMappingExceptionResolver();
        simpleMappingExceptionResolver.setExceptionMappings(properties);

        return simpleMappingExceptionResolver;
    }

    /*设置Spring框架支持集成其他框架可以使用AOP*/
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();

        //设置可以让Spring框架使用AOP为表现层创建代理, (Shiro权限判断的注解全部在表现层)
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }


    /*设置shiro框架对注解的支持*/
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        //设置安全管理器
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());

        return authorizationAttributeSourceAdvisor;
    }

    /*自定义FormAuthenticationFilter过滤器*/
    public MyFormAuthenticationFilter myFormAuthenticationFilter() {
        MyFormAuthenticationFilter myFormAuthenticationFilter = new MyFormAuthenticationFilter();
        //
        return myFormAuthenticationFilter;
    }

    /*自定义logout过滤器*/
    public LogoutFilter logoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter();
        //设置退出跳转的登录页面
        logoutFilter.setRedirectUrl("/admin/login");
        return logoutFilter;
    }

   /*配置shiro方言*/
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    /*自定义realm*/
    @Bean
    public MyRealm myRealm() {
        MyRealm myRealm = new MyRealm();

        //设置凭证匹配器
        myRealm.setCredentialsMatcher(credentialsMatcher());

        return myRealm;
    }

    /*凭证匹配器*/
    @Bean
    public CredentialsMatcher credentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        hashedCredentialsMatcher.setHashIterations(3);
        return hashedCredentialsMatcher;
    }


    /*安全管理器*/
    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();

        //配置认证管理器
        defaultWebSecurityManager.setRealm(myRealm());

        //配置缓存
        defaultWebSecurityManager.setCacheManager(cacheManager());

        //配置会话管理器
        defaultWebSecurityManager.setSessionManager(sessionManager());

        //设置rememberMes管理器
        defaultWebSecurityManager.setRememberMeManager(rememberMeManager());

        return defaultWebSecurityManager;
    }

    /*配置rememberMe管理器*/
    @Bean
    public RememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        //设置Cookie
        cookieRememberMeManager.setCookie(cookie());

        return cookieRememberMeManager;
    }

    /*创建cookie*/
    @Bean
    public Cookie cookie() {
        SimpleCookie simpleCookie = new SimpleCookie();
        //设置cookie时长, 一个月
        simpleCookie.setMaxAge(60 * 60 * 24 * 30);
        //设置cookie的名称
        simpleCookie.setName("rememberMe");
        return simpleCookie;
    }

    /*配置会话管理器*/
    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();

        //设置全局session超时时间,单位是毫秒
        defaultWebSessionManager.setGlobalSessionTimeout(10 * 1000);

        return defaultWebSessionManager;

    }

    /*配置缓存*/
    @Bean
    public CacheManager cacheManager() {
        EhCacheManager ehCacheManager = new EhCacheManager();
        //可以在这配置缓存策略, 在resources下编写配置文件ehcache.xml
//        ehCacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
        return ehCacheManager;
    }

}
