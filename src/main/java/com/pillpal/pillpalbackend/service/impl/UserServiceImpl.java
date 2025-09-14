package com.pillpal.pillpalbackend.service.impl;

import com.pillpal.pillpalbackend.entity.User;
import com.pillpal.pillpalbackend.exception.InvalidCredentialsException;
import com.pillpal.pillpalbackend.exception.UserAlreadyExistsException;
import com.pillpal.pillpalbackend.repository.UserRepository;
import com.pillpal.pillpalbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(String username, String email, String password) {
        // Check if username or email already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    @Override
    public User validateUserCredentials(String username, String rawPassword) {
        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("User doesn't exists"));

        // Verify password
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid Password");
        }

        return user;
    }

    @Override
    public User findById(String userId) {
        UUID uuid = UUID.fromString(userId);
        return userRepository.findById(uuid)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
    }
}
