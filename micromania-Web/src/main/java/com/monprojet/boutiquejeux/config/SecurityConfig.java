package com.monprojet.boutiquejeux.config;

import com.monprojet.boutiquejeux.service.ApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApiAuthenticationProvider apiAuthenticationProvider;
    private final ApiService apiService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(apiAuthenticationProvider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/catalogue/**", "/auth/**", "/css/**", "/js/**", "/img/**", "/panier/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/catalogue", true)
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessHandler(apiLogoutHandler())
                .invalidateHttpSession(true)
                .permitAll()
            );
        return http.build();
    }

    @Bean
    LogoutSuccessHandler apiLogoutHandler() {
        return (request, response, authentication) -> {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object jwt = session.getAttribute("jwt");
                if (jwt instanceof String token && !token.isBlank()) {
                    apiService.logout(token);
                }
                session.invalidate();
            }
            response.sendRedirect("/auth/login?logout");
        };
    }
}
