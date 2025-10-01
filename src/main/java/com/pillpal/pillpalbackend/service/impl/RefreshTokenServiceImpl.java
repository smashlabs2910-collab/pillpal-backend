package com.pillpal.pillpalbackend.service.impl;

import com.pillpal.pillpalbackend.entity.RefreshToken;
import com.pillpal.pillpalbackend.entity.User;
import com.pillpal.pillpalbackend.repository.RefreshTokenRepository;
import com.pillpal.pillpalbackend.service.RefreshTokenService;
import com.pillpal.pillpalbackend.util.TokenHashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
                                   PasswordEncoder passwordEncoder) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RefreshToken createOrUpdateRefreshToken(User user, String deviceId, String hashedToken, long durationInMs) {
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUserAndDeviceId(user, deviceId);

        RefreshToken refreshToken;
        if (existingTokenOpt.isPresent()) {
            // Update existing token with new hashed value + expiry
            refreshToken = existingTokenOpt.get();
            refreshToken.setToken(hashedToken);
            refreshToken.setExpiryDate(Instant.now().plusMillis(durationInMs));
        } else {
            // Create new token for device
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setDeviceId(deviceId);
            refreshToken.setToken(hashedToken);
            refreshToken.setExpiryDate(Instant.now().plusMillis(durationInMs));
        }
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> validateRefreshToken(String token) {
        return refreshTokenRepository.findAll().stream()
                .filter(rt -> passwordEncoder.matches(token, rt.getToken()))
                .findFirst()
                .filter(rt -> rt.getExpiryDate().isAfter(Instant.now()));
    }

    @Override
    public Optional<RefreshToken> validateRefreshTokenForDevice(String token, UUID userId, String deviceId) {
        String hashedToken = TokenHashUtil.hashToken(token);

        return refreshTokenRepository.findByDeviceId(deviceId)
                .stream()
                .filter(rt -> rt.getToken().equals(hashedToken))
                .filter(rt -> rt.getExpiryDate().isAfter(Instant.now()))
                .filter(rt -> rt.getUser().getId().equals(userId)) // UUID comparison
                .findFirst();
    }

    @Override
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    @Override
    public RefreshToken rotateRefreshToken(RefreshToken existingToken, long durationInMs) {
        existingToken.setToken(passwordEncoder.encode(existingToken.getToken()));
        existingToken.setExpiryDate(Instant.now().plusMillis(durationInMs));
        return refreshTokenRepository.save(existingToken);
    }

    @Override
    public void deleteByUserAndDeviceId(User user, String deviceId) {
        refreshTokenRepository.deleteByUserAndDeviceId(user, deviceId);
    }

    @Override
    public void deleteAllByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
