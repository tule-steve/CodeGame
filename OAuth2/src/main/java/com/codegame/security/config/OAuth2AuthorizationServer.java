package com.codegame.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Order(10)
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        Map<String, CorsConfiguration> corsConfigMap = new HashMap<>();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        corsConfigMap.put("/oauth/token", config);
        endpoints.getFrameworkEndpointHandlerMapping().setCorsConfigurations(corsConfigMap);
        endpoints.authenticationManager(authenticationManager)
                 //2fa
                 .requestFactory(customOAuth2RequestFactory())
                 .tokenEnhancer(tokenEnhancer());
    }

    @Bean
    public DefaultOAuth2RequestFactory customOAuth2RequestFactory() {
        return new CustomOAuth2RequestFactory(clientDetailsService);
    }

    @Bean
    public FilterRegistrationBean twoFactorAuthenticationFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(twoFactorAuthenticationFilter());
        registration.addUrlPatterns("/oauth/token");
        registration.setName("twoFactorAuthenticationFilter");
        return registration;
    }

    @Bean
    public TwoFactorAuthenticationFilter twoFactorAuthenticationFilter() {
        return new TwoFactorAuthenticationFilter();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory()
                .withClient("clientapp")
                .secret(passwordEncoder.encode("123456"))
                .authorizedGrantTypes("password")
                //begin 2fa
                //			.authorities("READ_ONLY_CLIENT")
                .authorities(TwoFactorAuthenticationFilter.ROLE_TWO_FACTOR_AUTHENTICATION_ENABLED)
                //end 2fa
                .scopes("read_profile_info")
                .resourceIds("oauth2-resource")
                .redirectUris("/login")
                .accessTokenValiditySeconds(3000)
                .refreshTokenValiditySeconds(3000);
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }
}
