package com.example.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 정적 리소스와 인증 페이지를 가장 먼저 허용
                .requestMatchers(
                    "/css/**", "/js/**", "/images/**", "/favicon.ico",
                    "/auth/login", "/auth/signup"
                ).permitAll()
                
                // 게시판 조회는 누구나
                .requestMatchers("/", "/board", "/board/{id}").permitAll()
                
                // 글쓰기, 수정은 인증 필요
                .requestMatchers("/board/write", "/board/{id}/edit").authenticated()
                
                // API는 인증 필요
                .requestMatchers("/posts/**", "/comments/**", "/bookmarks/**").authenticated()
                
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/board", true)
                .failureUrl("/auth/login?error=true")
                .usernameParameter("username")  // 폼의 필드명과 일치
                .passwordParameter("password")  // 폼의 필드명과 일치
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/board")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/posts/**", "/comments/**", "/bookmarks/**")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}