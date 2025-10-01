package com.pillpal.pillpalbackend.repository;

import com.pillpal.pillpalbackend.entity.RefreshToken;
import com.pillpal.pillpalbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserAndDeviceId(User user, String deviceId);

    Optional<RefreshToken> findByDeviceId(String deviceId);

    void deleteByUserAndDeviceId(User user, String deviceId);

    void deleteByUser(User user); // For logout all devices
}
