package com.pillpal.pillpalbackend.controller;


import com.pillpal.pillpalbackend.model.*;
import com.pillpal.pillpalbackend.repository.RefreshTokenRepository;
import com.pillpal.pillpalbackend.service.AuthenticationService;
import com.pillpal.pillpalbackend.service.RefreshTokenService;
import com.pillpal.pillpalbackend.service.UserService;
import com.pillpal.pillpalbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(UserService userService,
                                    RefreshTokenService refreshTokenService,
                                    JwtUtil jwtUtil,
                                    RefreshTokenRepository refreshTokenRepository,
                                    PasswordEncoder passwordEncoder, AuthenticationService authenticationService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<JwtResponse>> registerUser(@RequestBody RegisterRequest request, BindingResult result) {
        return ResponseEntity.ok(ApiResponse.success("User registered Successfully",authenticationService.registerUser(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("User logged in successfully",authenticationService.loginUser(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully",authenticationService.refreshToken(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody LogoutRequest request) {
        authenticationService.logoutUser(request);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully",null));
    }

    @PostMapping("/logout/all")
    public ResponseEntity<?> logoutAllDevices(@RequestBody LogoutAllDevicesRequest request) {
        authenticationService.logoutUserFromAllDevices(request);
        return ResponseEntity.ok(ApiResponse.success("Logged out from all devices successfully", null));
    }
}

