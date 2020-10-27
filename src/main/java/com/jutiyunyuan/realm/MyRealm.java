package com.jutiyunyuan.realm;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.jutiyunyuan.pojo.Admin;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author Kong
 * @Date 2020/10/26 20:05
 * @Version 1.0
 */
public class MyRealm extends AuthorizingRealm {

    /*授权方法*/
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("====================授权方法====================");
        //1.获取认证通过的身份
        Admin admin = (Admin) principalCollection.getPrimaryPrincipal();
        //2.从当前身份中获取角色id
        //3.使用角色id区权限表中查询出对应的所有权限表达式
        //模拟数据
        List<String> permissions = new ArrayList<>();
        permissions.add("user:list");
        permissions.add("user:update");
        permissions.add("student:list");
        permissions.add("role:list");


        //4.创建授权信息对象
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        //5.将查询出的权限表达式设置给授权信息对象
        simpleAuthorizationInfo.addStringPermissions(permissions);

        //6.返回封装权限表达式的授权信息对象
        return simpleAuthorizationInfo;
    }

    /*认证方法*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("====================认证方法====================");
        //1.获取Token信息
        String username = (String) authenticationToken.getPrincipal();


        if (!"admin".equals(username)) {
            return null;
        }

        //模拟service层查询出来的数据
        Admin admin = new Admin("admin", "3733e87d5fe26530d9e85f211e65a4bb","qwer");

        if (Objects.nonNull(admin)) {

            ByteSource salt = ByteSource.Util.bytes(admin.getSalt());

            //创建认证信息对象
            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(admin, admin.getPassword(), salt,this.getName());
            return simpleAuthenticationInfo;
        }
        return null;
    }
}
