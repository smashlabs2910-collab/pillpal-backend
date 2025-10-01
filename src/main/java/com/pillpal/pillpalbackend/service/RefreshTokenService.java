package com.pillpal.pillpalbackend.service;

import com.pillpal.pillpalbackend.entity.RefreshToken;
import com.pillpal.pillpalbackend.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenService {

    /**
     * Create or update a refresh token for a specific user and device.
     *
     * @param user the user
     * @param deviceId the device identifier
     * @param token the hashed refresh token
     * @param durationInMs token validity duration
     * @return the saved RefreshToken entity
     */
    RefreshToken createOrUpdateRefreshToken(User user, String deviceId, String token, long durationInMs);

    /**
     * Validate a refresh token across all devices.
     *
     * @param token the raw refresh token
     * @return Optional of RefreshToken if valid
     */
    Optional<RefreshToken> validateRefreshToken(String token);

    /**
     * Validate a refresh token for a specific device.
     *
     * @param token the raw refresh token
     * @param deviceId the device identifier
     * @return Optional of RefreshToken if valid
     */
    Optional<RefreshToken> validateRefreshTokenForDevice(String token, UUID userId, String deviceId);

    /**
     * Delete all refresh tokens for a user.
     *
     * @param user the user
     */
    void deleteByUser(User user);

    /**
     * Rotate (update) a refresh token for a specific device.
     *
     * @param existingToken the existing token entity
     * @param durationInMs new validity duration
     * @return the updated RefreshToken
     */
    RefreshToken rotateRefreshToken(RefreshToken existingToken, long durationInMs);


    void deleteByUserAndDeviceId(User user, String deviceId);
    void deleteAllByUser(User user);
}
