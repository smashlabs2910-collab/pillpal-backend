package com.pillpal.pillpalbackend.validator;

import com.pillpal.pillpalbackend.exception.ValidationException;
import com.pillpal.pillpalbackend.model.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestValidator {

    public void validateRegisterRequest(RegisterRequest request) {
        Errors errors = new BeanPropertyBindingResult(request, "registerRequest");

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            errors.rejectValue("username", "username.empty", "Username is required");
        }

        if (request.getEmail() == null || !request.getEmail().matches("^[^@]+@[^@]+\\.[^@]+$")) {
            errors.rejectValue("email", "email.invalid", "Valid email is required");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            errors.rejectValue("password", "password.weak", "Password must be at least 6 characters");
        }

        if (request.getDeviceId() == null || request.getDeviceId().trim().isEmpty()) {
            errors.rejectValue("deviceId", "deviceId.empty", "Device ID is required");
        }

        handleErrors(errors);
    }

    public void validateLoginRequest(LoginRequest request) {
        Errors errors = new BeanPropertyBindingResult(request, "loginRequest");

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            errors.rejectValue("username", "username.empty", "Username is required");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            errors.rejectValue("password", "password.empty", "Password is required");
        }

        if (request.getDeviceId() == null || request.getDeviceId().trim().isEmpty()) {
            errors.rejectValue("deviceId", "deviceId.empty", "Device ID is required");
        }

        handleErrors(errors);
    }

    public void validateRefreshTokenRequest(RefreshTokenRequest request) {
        Errors errors = new BeanPropertyBindingResult(request, "refreshTokenRequest");

        if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            errors.rejectValue("refreshToken", "refreshToken.empty", "Refresh token is required");
        }

        if (request.getDeviceId() == null || request.getDeviceId().trim().isEmpty()) {
            errors.rejectValue("deviceId", "deviceId.empty", "Device ID is required");
        }

        handleErrors(errors);
    }

    public void validateLogoutRequest(LogoutRequest request) {
        Errors errors = new BeanPropertyBindingResult(request, "logoutRequest");

        if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            errors.rejectValue("refreshToken", "refreshToken.empty", "Refresh token is required");
        }

        if (request.getDeviceId() == null || request.getDeviceId().trim().isEmpty()) {
            errors.rejectValue("deviceId", "deviceId.empty", "Device ID is required");
        }

        handleErrors(errors);
    }

    public void validateLogoutAllDevicesRequest(LogoutAllDevicesRequest request) {
        Errors errors = new BeanPropertyBindingResult(request, "logoutAllDevicesRequest");

        if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
            errors.rejectValue("refreshToken", "refreshToken.empty", "Refresh token is required");
        }

        handleErrors(errors);
    }

    private void handleErrors(Errors errors) {
        if (errors.hasErrors()) {
            List<String> errorMessages = errors.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new ValidationException("Validation failed", errorMessages);
        }
    }
}

