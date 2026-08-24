package com.autocare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/users/login",
                                "/users/register",
                                "/css/**",
                                "/images/**",
                                "/error"
                        ).permitAll()
                        .requestMatchers(
                                "/admin/**"
                        ).hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(form -> form
                        .loginPage(
                                "/users/login"
                        )
                        .loginProcessingUrl(
                                "/users/login"
                        )
                        .defaultSuccessUrl(
                                "/dashboard",
                                true
                        )
                        .failureUrl(
                                "/users/login?error"
                        )
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl(
                                "/users/logout"
                        )
                        .logoutSuccessUrl(
                                "/"
                        )
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}