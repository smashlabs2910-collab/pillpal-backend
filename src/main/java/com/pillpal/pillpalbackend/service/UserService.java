package com.pillpal.pillpalbackend.service;

import com.pillpal.pillpalbackend.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User saveUser(String username, String email, String password);
    User validateUserCredentials(String username, String rawPassword);
    User findById(String userId);
}
