package com.pillpal.pillpalbackend.service.impl;

import com.pillpal.pillpalbackend.entity.RefreshToken;
import com.pillpal.pillpalbackend.entity.User;
import com.pillpal.pillpalbackend.exception.InvalidCredentialsException;
import com.pillpal.pillpalbackend.exception.InvalidRefreshTokenException;
import com.pillpal.pillpalbackend.model.*;
import com.pillpal.pillpalbackend.service.AuthenticationService;
import com.pillpal.pillpalbackend.service.RefreshTokenService;
import com.pillpal.pillpalbackend.service.UserService;
import com.pillpal.pillpalbackend.util.JwtUtil;
import com.pillpal.pillpalbackend.util.TokenHashUtil;
import com.pillpal.pillpalbackend.validator.RequestValidator;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final long REFRESH_TOKEN_EXPIRY_MS = 30L * 24 * 60 * 60 * 1000;

    private final RequestValidator requestValidator;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    public AuthenticationServiceImpl(RequestValidator requestValidator, UserService userService, RefreshTokenService refreshTokenService, JwtUtil jwtUtil) {
        this.requestValidator = requestValidator;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public JwtResponse registerUser(RegisterRequest registerRequest) {
        requestValidator.validateRegisterRequest(registerRequest);
        User user = userService.saveUser(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword()
        );
        return generateAndStoreTokens(user, registerRequest.getDeviceId());
    }

    @Override
    public JwtResponse loginUser(LoginRequest loginRequest) {
        requestValidator.validateLoginRequest(loginRequest);
        User user = userService.validateUserCredentials(loginRequest.getUsername(), loginRequest.getPassword());
        if (user == null) {
            throw new InvalidCredentialsException();
        }
        return generateAndStoreTokens(user, loginRequest.getDeviceId());
    }

    @Override
    public JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        requestValidator.validateRefreshTokenRequest(refreshTokenRequest);
        String refreshToken = refreshTokenRequest.getRefreshToken();
        String deviceId = refreshTokenRequest.getDeviceId();
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        Optional<RefreshToken> storedTokenOpt = refreshTokenService.validateRefreshTokenForDevice(refreshToken, UUID.fromString(userId), deviceId);
        RefreshToken storedToken = storedTokenOpt.orElseThrow(InvalidRefreshTokenException::new);
        return generateAndStoreTokens(storedToken.getUser(), deviceId);
    }

    @Override
    public void logoutUser(LogoutRequest logoutRequest) {
        requestValidator.validateLogoutRequest(logoutRequest);
        String refreshToken = logoutRequest.getRefreshToken();
        String deviceId = logoutRequest.getDeviceId();
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        User user = userService.findById(userId);
        if (user != null) {
            refreshTokenService.deleteByUserAndDeviceId(user, deviceId);
        }
    }

    @Override
    public void logoutUserFromAllDevices(LogoutAllDevicesRequest logoutAllDevicesRequest) {
        requestValidator.validateLogoutAllDevicesRequest(logoutAllDevicesRequest);
        String refreshToken = logoutAllDevicesRequest.getRefreshToken();
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        User user = userService.findById(userId);
        if (user != null) {
            refreshTokenService.deleteByUser(user);
        }
    }

    private JwtResponse generateAndStoreTokens(User user, String deviceId) {
        String userId = String.valueOf(user.getId());
        String accessToken = jwtUtil.generateAccessToken(userId);
        String refreshTokenRaw = jwtUtil.generateRefreshToken(userId);
        String hashedToken = TokenHashUtil.hashToken(refreshTokenRaw);
        refreshTokenService.createOrUpdateRefreshToken(user, deviceId, hashedToken, REFRESH_TOKEN_EXPIRY_MS);
        return new JwtResponse(accessToken, refreshTokenRaw, deviceId);
    }
}

