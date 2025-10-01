package com.pillpal.pillpalbackend.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutAllDevicesRequest {
    private String refreshToken;
}
