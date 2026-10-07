package com.example.loan_management_system.loan.controller;

import com.example.loan_management_system.auth.service.AuthService;
import com.example.loan_management_system.loan.dto.EmiScheduleResponse;
import com.example.loan_management_system.loan.service.EmiScheduleService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class EmiScheduleController {

    private final EmiScheduleService emiScheduleService;
    private final AuthService authService;

    public EmiScheduleController(
            EmiScheduleService emiScheduleService,
            AuthService authService) {

        this.emiScheduleService = emiScheduleService;
        this.authService = authService;
    }

    @PostMapping("/{loanId}/schedule")
    public ResponseEntity<?> generateSchedule(
            @PathVariable Long loanId,
            HttpSession session) {

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

        List<EmiScheduleResponse> schedule =
                emiScheduleService.generateSchedule(loanId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(schedule);
    }

    @GetMapping("/{loanId}/schedule")
    public ResponseEntity<?> getSchedule(
            @PathVariable Long loanId,
            HttpSession session) {

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

        List<EmiScheduleResponse> schedule =
                emiScheduleService.getSchedule(loanId);

        return ResponseEntity.ok(schedule);
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