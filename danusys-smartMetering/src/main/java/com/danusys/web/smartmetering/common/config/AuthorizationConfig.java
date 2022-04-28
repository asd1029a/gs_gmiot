/*package com.danusys.smartmetering.common.config;

import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;

//@Configuration
//@EnableAuthorizationServer
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

	@Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("some_client_id")
                .secret("some_client_secret")
                .scopes("read:current_user", "read:users")
                .authorizedGrantTypes("client_credentials");
    }
}*/