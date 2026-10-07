package com.example.loan_management_system.loan.service;

import com.example.loan_management_system.loan.dto.EmiScheduleResponse;
import com.example.loan_management_system.loan.entity.EmiSchedule;
import com.example.loan_management_system.loan.entity.EmiStatus;
import com.example.loan_management_system.loan.entity.Loan;
import com.example.loan_management_system.loan.repository.EmiScheduleRepository;
import com.example.loan_management_system.loan.repository.LoanRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class EmiScheduleService {

    private static final int MONEY_SCALE = 2;
    private static final int CALCULATION_SCALE = 10;

    private final LoanRepository loanRepository;
    private final EmiScheduleRepository emiScheduleRepository;

    public EmiScheduleService(
            LoanRepository loanRepository,
            EmiScheduleRepository emiScheduleRepository) {

        this.loanRepository = loanRepository;
        this.emiScheduleRepository = emiScheduleRepository;
    }

    // =========================================================
    // GENERATE EMI SCHEDULE
    // =========================================================

    @Transactional
    public List<EmiScheduleResponse> generateSchedule(Long loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Loan not found with id: " + loanId));

        // Check if schedule already exists
        List<EmiSchedule> existingSchedule =
                emiScheduleRepository
                        .findByLoanIdOrderByEmiNumberAsc(loanId);

        if (!existingSchedule.isEmpty()) {

            updateOverdueStatuses(existingSchedule);

            return existingSchedule.stream()
                    .map(this::convertToResponse)
                    .toList();
        }

        // =====================================================
        // INITIAL PRINCIPAL
        // =====================================================

        BigDecimal principal =
                loan.getLoanAmount()
                        .setScale(
                                MONEY_SCALE,
                                RoundingMode.HALF_UP);

        // =====================================================
        // MONTHLY INTEREST RATE
        // =====================================================

        BigDecimal monthlyRate =
                loan.getAnnualInterestRate()
                        .divide(
                                BigDecimal.valueOf(100),
                                CALCULATION_SCALE,
                                RoundingMode.HALF_UP)
                        .divide(
                                BigDecimal.valueOf(12),
                                CALCULATION_SCALE,
                                RoundingMode.HALF_UP);

        // =====================================================
        // CALCULATE EMI
        // =====================================================

        BigDecimal emiAmount;

        // 0% interest
        if (loan.getAnnualInterestRate()
                .compareTo(BigDecimal.ZERO) == 0) {

            emiAmount =
                    principal.divide(
                            BigDecimal.valueOf(
                                    loan.getLoanTenureMonths()),
                            MONEY_SCALE,
                            RoundingMode.HALF_UP);

        } else {

            BigDecimal onePlusRate =
                    BigDecimal.ONE.add(monthlyRate);

            BigDecimal power =
                    onePlusRate.pow(
                            loan.getLoanTenureMonths());

            emiAmount =
                    principal
                            .multiply(monthlyRate)
                            .multiply(power)
                            .divide(
                                    power.subtract(
                                            BigDecimal.ONE),
                                    MONEY_SCALE,
                                    RoundingMode.HALF_UP);
        }

        // =====================================================
        // GENERATE SCHEDULE
        // =====================================================

        BigDecimal openingPrincipal = principal;

        LocalDate dueDate = loan.getStartDate();

        for (int i = 1;
             i <= loan.getLoanTenureMonths();
             i++) {

            // Calculate next due date
            dueDate = dueDate.plusMonths(1);

            // =================================================
            // INTEREST CALCULATION
            // =================================================

            BigDecimal interestAmount;

            if (loan.getAnnualInterestRate()
                    .compareTo(BigDecimal.ZERO) == 0) {

                interestAmount =
                        BigDecimal.ZERO
                                .setScale(MONEY_SCALE);

            } else {

                interestAmount =
                        openingPrincipal
                                .multiply(monthlyRate)
                                .setScale(
                                        MONEY_SCALE,
                                        RoundingMode.HALF_UP);
            }

            // =================================================
            // PRINCIPAL COMPONENT
            // =================================================

            BigDecimal principalAmount =
                    emiAmount
                            .subtract(interestAmount)
                            .setScale(
                                    MONEY_SCALE,
                                    RoundingMode.HALF_UP);

            // Prevent principal from exceeding balance
            if (principalAmount.compareTo(
                    openingPrincipal) > 0) {

                principalAmount = openingPrincipal;
            }

            // =================================================
            // REMAINING PRINCIPAL
            // =================================================

            BigDecimal remainingPrincipal =
                    openingPrincipal
                            .subtract(principalAmount)
                            .setScale(
                                    MONEY_SCALE,
                                    RoundingMode.HALF_UP);

            if (remainingPrincipal.compareTo(
                    BigDecimal.ZERO) < 0) {

                remainingPrincipal =
                        BigDecimal.ZERO
                                .setScale(MONEY_SCALE);
            }

            // =================================================
            // FINAL EMI ADJUSTMENT
            // =================================================

            BigDecimal currentEmiAmount = emiAmount;

            if (i == loan.getLoanTenureMonths()
                    && remainingPrincipal.compareTo(
                            BigDecimal.ZERO) != 0) {

                principalAmount = openingPrincipal;

                currentEmiAmount =
                        principalAmount
                                .add(interestAmount)
                                .setScale(
                                        MONEY_SCALE,
                                        RoundingMode.HALF_UP);

                remainingPrincipal =
                        BigDecimal.ZERO
                                .setScale(MONEY_SCALE);
            }

            // =================================================
            // CREATE EMI SCHEDULE RECORD
            // =================================================

            EmiSchedule schedule = new EmiSchedule();

            schedule.setLoan(loan);

            schedule.setEmiNumber(i);

            schedule.setDueDate(dueDate);

            schedule.setEmiAmount(currentEmiAmount);

            schedule.setInterestAmount(interestAmount);

            schedule.setPrincipalAmount(principalAmount);

            schedule.setRemainingPrincipal(
                    remainingPrincipal);

            schedule.setStatus(EmiStatus.PENDING);

            emiScheduleRepository.save(schedule);

            // Next EMI starts with this principal
            openingPrincipal = remainingPrincipal;
        }

        // =====================================================
        // RETURN SCHEDULE
        // =====================================================

        List<EmiSchedule> schedule =
                emiScheduleRepository
                        .findByLoanIdOrderByEmiNumberAsc(
                                loanId);

        updateOverdueStatuses(schedule);

        return schedule.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // GET LOAN SCHEDULE
    // =========================================================

    @Transactional
    public List<EmiScheduleResponse> getSchedule(
            Long loanId) {

        if (!loanRepository.existsById(loanId)) {

            throw new RuntimeException(
                    "Loan not found with id: " + loanId);
        }

        List<EmiSchedule> schedules =
                emiScheduleRepository
                        .findByLoanIdOrderByEmiNumberAsc(
                                loanId);

        // Update overdue statuses
        updateOverdueStatuses(schedules);

        return schedules.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // UPDATE EMI STATUS
    // =========================================================

    private void updateOverdueStatuses(
            List<EmiSchedule> schedules) {

        LocalDate today = LocalDate.now();

        for (EmiSchedule schedule : schedules) {

            // Already paid → don't change
            if (schedule.getStatus() == EmiStatus.PAID) {
                continue;
            }

            // Due date has passed
            if (schedule.getDueDate().isBefore(today)) {

                schedule.setStatus(
                        EmiStatus.OVERDUE);

            } else {

                schedule.setStatus(
                        EmiStatus.PENDING);
            }
        }

        emiScheduleRepository.saveAll(schedules);
    }

    // =========================================================
    // CONVERT ENTITY TO RESPONSE
    // =========================================================

    private EmiScheduleResponse convertToResponse(
            EmiSchedule schedule) {

        EmiScheduleResponse response =
                new EmiScheduleResponse();

        response.setId(schedule.getId());

        response.setLoanId(
                schedule.getLoan().getId());

        response.setInstallmentNo(
                schedule.getEmiNumber());

        response.setDueDate(
                schedule.getDueDate());

        response.setEmiAmount(
                schedule.getEmiAmount());

        response.setInterest(
                schedule.getInterestAmount());

        response.setPrincipal(
                schedule.getPrincipalAmount());

        response.setRemainingPrincipal(
                schedule.getRemainingPrincipal());

        response.setPaymentStatus(
                schedule.getStatus());

        return response;
    }
}