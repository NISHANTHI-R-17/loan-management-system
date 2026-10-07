package com.example.loan_management_system.loan.controller;

import com.example.loan_management_system.auth.service.AuthService;
import com.example.loan_management_system.loan.dto.LoanRequest;
import com.example.loan_management_system.loan.dto.LoanResponse;
import com.example.loan_management_system.loan.service.LoanService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
public class LoanController {

    private final LoanService loanService;
    private final AuthService authService;

    public LoanController(
            LoanService loanService,
            AuthService authService) {

        this.loanService = loanService;
        this.authService = authService;
    }

    // =========================================================
    // CREATE LOAN
    // POST /api/loans
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createLoan(
            @Valid @RequestBody LoanRequest request,
            HttpSession session) {

        // Check authentication
        if (!authService.isAuthenticated(session)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new ErrorResponse(
                                    false,
                                    "User is not authenticated"
                            )
                    );
        }

        LoanResponse response =
                loanService.createLoan(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // GET ALL LOANS
    // GET /api/loans
    // =========================================================

    @GetMapping
    public ResponseEntity<?> getAllLoans(
            HttpSession session) {

        // Check authentication
        if (!authService.isAuthenticated(session)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new ErrorResponse(
                                    false,
                                    "User is not authenticated"
                            )
                    );
        }

        List<LoanResponse> loans =
                loanService.getAllLoans();

        return ResponseEntity.ok(loans);
    }

    // =========================================================
    // ERROR RESPONSE
    // =========================================================

    public static class ErrorResponse {

        private boolean success;
        private String message;

        public ErrorResponse(
                boolean success,
                String message) {

            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}