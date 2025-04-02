package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Security configuration for the application
 * Configures OAuth2 login with Azure Entra ID and access rules
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain for HTTP requests
     * Defines URL access patterns, authentication, and logout behavior
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Configure which URLs require authentication
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/index.html", "/error/**").permitAll() // Public pages
                .anyRequest().authenticated()                                 // All others require login
            )
            // Configure OAuth2 login with Azure Entra ID
            .oauth2Login(oauth2 -> oauth2
                // After successful login, redirect to profile page
                .defaultSuccessUrl("/profile", true)
            )
            // Configure CSRF protection using cookies
            // withHttpOnlyFalse allows JavaScript to read the cookie
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            // Configure logout behavior
            .logout(logout -> logout
                .logoutSuccessUrl("/")        // Redirect to home page after logout
                .invalidateHttpSession(true)  // Invalidate HTTP session
                .clearAuthentication(true)    // Clear authentication information
                .deleteCookies("JSESSIONID")  // Remove session cookie
            );
        
        return http.build();
    }
}