package com.example.marksheetgenerator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Permit CSS, JS, images, login page, and root (/) so no loop
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**", "/login", "/",
                                "/marksheets/export")
                        .permitAll()
                        // All other URLs require authentication
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page
                        .permitAll() // Everyone can see the login page
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }

    /**
     * In-memory authentication for testing.
     * Username: user
     * Password: password
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Configuration
    public class WebConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:src/main/resources/static/uploads/");
        }
    }
}
