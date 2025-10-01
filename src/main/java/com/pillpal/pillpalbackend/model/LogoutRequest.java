package com.pillpal.pillpalbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
    private String deviceId;       // device to log out from
    private String refreshToken;   // optional, extra safety
}