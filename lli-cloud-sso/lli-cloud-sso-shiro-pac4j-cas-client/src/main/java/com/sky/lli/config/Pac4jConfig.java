package com.sky.lli.config;

import com.sky.lli.Pac4jCasProperties;
import io.buji.pac4j.context.ShiroSessionStore;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author kluas
 * @version 2019/7/16 8:25
 */
@Configuration
public class Pac4jConfig {

    @Resource
    private Pac4jCasProperties pac4jCasProperties;

    @Bean
    public Config config(CasClient casClient, ShiroSessionStore shiroSessionStore) {
        Config config = new Config();
        config.setSessionStore(shiroSessionStore);
        Clients clients = new Clients(casClient);
        config.setClients(clients);
        return config;
    }

    @Bean
    public ShiroSessionStore shiroSessionStore() {
        return new ShiroSessionStore();
    }

    @Bean
    public CasClient casClient(CasConfiguration casConfig) {
        CasClient casClient = new CasClient(casConfig);
        casClient.setCallbackUrl(pac4jCasProperties.getClientHostUrl() + "/callback?client_name=" + pac4jCasProperties.getClientName());
        casClient.setName(pac4jCasProperties.getClientName());
        return casClient;
    }

    @Bean
    public CasConfiguration casConfig() {
        final CasConfiguration casConfiguration = new CasConfiguration();
        casConfiguration.setLoginUrl(pac4jCasProperties.getServerLoginUrl());
        //CAS30协议可以接受CAS server返回的attitudes
        casConfiguration.setProtocol(CasProtocol.CAS30);
        casConfiguration.setAcceptAnyProxy(true);
        casConfiguration.setPrefixUrl(pac4jCasProperties.getServerUrlPrefix());
        return casConfiguration;
    }
}
