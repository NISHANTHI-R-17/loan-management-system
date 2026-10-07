package com.example.loan_management_system.auth.service;

import org.springframework.stereotype.Service;

import com.example.loan_management_system.auth.dto.LoginRequest;
import com.example.loan_management_system.auth.dto.LoginResponse;

import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";

    public LoginResponse login(LoginRequest request, HttpSession session) {

        if (request == null
                || request.getUsername() == null
                || request.getPassword() == null) {

            return new LoginResponse(
                    false,
                    "Username and password are required"
            );
        }

        if (DEFAULT_USERNAME.equals(request.getUsername())
                && DEFAULT_PASSWORD.equals(request.getPassword())) {

            session.setAttribute("authenticated", true);
            session.setAttribute("username", DEFAULT_USERNAME);

            return new LoginResponse(
                    true,
                    "Login successful"
            );
        }

        return new LoginResponse(
                false,
                "Invalid username or password"
        );
    }

    public boolean isAuthenticated(HttpSession session) {

        Boolean authenticated =
                (Boolean) session.getAttribute("authenticated");

        return Boolean.TRUE.equals(authenticated);
    }

    public void logout(HttpSession session) {

        session.invalidate();
    }
}