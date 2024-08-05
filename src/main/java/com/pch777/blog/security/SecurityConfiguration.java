package com.pch777.blog.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableMethodSecurity
@Configuration
public class SecurityConfiguration {

    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        String[] whiteList = {"/", "/login", "/signup",
                "/resend-token", "/email/verify", "/password/**",
                "/search", "/articles/*", "/articles/*/*",
                "/authors/*/articles", "/categories/*/articles", "/tags/*/articles",
                "/css/**", "/js/**", "/img/**", "/plugins/**", "/error/**"};

        String[] whiteApiList = {"/api/v1/auth/signup",
                "/api/v1/auth/resend-token", "/api/v1/auth/email/verify", "/api/v1/auth/password/**"
                //"/api/v1/articles/*/comments"
        };

        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(whiteList).permitAll()
                        .requestMatchers(whiteApiList).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/*/comments").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/articles/*/comments").hasAuthority("COMMENT_CREATE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/articles/*/comments/*").hasAuthority("COMMENT_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/articles/*/comments/*").hasAuthority("COMMENT_DELETE")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/author/**").hasAnyRole("ADMIN", "AUTHOR")
                        .requestMatchers("/reader/**").hasAnyRole("READER")
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .csrf(AbstractHttpConfigurer::disable)

                //   .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard")
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
