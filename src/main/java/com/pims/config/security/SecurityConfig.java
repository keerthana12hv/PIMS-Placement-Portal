package com.pims.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;


@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> {})
        .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()   // 🔥 IMPORTANT
                .requestMatchers("/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
                .requestMatchers("/api/student/**").hasAuthority("ROLE_STUDENT")
                .requestMatchers("/api/company/**").hasAuthority("ROLE_COMPANY")
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

    return http.build();
}


    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    //     http
    //         .csrf(csrf -> csrf.disable())
    //         .cors(cors -> {})
    //         .sessionManagement(session ->
    //                 session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    //         .authorizeHttpRequests(auth -> auth

    //             // ✅ Public endpoints
    //             .requestMatchers("/api/auth/**").permitAll()

    //             // ✅ VERY IMPORTANT — allow uploaded files
    //             .requestMatchers("/uploads/**").permitAll()

    //             // ✅ Swagger (for later admin testing)
    //             .requestMatchers(
    //                     "/v3/api-docs/**",
    //                     "/swagger-ui/**",
    //                     "/swagger-ui.html"
    //             ).permitAll()

    //             // ✅ Role based access
    //             .requestMatchers("/api/student/**").hasAuthority("ROLE_STUDENT")
    //             .requestMatchers("/api/company/**").hasAuthority("ROLE_COMPANY")

    //             .anyRequest().authenticated()
    //         )
    //         .addFilterBefore(jwtAuthenticationFilter,
    //                 UsernamePasswordAuthenticationFilter.class);

    //     return http.build();
    // }

    @Bean
public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers("/uploads/**");
}


}
