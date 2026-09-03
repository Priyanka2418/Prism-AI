package com.aimock.interview.auth.service;

import com.aimock.interview.auth.dto.AuthResponse;
import com.aimock.interview.auth.dto.LoginRequest;
import com.aimock.interview.auth.dto.RefreshTokenRequest;
import com.aimock.interview.auth.entity.RefreshToken;
import com.aimock.interview.auth.repository.RefreshTokenRepository;
import com.aimock.interview.auth.security.JwtService;
import com.aimock.interview.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
    public AuthResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()));

        User user =
                (User) authentication.getPrincipal();

        return authenticate(user);
    }

    @Override
    public AuthResponse refreshToken(
            String refreshToken) {

        RefreshToken storedToken =
                refreshTokenRepository.findByToken(refreshToken)
                        .orElseThrow(
                                () -> new BadCredentialsException(
                                        "Invalid refresh token"
                                )
                        );

        if (storedToken.isRevoked()) {
            throw new BadCredentialsException(
                    "Refresh token has been revoked"
            );
        }

        if (storedToken.getExpiresAt()
                .isBefore(Instant.now())) {

            throw new BadCredentialsException(
                    "Refresh token has expired"
            );
        }

        String username =
                jwtService.extractUsername(refreshToken);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(username);

        if (!jwtService.isTokenValid(
                refreshToken,
                userDetails)) {

            throw new BadCredentialsException(
                    "Invalid refresh token"
            );
        }

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }

    @Override
    public AuthResponse authenticate(User user) {

        String accessToken =
                jwtService.generateAccessToken(user);

        String refreshToken =
                jwtService.generateRefreshToken(user);

        RefreshToken refreshTokenEntity =
                RefreshToken.builder()
                        .token(refreshToken)
                        .user(user)
                        .expiresAt(Instant.now().plusMillis(refreshTokenExpiration))
                        .revoked(false)
                        .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public void logout(User user) {

        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUser(user);

        refreshTokens.forEach(token -> token.setRevoked(true));

        refreshTokenRepository.saveAll(refreshTokens);
    }
}