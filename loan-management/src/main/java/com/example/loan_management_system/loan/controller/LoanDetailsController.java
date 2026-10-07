package com.example.loan_management_system.loan.controller;

import com.example.loan_management_system.auth.service.AuthService;
import com.example.loan_management_system.loan.dto.LoanDetailsResponse;
import com.example.loan_management_system.loan.service.LoanDetailsService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
public class LoanDetailsController {

    private final LoanDetailsService loanDetailsService;
    private final AuthService authService;

    public LoanDetailsController(
            LoanDetailsService loanDetailsService,
            AuthService authService) {

        this.loanDetailsService = loanDetailsService;
        this.authService = authService;
    }

    @GetMapping("/{loanId}/details")
    public ResponseEntity<?> getLoanDetails(
            @PathVariable Long loanId,
            HttpSession session) {

        // Check authentication
        if (!authService.isAuthenticated(session)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(
                            false,
                            "User is not authenticated"));
        }

        try {

            LoanDetailsResponse response =
                    loanDetailsService
                            .getLoanDetails(loanId);

            return ResponseEntity.ok(response);

        } catch (RuntimeException exception) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            false,
                            exception.getMessage()));
        }
    }

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