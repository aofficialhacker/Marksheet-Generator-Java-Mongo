package com.example.marksheetgenerator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.example.marksheetgenerator.service.StudentUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final StudentUserDetailsService studentUserDetailsService;

        public SecurityConfig(StudentUserDetailsService studentUserDetailsService) {
                this.studentUserDetailsService = studentUserDetailsService;
        }

        // Teacher Security Filter Chain
        @Bean
        @Order(1)
        public SecurityFilterChain teacherSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/teacher/**", "/teacher/login", "/teacher/doLogin", "/teacher/logout")
                                .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("TEACHER"))
                                .formLogin(form -> form
                                                .loginPage("/teacher/login")
                                                .loginProcessingUrl("/teacher/doLogin")
                                                .defaultSuccessUrl("/teacher/addMarksheet", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutRequestMatcher(
                                                                new AntPathRequestMatcher("/teacher/logout", "GET"))
                                                .logoutSuccessUrl("/")
                                                .permitAll())
                                .userDetailsService(teacherUserDetailsManager());
                return http.build();
        }

        // Student Security Filter Chain
        @Bean
        @Order(2)
        public SecurityFilterChain studentSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/student/**", "/logout")
                                .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("STUDENT"))
                                .formLogin(form -> form
                                                .loginPage("/student/login")
                                                .loginProcessingUrl("/student/doLogin")
                                                .defaultSuccessUrl("/student/marksheet", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                                                .logoutSuccessUrl("/")
                                                .permitAll())
                                .authenticationProvider(studentDaoAuthenticationProvider());
                return http.build();
        }

        // Public Security Filter Chain
        @Bean
        @Order(3)
        public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
                http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
                return http.build();
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // Authentication provider for student logins
        @Bean
        public DaoAuthenticationProvider studentDaoAuthenticationProvider() {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
                provider.setUserDetailsService(studentUserDetailsService);
                provider.setPasswordEncoder(passwordEncoder());
                return provider;
        }

        // In-memory teacher details manager with three teacher users.
        @Bean
        public InMemoryUserDetailsManager teacherUserDetailsManager() {
                UserDetails teacher1 = User.builder()
                                .username("teacher1")
                                .password(passwordEncoder().encode("teacher1"))
                                .roles("TEACHER")
                                .build();
                UserDetails teacher2 = User.builder()
                                .username("teacher2")
                                .password(passwordEncoder().encode("teacher2"))
                                .roles("TEACHER")
                                .build();
                UserDetails teacher3 = User.builder()
                                .username("teacher3")
                                .password(passwordEncoder().encode("teacher3"))
                                .roles("TEACHER")
                                .build();
                return new InMemoryUserDetailsManager(teacher1, teacher2, teacher3);
        }
}
