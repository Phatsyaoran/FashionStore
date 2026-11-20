package com.fashion.store.service;

import com.fashion.store.dto.AuthResponse;
import com.fashion.store.dto.ChangePasswordRequest;
import com.fashion.store.dto.LoginRequest;
import com.fashion.store.dto.RegisterRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);

    void changePassword(String email, ChangePasswordRequest request);
}
