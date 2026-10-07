package com.example.loan_management_system.loan.controller;

import com.example.loan_management_system.auth.service.AuthService;
import com.example.loan_management_system.loan.dto.AdditionalPaymentRequest;
import com.example.loan_management_system.loan.dto.AdditionalPaymentResponse;
import com.example.loan_management_system.loan.service.AdditionalPaymentService;

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
public class AdditionalPaymentController {

    private final AdditionalPaymentService additionalPaymentService;
    private final AuthService authService;

    public AdditionalPaymentController(
            AdditionalPaymentService additionalPaymentService,
            AuthService authService) {

        this.additionalPaymentService = additionalPaymentService;
        this.authService = authService;
    }

    @PostMapping("/{loanId}/additional-payments")
    public ResponseEntity<?> makeAdditionalPayment(
            @PathVariable Long loanId,
            @Valid @RequestBody AdditionalPaymentRequest request,
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

            AdditionalPaymentResponse response =
                    additionalPaymentService
                            .makeAdditionalPayment(loanId, request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (RuntimeException exception) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(
                            false,
                            exception.getMessage()));
        }
    }

    @GetMapping("/{loanId}/additional-payments")
    public ResponseEntity<?> getAdditionalPaymentHistory(
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

            List<AdditionalPaymentResponse> history =
                    additionalPaymentService
                            .getAdditionalPaymentHistory(loanId);

            return ResponseEntity.ok(history);

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