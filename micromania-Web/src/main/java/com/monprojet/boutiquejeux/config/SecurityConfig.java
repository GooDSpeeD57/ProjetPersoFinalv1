package com.monprojet.boutiquejeux.config;

import com.monprojet.boutiquejeux.service.ApiService;
import com.monprojet.boutiquejeux.service.RememberMeCookieService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApiAuthenticationProvider apiAuthenticationProvider;
    private final ApiRememberMeAuthenticationFilter apiRememberMeAuthenticationFilter;
    private final ApiService apiService;
    private final RememberMeCookieService rememberMeCookieService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(apiAuthenticationProvider)
            .addFilterBefore(apiRememberMeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/catalogue/**", "/magasins/**", "/auth/**", "/css/**", "/js/**", "/img/**", "/panier/**"
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
            String rememberMeToken = rememberMeCookieService.readRememberMeCookie(request).orElse(null);

            HttpSession session = request.getSession(false);
            if (session != null) {
                Object jwt = session.getAttribute("jwt");
                if (jwt instanceof String token && !token.isBlank()) {
                    apiService.logout(token, rememberMeToken);
                } else if (rememberMeToken != null && !rememberMeToken.isBlank()) {
                    apiService.logout(null, rememberMeToken);
                }
                session.invalidate();
            } else if (rememberMeToken != null && !rememberMeToken.isBlank()) {
                apiService.logout(null, rememberMeToken);
            }

            rememberMeCookieService.clearRememberMeCookie(response);
            response.sendRedirect("/auth/login?logout");
        };
    }
}
