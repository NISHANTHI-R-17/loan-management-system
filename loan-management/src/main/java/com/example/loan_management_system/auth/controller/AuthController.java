package com.example.loan_management_system.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.loan_management_system.auth.dto.LoginRequest;
import com.example.loan_management_system.auth.dto.LoginResponse;
import com.example.loan_management_system.auth.service.AuthService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpSession session) {

        LoginResponse response =
                authService.login(request, session);

        if (!response.isSuccess()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponse> currentUser(
            HttpSession session) {

        if (!authService.isAuthenticated(session)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new LoginResponse(
                                    false,
                                    "User is not authenticated"
                            )
                    );
        }

        return ResponseEntity.ok(
                new LoginResponse(
                        true,
                        "admin"
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logout(
            HttpSession session) {

        authService.logout(session);

        return ResponseEntity.ok(
                new LoginResponse(
                        true,
                        "Logout successful"
                )
        );
    }
}