package com.example.loan_management_system.loan.service;

import com.example.loan_management_system.loan.dto.LoanDetailsResponse;
import com.example.loan_management_system.loan.dto.RepaymentResponse;
import com.example.loan_management_system.loan.entity.Loan;
import com.example.loan_management_system.loan.entity.Repayment;
import com.example.loan_management_system.loan.repository.AdditionalPaymentRepository;
import com.example.loan_management_system.loan.repository.LoanRepository;
import com.example.loan_management_system.loan.repository.RepaymentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class LoanDetailsService {

    private static final int MONEY_SCALE = 2;
    private static final int CALCULATION_SCALE = 10;

    private final LoanRepository loanRepository;
    private final RepaymentRepository repaymentRepository;
    private final AdditionalPaymentRepository additionalPaymentRepository;

    public LoanDetailsService(
            LoanRepository loanRepository,
            RepaymentRepository repaymentRepository,
            AdditionalPaymentRepository additionalPaymentRepository) {

        this.loanRepository = loanRepository;
        this.repaymentRepository = repaymentRepository;
        this.additionalPaymentRepository = additionalPaymentRepository;
    }

    @Transactional(readOnly = true)
    public LoanDetailsResponse getLoanDetails(Long loanId) {

        // =====================================================
        // FIND LOAN
        // =====================================================

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Loan not found with id: " + loanId));

        // =====================================================
        // GET REGULAR REPAYMENTS
        // =====================================================

        List<Repayment> repayments =
                repaymentRepository
                        .findByLoanIdOrderByRepaymentDateAsc(loanId);

        BigDecimal regularTotalPaid =
                BigDecimal.ZERO;

        BigDecimal regularPrincipalPaid =
                BigDecimal.ZERO;

        BigDecimal regularInterestPaid =
                BigDecimal.ZERO;

        for (Repayment repayment : repayments) {

            regularTotalPaid =
                    regularTotalPaid.add(
                            repayment.getRepaymentAmount());

            regularPrincipalPaid =
                    regularPrincipalPaid.add(
                            repayment.getPrincipalAmount());

            regularInterestPaid =
                    regularInterestPaid.add(
                            repayment.getInterestAmount());
        }

        // =====================================================
        // GET ADDITIONAL PRINCIPAL PAYMENTS
        // =====================================================

        var additionalPayments =
                additionalPaymentRepository
                        .findByLoanIdOrderByPaymentDateAsc(loanId);

        BigDecimal additionalTotalPaid =
                BigDecimal.ZERO;

        BigDecimal additionalPrincipalPaid =
                BigDecimal.ZERO;

        for (var payment : additionalPayments) {

            additionalTotalPaid =
                    additionalTotalPaid.add(
                            payment.getPaymentAmount());

            additionalPrincipalPaid =
                    additionalPrincipalPaid.add(
                            payment.getPaymentAmount());
        }

        // =====================================================
        // CALCULATE TOTAL PAID
        // =====================================================

        BigDecimal totalPaid =
                regularTotalPaid
                        .add(additionalTotalPaid)
                        .setScale(
                                MONEY_SCALE,
                                RoundingMode.HALF_UP);

        // =====================================================
        // CALCULATE PRINCIPAL PAID
        // =====================================================

        BigDecimal principalPaid =
                regularPrincipalPaid
                        .add(additionalPrincipalPaid)
                        .setScale(
                                MONEY_SCALE,
                                RoundingMode.HALF_UP);

        // =====================================================
        // CALCULATE INTEREST PAID
        // =====================================================

        BigDecimal interestPaid =
                regularInterestPaid
                        .setScale(
                                MONEY_SCALE,
                                RoundingMode.HALF_UP);

        // =====================================================
        // CALCULATE EMI
        // =====================================================

        BigDecimal emiAmount =
                calculateEmi(
                        loan.getLoanAmount(),
                        loan.getAnnualInterestRate(),
                        loan.getLoanTenureMonths());

        // =====================================================
        // CREATE RESPONSE
        // =====================================================

        LoanDetailsResponse response =
                new LoanDetailsResponse();

        response.setLoanId(
                loan.getId());

        response.setCustomerName(
                loan.getCustomerName());

        response.setOriginalLoanAmount(
                loan.getLoanAmount());

        response.setInterestRate(
                loan.getAnnualInterestRate());

        response.setTenureMonths(
                loan.getLoanTenureMonths());

        response.setStartDate(
                loan.getStartDate());

        response.setEmi(
                emiAmount);

        response.setTotalInterest(
                loan.getTotalInterest());

        response.setTotalPaid(
                totalPaid);

        response.setPrincipalPaid(
                principalPaid);

        response.setInterestPaid(
                interestPaid);

        response.setRemainingPrincipal(
                loan.getRemainingPrincipal());

        response.setLoanStatus(
                loan.getStatus());

        // =====================================================
        // REPAYMENT HISTORY
        // =====================================================

        response.setRepaymentHistory(
                repayments.stream()
                        .map(this::convertToResponse)
                        .toList());

        return response;
    }

    // =========================================================
    // EMI CALCULATION
    // =========================================================

    private BigDecimal calculateEmi(
            BigDecimal principal,
            BigDecimal annualInterestRate,
            Integer tenureMonths) {

        principal = principal.setScale(
                MONEY_SCALE,
                RoundingMode.HALF_UP);

        // 0% interest
        if (annualInterestRate.compareTo(
                BigDecimal.ZERO) == 0) {

            return principal.divide(
                    BigDecimal.valueOf(tenureMonths),
                    MONEY_SCALE,
                    RoundingMode.HALF_UP);
        }

        // Monthly interest rate
        BigDecimal monthlyRate =
                annualInterestRate
                        .divide(
                                BigDecimal.valueOf(100),
                                CALCULATION_SCALE,
                                RoundingMode.HALF_UP)
                        .divide(
                                BigDecimal.valueOf(12),
                                CALCULATION_SCALE,
                                RoundingMode.HALF_UP);

        // (1 + r)^n
        BigDecimal onePlusRate =
                BigDecimal.ONE.add(monthlyRate);

        BigDecimal power =
                onePlusRate.pow(tenureMonths);

        // EMI formula
        BigDecimal emi =
                principal
                        .multiply(monthlyRate)
                        .multiply(power)
                        .divide(
                                power.subtract(
                                        BigDecimal.ONE),
                                MONEY_SCALE,
                                RoundingMode.HALF_UP);

        return emi;
    }

    // =========================================================
    // CONVERT REPAYMENT TO RESPONSE
    // =========================================================

    private RepaymentResponse convertToResponse(
            Repayment repayment) {

        RepaymentResponse response =
                new RepaymentResponse();

        response.setId(
                repayment.getId());

        response.setLoanId(
                repayment.getLoan().getId());

        response.setEmiScheduleId(
                repayment.getEmiSchedule().getId());

        response.setDate(
                repayment.getRepaymentDate());

        response.setPaymentAmount(
                repayment.getRepaymentAmount());

        response.setInterestPaid(
                repayment.getInterestAmount());

        response.setPrincipalPaid(
                repayment.getPrincipalAmount());

        response.setRemainingPrincipal(
                repayment.getRemainingPrincipal());

        return response;
    }
}