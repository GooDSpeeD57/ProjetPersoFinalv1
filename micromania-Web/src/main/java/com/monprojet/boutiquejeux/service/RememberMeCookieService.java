package com.monprojet.boutiquejeux.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

@Service
public class RememberMeCookieService {

    @Value("${app.remember-me.cookie-name:MICROMANIA_REMEMBER_ME}")
    private String cookieName;

    @Value("${app.remember-me.validity-days:30}")
    private int validityDays;

    public Optional<String> readRememberMeCookie(HttpServletRequest request) {
        if (request == null || request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
            .filter(cookie -> cookieName.equals(cookie.getName()))
            .findFirst()
            .map(Cookie::getValue)
            .filter(value -> value != null && !value.isBlank())
            .map(value -> URLDecoder.decode(value, StandardCharsets.UTF_8));
    }

    public void writeRememberMeCookie(HttpServletResponse response, String tokenValue) {
        if (response == null || tokenValue == null || tokenValue.isBlank()) {
            return;
        }

        Cookie cookie = new Cookie(cookieName, URLEncoder.encode(tokenValue, StandardCharsets.UTF_8));
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(validityDays * 24 * 60 * 60);
        response.addCookie(cookie);
    }

    public void clearRememberMeCookie(HttpServletResponse response) {
        if (response == null) {
            return;
        }

        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
