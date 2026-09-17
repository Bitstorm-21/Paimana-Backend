package com.example.demo.security.services;

import com.example.demo.payload.AuthenticationResult;
import com.example.demo.payload.UserResponse;
import com.example.demo.security.request.LoginRequest;
import com.example.demo.security.request.SignupRequest;
import com.example.demo.security.response.MessageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseCookie;

public interface AuthService {
    AuthenticationResult login(LoginRequest loginRequest);

    MessageResponse register(SignupRequest signUpRequest);
String deleteUser(Long userId);

    ResponseCookie logout();
}
