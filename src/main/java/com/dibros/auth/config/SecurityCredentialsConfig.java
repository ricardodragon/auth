package com.dibros.auth.config;

import com.dibros.core.token.config.TokenConfig;
import com.dibros.core.token.filter.JwtTokenAuthorizationFilter;
import com.dibros.core.token.property.JwtConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityCredentialsConfig extends TokenConfig{

    public SecurityCredentialsConfig(JwtConfiguration jwtConfiguration) {
        this.getUrls().addAll(Arrays.asList(jwtConfiguration.getLoginUrl(), "/usuarios/email-token"));
    }

    @Bean
    @Override
    public SecurityWebFilterChain configure(ServerHttpSecurity http){
        http.addFilterAt(new JwtTokenAuthorizationFilter(), SecurityWebFiltersOrder.AUTHORIZATION);
        return super.configure(http);
    }
}
