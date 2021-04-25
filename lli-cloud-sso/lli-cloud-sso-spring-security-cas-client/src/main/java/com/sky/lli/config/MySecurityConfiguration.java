package com.sky.lli.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 说明:
 *
 * @author klaus
 * @date 2021/4/25
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MySecurityConfiguration extends WebSecurityConfigurerAdapter {

    private Pattern rolesPattern = Pattern.compile("roles\\[(\\S+)]");
    private Pattern permsPattern = Pattern.compile("perms\\[(\\S+)]");
    private Pattern ipAddrPattern = Pattern.compile("ipaddr\\[(\\S+)]");

    @Resource
    private SecurityCasProperties securityCasProperties;

    /**
     * 用户自定义的AuthenticationUserDetailsService
     */
    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> casUserDetailsService() {
        return new CasUserDetailsServiceImpl();
    }

    /**
     * 定义安全策略
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 对过滤链按过滤器名称进行分组
        Map<Object, List<Map.Entry<String, String>>> groupingMap = securityCasProperties.getFilterChainDefinitionMap().entrySet()
                .stream().collect(Collectors.groupingBy(Map.Entry::getValue, TreeMap::new, Collectors.toList()));

        // https://www.jianshu.com/p/01498e0e0c83
        Set<Object> keySet = groupingMap.keySet();
        for (Object key : keySet) {
            // Ant表达式 = roles[xxx]
            Matcher rolesMatcher = getRoleMatcher(http, groupingMap, key);
            //Ant表达式 = perms[xxx]
            getPermsMatcher(http, groupingMap, key);
            //Ant表达式 = ipaddr[192.168.1.0/24]
            getIpAddrMatcher(http, groupingMap, key, rolesMatcher);
        }
        //禁用CSRF
        http.csrf().disable();
    }

    /**
     * Ant表达式 = ipaddr[192.168.1.0/24]
     */
    private void getIpAddrMatcher(HttpSecurity http, Map<Object, List<Map.Entry<String, String>>> groupingMap, Object key, Matcher rolesMatcher) throws Exception {
        // Ant表达式 = ipaddr[192.168.1.0/24]
        Matcher ipMatcher = ipAddrPattern.matcher(key.toString());
        if (rolesMatcher.find()) {

            String[] strings = groupingMap.get(key.toString()).stream().map(Map.Entry::getKey).toArray(String[]::new);
            // ipaddress
            String ipaddr = ipMatcher.group(1);
            if (StringUtils.hasText(ipaddr)) {
                // 如果请求来自给定IP地址的话，就允许访问
                http.authorizeRequests().antMatchers(strings)
                        .hasIpAddress(ipaddr);
            }
        }
    }

    /**
     * Ant表达式 = perms[xxx]
     */
    private void getPermsMatcher(HttpSecurity http, Map<Object, List<Map.Entry<String, String>>> groupingMap, Object key) throws Exception {
        // Ant表达式 = perms[xxx]
        Matcher permsMatcher = permsPattern.matcher(key.toString());
        if (permsMatcher.find()) {

            List<String> antPatterns = groupingMap.get(key.toString()).stream().map(Map.Entry::getKey).collect(Collectors.toList());
            // 权限标记
            String[] perms = StringUtils.split(permsMatcher.group(1), ",");
            if (null != perms && perms.length > 0) {
                if (perms.length > 1) {
                    // 如果用户具备给定全权限的某一个的话，就允许访问
                    http.authorizeRequests().antMatchers(antPatterns.toArray(new String[0]))
                            .hasAnyAuthority(perms);
                } else {
                    // 如果用户具备给定权限的话，就允许访问
                    http.authorizeRequests().antMatchers(antPatterns.toArray(new String[0]))
                            .hasAuthority(perms[0]);
                }
            }
        }
    }

    /**
     * Ant表达式 = roles[xxx]
     */
    private Matcher getRoleMatcher(HttpSecurity http, Map<Object, List<Map.Entry<String, String>>> groupingMap, Object key) throws Exception {
        // Ant表达式 = roles[xxx]
        Matcher rolesMatcher = rolesPattern.matcher(key.toString());
        if (rolesMatcher.find()) {

            //获取所有的key,也就是路径
            List<String> antPatterns = groupingMap.get(key.toString()).stream().map(Map.Entry::getKey).collect(Collectors.toList());
            // 角色
            String[] roles = StringUtils.split(rolesMatcher.group(1), ",");
            if (null != roles && roles.length > 0) {
                if (roles.length > 1) {
                    // 如果用户具备给定角色中的某一个的话，就允许访问
                    http.authorizeRequests().antMatchers(antPatterns.toArray(new String[0]))
                            .hasAnyRole(roles);
                } else {
                    // 如果用户具备给定角色的话，就允许访问
                    http.authorizeRequests().antMatchers(antPatterns.toArray(new String[0]))
                            .hasRole(roles[0]);
                }
            }
        }
        return rolesMatcher;
    }


    @Override
    public void configure(WebSecurity web) throws Exception {

        // 对过滤链按过滤器名称进行分组
        Map<Object, List<Map.Entry<String, String>>> groupingMap = securityCasProperties.getFilterChainDefinitionMap().entrySet()
                .stream().collect(Collectors.groupingBy(Map.Entry::getValue, TreeMap::new, Collectors.toList()));

        //anon权限集合
        List<Map.Entry<String, String>> noneEntries = groupingMap.get("anon");
        List<String> permitMatchers = new ArrayList<>();
        if (!CollectionUtils.isEmpty(noneEntries)) {
            for (Map.Entry<String, String> mapper : noneEntries) {
                permitMatchers.add(mapper.getKey());
            }
        }
        web.ignoring().antMatchers(permitMatchers.toArray(new String[0]))
                .antMatchers(HttpMethod.OPTIONS, "/**");

        super.configure(web);
    }
}
