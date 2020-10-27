package com.jutiyunyuan.filter;

import com.jutiyunyuan.pojo.Admin;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @Author Kong
 * @Date 2020/10/26 22:29
 * @Version 1.0
 */
public class MyFormAuthenticationFilter extends FormAuthenticationFilter {
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        //清除Session中的数据
        WebUtils.getAndClearSavedRequest(request);

        return super.onLoginSuccess(token, subject, request, response);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        //从请求中获取Shiro的主体
        Subject subject = getSubject(request, response);
        //从主体中获取Shiro框架的session
        Session session = subject.getSession();
        //如果主体没有认证 并且 用户选择了记住我功能
        if (!subject.isAuthenticated() && subject.isRemembered()) {
            //获取主体的身份(从rememberMe的cookie中获取)
            Admin principal = (Admin) subject.getPrincipal();
            //将身份认证信息共享到session中, session的名称可以随便取 ,shiro底层会按照类型自动获取
        }
        //用户登录或者使用了rememberMe功能都会返回true
        return subject.isAuthenticated() || subject.isRemembered();
    }
}
