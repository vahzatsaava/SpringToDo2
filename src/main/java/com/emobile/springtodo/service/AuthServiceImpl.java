package com.emobile.springtodo.service;

import com.emobile.springtodo.entity.User;
import com.emobile.springtodo.model.AuthRequest;
import com.emobile.springtodo.model.AuthResponse;
import com.emobile.springtodo.model.UserRegistrationRequest;
import com.emobile.springtodo.repository.UserRepository;
import com.emobile.springtodo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(UserRegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        userRepository.save(user);

        String token = jwtUtil.generateToken(new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), new ArrayList<>())
        );
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), new ArrayList<>())
        );
        return new AuthResponse(token);
    }
}
