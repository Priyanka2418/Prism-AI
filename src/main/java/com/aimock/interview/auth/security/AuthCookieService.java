package com.aimock.interview.auth.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AuthCookieService {

    private static final String ACCESS_TOKEN_COOKIE =
            "access_token";

    private static final String REFRESH_TOKEN_COOKIE =
            "refresh_token";

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Value("${cookie.same-site}")
    private String cookieSameSite;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public void addAccessTokenCookie(
            HttpServletResponse response,
            String accessToken) {

        ResponseCookie cookie =
                ResponseCookie.from(
                                ACCESS_TOKEN_COOKIE,
                                accessToken
                        )
                        .httpOnly(true)
                        .secure(cookieSecure)
                        .sameSite(cookieSameSite)
                        .path("/")
                        .build();

        response.addHeader(
                "Set-Cookie",
                cookie.toString()
        );
    }

    public void addRefreshTokenCookie(
            HttpServletResponse response,
            String refreshToken) {

        ResponseCookie cookie =
                ResponseCookie.from(
                                REFRESH_TOKEN_COOKIE,
                                refreshToken
                        )
                        .httpOnly(true)
                        .secure(cookieSecure)
                        .sameSite(cookieSameSite)
                        .path("/api/v1/auth/refresh")
                        .maxAge(
                                Duration.ofMillis(
                                        refreshTokenExpiration
                                )
                        )
                        .build();

        response.addHeader(
                "Set-Cookie",
                cookie.toString()
        );
    }

    public void clearAuthCookies(
            HttpServletResponse response) {

        ResponseCookie accessCookie =
                ResponseCookie.from(
                                ACCESS_TOKEN_COOKIE,
                                ""
                        )
                        .httpOnly(true)
                        .secure(cookieSecure)
                        .sameSite(cookieSameSite)
                        .path("/")
                        .maxAge(0)
                        .build();

        ResponseCookie refreshCookie =
                ResponseCookie.from(
                                REFRESH_TOKEN_COOKIE,
                                ""
                        )
                        .httpOnly(true)
                        .secure(cookieSecure)
                        .sameSite(cookieSameSite)
                        .path("/api/v1/auth/refresh")
                        .maxAge(0)
                        .build();

        response.addHeader(
                "Set-Cookie",
                accessCookie.toString()
        );

        response.addHeader(
                "Set-Cookie",
                refreshCookie.toString()
        );
    }
}