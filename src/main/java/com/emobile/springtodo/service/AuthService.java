package com.emobile.springtodo.service;

import com.emobile.springtodo.model.AuthRequest;
import com.emobile.springtodo.model.AuthResponse;
import com.emobile.springtodo.model.UserRegistrationRequest;

public interface AuthService {
    AuthResponse register(UserRegistrationRequest request);
    AuthResponse login(AuthRequest request);
}
