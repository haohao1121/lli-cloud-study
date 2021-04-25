package com.sky.lli.config;

import io.buji.pac4j.realm.Pac4jRealm;
import io.buji.pac4j.subject.Pac4jPrincipal;
import io.buji.pac4j.token.Pac4jToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.pac4j.core.profile.CommonProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kluas
 * @version 2019/7/15 17:15
 */

@Slf4j
public class CasRealm extends Pac4jRealm {

    private String clientName;

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) {
        final Pac4jToken pac4jToken = (Pac4jToken) authenticationToken;
        final List<CommonProfile> commonProfileList = pac4jToken.getProfiles();
        final CommonProfile commonProfile = commonProfileList.get(0);
        log.info("clientName:{},单点登录返回的信息:{}", clientName, commonProfile.toString());
        final Pac4jPrincipal principal = new Pac4jPrincipal(commonProfileList, getPrincipalNameAttribute());
        final PrincipalCollection principalCollection = new SimplePrincipalCollection(principal, getName());
        return new SimpleAuthenticationInfo(principalCollection, commonProfileList.hashCode());
    }

    /**
     * 获取roles和perms
     * info.addStringPermission(String.valueOf(commonProfile.getAttribute("perms")));
     * info.addRole(String.valueOf(commonProfile.getAttribute("roles")));
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Object user = super.getAvailablePrincipal(principals);
        log.info("登录用户：{}", user);
        SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
        List<String> permissions = new ArrayList<>();
        permissions.add("user:info");
        authInfo.addStringPermissions(permissions);

        return authInfo;
    }

}
