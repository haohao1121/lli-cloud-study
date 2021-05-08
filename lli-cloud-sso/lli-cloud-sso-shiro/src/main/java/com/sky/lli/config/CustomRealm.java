package com.sky.lli.config;

import com.sky.lli.model.Permission;
import com.sky.lli.model.RoleInfo;
import com.sky.lli.model.UserInfo;
import com.sky.lli.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 描述:
 *
 * @author lihao
 * @date 2021/5/8
 */
@Slf4j
public class CustomRealm extends AuthorizingRealm {

    @Resource
    private UserService userService;

    /**
     * Retrieves the AuthorizationInfo for the given principals from the underlying data store.  When returning
     * an instance from this method, you might want to consider using an instance of
     * {@link SimpleAuthorizationInfo SimpleAuthorizationInfo}, as it is suitable in most cases.
     *
     * @param principals the primary identifying principals of the AuthorizationInfo that should be retrieved.
     * @return the AuthorizationInfo associated with this principals.
     * @see SimpleAuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //获取登录用户名查询用户信息
        UserInfo user = (UserInfo) principals.getPrimaryPrincipal();
        log.info("user:{}", user.getUserName());
        //添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        Set<String> roleSet = new HashSet<>();
        Set<String> permissionSet = new HashSet<>();
        for (RoleInfo roleInfo : user.getRoleList()) {
            roleSet.add(roleInfo.getRoleName());
            if (CollectionUtils.isEmpty(roleInfo.getPermissionList())) {
                continue;
            }
            Set<String> collect = roleInfo.getPermissionList().stream().map(Permission::getPermissionName).collect(Collectors.toSet());
            permissionSet.addAll(collect);
        }
        //添加角色
        simpleAuthorizationInfo.addRoles(roleSet);
        //添加权限
        simpleAuthorizationInfo.addStringPermissions(permissionSet);
        return simpleAuthorizationInfo;
    }

    /**
     * Retrieves authentication data from an implementation-specific datasource (RDBMS, LDAP, etc) for the given
     * authentication token.
     * <p/>
     * For most datasources, this means just 'pulling' authentication data for an associated subject/user and nothing
     * more and letting Shiro do the rest.  But in some systems, this method could actually perform EIS specific
     * log-in logic in addition to just retrieving data - it is up to the Realm implementation.
     * <p/>
     * A {@code null} return value means that no account could be associated with the specified token.
     *
     * @param token the authentication token containing the user's principal and credentials.
     * @return an {@link AuthenticationInfo} object containing account data resulting from the
     * authentication ONLY if the lookup is successful (i.e. account exists and is valid, etc.)
     * @throws AuthenticationException if there is an error acquiring data or performing
     *                                 realm-specific authentication logic for the specified <tt>token</tt>
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        if (StringUtils.isEmpty(token.getPrincipal())) {
            return null;
        }
        //获取用户信息
        String name = token.getPrincipal().toString();
        UserInfo user = this.userService.findUserByAccout(name);
        if (user == null) {
            //这里返回后会报出对应异常
            return null;
        } else {
            //这里验证authenticationToken和simpleAuthenticationInfo的信息
            return new SimpleAuthenticationInfo(user, user.getUserPwd(), getName());
        }
    }
}
