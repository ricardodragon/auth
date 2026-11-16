package com.dibros.auth.config;

import com.dibros.core.token.config.TokenConfig;
import com.dibros.core.token.filter.JwtTokenAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityCredentialsConfig extends TokenConfig{

    private final JwtTokenAuthorizationFilter jwtTokenAuthorizationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {
        http.addFilterAt(this.jwtTokenAuthorizationFilter, SecurityWebFiltersOrder.AUTHORIZATION);
        return super.configure(http);
    }
}
