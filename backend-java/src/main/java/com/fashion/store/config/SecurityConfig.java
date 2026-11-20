package com.fashion.store.config;

import com.fashion.store.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Ngăn Spring Boot tự động đăng ký JwtAuthenticationFilter ở tầng Servlet (chạy trước SecurityContextHolderFilter).
     * Đảm bảo filter CHỈ chạy đúng 1 lần BÊN TRONG SecurityFilterChain.
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilter(corsFilter)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"success\":false,\"message\":\"Vui lòng đăng nhập để thực hiện thao tác này.\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"success\":false,\"message\":\"Bạn không có quyền truy cập chức năng quản trị.\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // Cho phép CORS preflight OPTIONS đi qua không bị chặn 401
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Authenticated customer endpoints (Orders & Change Password)
                        .requestMatchers(
                                "/api/orders/**",
                                "/api/auth/change-password"
                        ).authenticated()
                        // Public endpoints
                        .requestMatchers(
                                "/api/categories/**",
                                "/api/products/**",
                                "/api/auth/**",
                                "/api/coupons/apply",
                                "/api/chat-assistant/**",
                                "/uploads/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/error"
                        ).permitAll()
                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
