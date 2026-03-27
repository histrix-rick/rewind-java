package com.rewindai.app.config;

import com.rewindai.common.security.config.JwtProperties;
import com.rewindai.common.security.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.common.core.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 * 前端API Spring Security配置
 *
 * @author Rewind.ai Team
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class AppSecurityConfig {

    private final AppJwtAuthenticationEntryPoint unauthorizedHandler;
    private final AppJwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] WHITE_LIST = {
            "/health",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/error",
            "/api/auth/**",
            "/api/dreams/public/**",
            "/api/dream-meta/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST).permitAll()
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Slf4j
    @Component
    @RequiredArgsConstructor
    public static class AppJwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

        private final ObjectMapper objectMapper;

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException {
            log.warn("未授权访问: {}", authException.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            Result<Void> result = Result.error(ErrorCode.UNAUTHORIZED);
            response.getWriter().write(objectMapper.writeValueAsString(result));
        }
    }

    @Slf4j
    @Component
    @RequiredArgsConstructor
    public static class AppJwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtUtil jwtUtil;
        private final JwtProperties jwtProperties;

        @Override
        protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
            try {
                String jwt = getJwtFromRequest(request);

                if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                    String userIdStr = jwtUtil.getUserIdAsStringFromToken(jwt);
                    String username = jwtUtil.getUsernameFromToken(jwt);
                    String userType = jwtUtil.getUserTypeFromToken(jwt);

                    // 前端API只允许app类型token
                    if (JwtUtil.TYPE_APP.equals(userType)) {
                        org.springframework.security.authentication.UsernamePasswordAuthenticationToken authentication =
                                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                        userIdStr,
                                        null,
                                        Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
                                );
                        authentication.setDetails(new org.springframework.security.web.authentication.WebAuthenticationDetailsSource().buildDetails(request));
                        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);

                        log.debug("用户 {} 认证成功", username);
                    }
                }
            } catch (Exception ex) {
                log.error("无法设置用户认证: {}", ex.getMessage());
            }

            filterChain.doFilter(request, response);
        }

        private String getJwtFromRequest(HttpServletRequest request) {
            String bearerToken = request.getHeader(jwtProperties.getHeader());
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtProperties.getPrefix() + " ")) {
                return bearerToken.substring(jwtProperties.getPrefix().length() + 1);
            }
            return null;
        }
    }
}
