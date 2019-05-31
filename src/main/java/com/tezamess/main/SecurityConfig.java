package com.tezamess.main;

import com.tezamess.authentication.CustomAccessDeniedHandler;
import com.tezamess.authentication.JwtAuthenticationTokenFilter;
import com.tezamess.authentication.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() throws Exception {
        JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter = new JwtAuthenticationTokenFilter();
        jwtAuthenticationTokenFilter.setAuthenticationManager(authenticationManager());
        return jwtAuthenticationTokenFilter;
    }

    @Bean
    public RestAuthenticationEntryPoint restServicesEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Disable crsf cho đường dẫn /appchat/**
        http.csrf().ignoringAntMatchers("/tezamess/api/**");

        http.authorizeRequests()
                .antMatchers("/", "/tezamess", "/tezamess/api-login", "/tezamess/api-register", "/tezamess/api-users", "tezamess/api-user/{id}")
                .permitAll();

        http.antMatcher("/tezamess/api/**").httpBasic().authenticationEntryPoint(restServicesEntryPoint()).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
                .antMatchers(HttpMethod.GET, "/tezamess/api/**").access("hasRole('ROLE_USER')")
                .antMatchers(HttpMethod.POST, "/tezamess/api/**").access("hasRole('ROLE_USER')")
                .antMatchers(HttpMethod.PUT, "/tezamess/api/**").access("hasRole('ROLE_USER')")
                .antMatchers(HttpMethod.DELETE, "/tezamess/api/**").access("hasRole('ROLE_USER')").and()
                .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler());
    }
}
