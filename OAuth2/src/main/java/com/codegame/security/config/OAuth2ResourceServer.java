package com.codegame.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter
{
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.requestMatchers()
			//2fa
				.antMatchers("/login", "/oauth/authorize", "/secure/two_factor_authentication")
			.and()
        	.authorizeRequests()
//            .antMatchers("/api/admin/**").access("#oauth2.hasScope('read_profile_info')")
//			.antMatchers("/api/admin/**").access("hasRole('ADMIN')")
            .antMatchers("/api/user/**").access("#oauth2.hasScope('ADMIN')")
        	.antMatchers("/api/v1/**").authenticated();
	}
}
