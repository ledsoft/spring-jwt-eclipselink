package com.github.ledsoft.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ledsoft.demo.security.*;
import com.github.ledsoft.demo.service.security.AppUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationProvider authenticationProvider;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final AuthenticationSuccess authenticationSuccessHandler;

    private final AuthenticationFailureHandler authenticationFailureHandler;

    private final JwtUtils jwtUtils;

    private final ObjectMapper objectMapper;

    private final AppUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(AuthenticationProvider authenticationProvider,
                          AuthenticationEntryPoint authenticationEntryPoint,
                          AuthenticationSuccess authenticationSuccessHandler,
                          AuthenticationFailureHandler authenticationFailureHandler,
                          JwtUtils jwtUtils,
                          ObjectMapper objectMapper,
                          AppUserDetailsService userDetailsService) {
        this.authenticationProvider = authenticationProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.jwtUtils = jwtUtils;
        this.objectMapper = objectMapper;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll().and()
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
            .and().cors().and().csrf().disable()
            .addFilter(authenticationFilter())
            .addFilter(new JwtAuthorizationFilter(authenticationManager(), jwtUtils, objectMapper, userDetailsService))
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public JwtAuthenticationFilter authenticationFilter() throws Exception {
        final JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter(authenticationManager(),
                jwtUtils);
        authenticationFilter.setFilterProcessesUrl(SecurityConstants.SECURITY_CHECK_URI);
        authenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        authenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        return authenticationFilter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        // We're allowing all methods from all origins so that the application API is usable also by other clients
        // than just the UI.
        // This behavior can be restricted later.
        final CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
