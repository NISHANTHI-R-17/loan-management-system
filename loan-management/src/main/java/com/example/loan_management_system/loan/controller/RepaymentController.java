package com.example.loan_management_system.loan.controller;

import com.example.loan_management_system.auth.service.AuthService;
import com.example.loan_management_system.loan.dto.RepaymentRequest;
import com.example.loan_management_system.loan.dto.RepaymentResponse;
import com.example.loan_management_system.loan.service.RepaymentService;

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
public class RepaymentController {

    private final RepaymentService repaymentService;
    private final AuthService authService;

    public RepaymentController(
            RepaymentService repaymentService,
            AuthService authService) {

        this.repaymentService = repaymentService;
        this.authService = authService;
    }

    @PostMapping("/{loanId}/repayments")
    public ResponseEntity<?> makeRepayment(
            @PathVariable Long loanId,
            @Valid @RequestBody RepaymentRequest request,
            HttpSession session) {

        if (!authService.isAuthenticated(session)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(
                            false,
                            "User is not authenticated"));
        }

        try {
            RepaymentResponse response =
                    repaymentService.makeRepayment(
                            loanId,
                            request);

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

    @GetMapping("/{loanId}/repayments")
    public ResponseEntity<?> getRepaymentHistory(
            @PathVariable Long loanId,
            HttpSession session) {

        if (!authService.isAuthenticated(session)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(
                            false,
                            "User is not authenticated"));
        }

        try {
            List<RepaymentResponse> history =
                    repaymentService
                            .getRepaymentHistory(loanId);

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