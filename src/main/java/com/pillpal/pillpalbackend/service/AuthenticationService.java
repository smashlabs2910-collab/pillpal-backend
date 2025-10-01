package com.pillpal.pillpalbackend.service;

import com.pillpal.pillpalbackend.entity.User;
import com.pillpal.pillpalbackend.model.*;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {
    JwtResponse registerUser(RegisterRequest registerRequest);

    JwtResponse loginUser(LoginRequest loginRequest);

    JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    void logoutUser(LogoutRequest logoutRequest);

    void logoutUserFromAllDevices(LogoutAllDevicesRequest logoutAllDevicesRequest);


}
