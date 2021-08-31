package com.reddit.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.reddit.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID>, JpaSpecificationExecutor<RefreshToken> {

  Optional<RefreshToken> findByToken(String token);

  void deleteByToken(String token);
}