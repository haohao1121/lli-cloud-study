
lli-cloud-sso-cas-client
---
原生java-cas-client,采用最新3.6.2版本

### Advanced configuration
由于该模块没有对外开放原CAS client configuration options,但是我们可以自己实现 `CasClientConfigurer` 来自定义添加我们需要的配置,
比如排除不需要登录验证的url路径等.

#### 使用方式
1. 使用maven install到本地仓库
2. 引入依赖
```
    <dependency>
      <groupId>org.jasig.cas.client</groupId>
      <artifactId>cas-client-support-springboot</artifactId>
      <version>3.6.2</version>
    </dependency>
```
3.项目配置
```
    cas:
      server-url-prefix: http://localhost:8080/cas
      server-login-url: http://localhost:8080/cas/login
      client-host-url: http://localhost:${server.port}
```

3.启动类或自定义配置类添加注解 @EnableCasClient
```
    @Autowired
    private ZookeeperDistributedLock zookeeperDistributedLock;
```

4.排除不需要验证的url
```
    @Configuration
    public class MyCasClientConfiguration implements CasClientConfigurer {
        /**
         * Configure or customize CAS authentication filter.
         *
         * @param authenticationFilter the authentication filter
         */
        @Override
        public void configureAuthenticationFilter(FilterRegistrationBean authenticationFilter) {
    
            //不需要认证的路径
            List<String> ignoreUrlPattern = new ArrayList<>();
            ignoreUrlPattern.add("/healthCheck");
            ignoreUrlPattern.add("/test/");
    
            authenticationFilter.addInitParameter(ConfigurationKeys.IGNORE_PATTERN.getName(), String.join("|", ignoreUrlPattern));
            authenticationFilter.addInitParameter(ConfigurationKeys.IGNORE_URL_PATTERN_TYPE.getName(), ConfigurationKeys.IGNORE_URL_PATTERN_TYPE.getDefaultValue());
            authenticationFilter.setOrder(1);
        }
    }
```