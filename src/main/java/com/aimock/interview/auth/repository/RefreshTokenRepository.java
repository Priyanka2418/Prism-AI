package com.aimock.interview.auth.repository;

import com.aimock.interview.auth.entity.RefreshToken;
import com.aimock.interview.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUser(User user);
}