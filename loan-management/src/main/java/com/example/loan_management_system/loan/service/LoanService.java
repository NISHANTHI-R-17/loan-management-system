package com.example.loan_management_system.loan.service;

import com.example.loan_management_system.loan.dto.LoanRequest;
import com.example.loan_management_system.loan.dto.LoanResponse;
import com.example.loan_management_system.loan.entity.Loan;
import com.example.loan_management_system.loan.entity.LoanStatus;
import com.example.loan_management_system.loan.repository.LoanRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    private static final int MONEY_SCALE = 2;
    private static final int CALCULATION_SCALE = 10;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    // =========================================================
    // CREATE LOAN
    // POST /api/loans
    // =========================================================

    public LoanResponse createLoan(LoanRequest request) {

        BigDecimal principal = request.getLoanAmount();
        BigDecimal annualRate = request.getAnnualInterestRate();
        int tenureMonths = request.getLoanTenureMonths();

        // Calculate Monthly EMI
        BigDecimal monthlyEmi = calculateMonthlyEmi(
                principal,
                annualRate,
                tenureMonths
        );

        // Calculate Total Payable
        BigDecimal totalPayable = monthlyEmi.multiply(
                BigDecimal.valueOf(tenureMonths)
        );

        // Calculate Total Interest
        BigDecimal totalInterest = totalPayable.subtract(principal);

        // Create Loan Entity
        Loan loan = new Loan();

        loan.setCustomerName(
                request.getCustomerName().trim()
        );

        loan.setLoanAmount(
                principal.setScale(
                        MONEY_SCALE,
                        RoundingMode.HALF_UP
                )
        );

        loan.setAnnualInterestRate(
                annualRate.setScale(
                        4,
                        RoundingMode.HALF_UP
                )
        );

        loan.setLoanTenureMonths(
                tenureMonths
        );

        loan.setStartDate(
                request.getStartDate()
        );

        loan.setMonthlyEmi(
                monthlyEmi.setScale(
                        MONEY_SCALE,
                        RoundingMode.HALF_UP
                )
        );

        loan.setTotalInterest(
                totalInterest.setScale(
                        MONEY_SCALE,
                        RoundingMode.HALF_UP
                )
        );

        loan.setTotalPayable(
                totalPayable.setScale(
                        MONEY_SCALE,
                        RoundingMode.HALF_UP
                )
        );

        // Initially no repayment has been made
        loan.setRemainingPrincipal(
                principal.setScale(
                        MONEY_SCALE,
                        RoundingMode.HALF_UP
                )
        );

        // Initial status
        loan.setStatus(
                LoanStatus.ACTIVE
        );

        Loan savedLoan = loanRepository.save(loan);

        return convertToResponse(savedLoan);
    }

    // =========================================================
    // GET ALL LOANS
    // GET /api/loans
    // =========================================================

    public List<LoanResponse> getAllLoans() {

        List<Loan> loans = loanRepository.findAll();

        return loans.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // GET LOAN BY ID
    // GET /api/loans/{loanId}
    // =========================================================

    public LoanResponse getLoanById(Long loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Loan not found with id: " + loanId
                        )
                );

        return convertToResponse(loan);
    }

    // =========================================================
    // CALCULATE MONTHLY EMI
    // =========================================================

    private BigDecimal calculateMonthlyEmi(
            BigDecimal principal,
            BigDecimal annualRate,
            int tenureMonths) {

        // 0% interest
        if (annualRate.compareTo(BigDecimal.ZERO) == 0) {

            return principal.divide(
                    BigDecimal.valueOf(tenureMonths),
                    CALCULATION_SCALE,
                    RoundingMode.HALF_UP
            );
        }

        /*
         * Reducing Balance EMI Formula
         *
         * EMI = P × r × (1+r)^n
         *       -----------------
         *          (1+r)^n - 1
         *
         * P = Principal
         * r = Monthly interest rate
         * n = Number of months
         */

        BigDecimal monthlyRate = annualRate
                .divide(
                        BigDecimal.valueOf(100),
                        CALCULATION_SCALE,
                        RoundingMode.HALF_UP
                )
                .divide(
                        BigDecimal.valueOf(12),
                        CALCULATION_SCALE,
                        RoundingMode.HALF_UP
                );

        BigDecimal onePlusRate =
                BigDecimal.ONE.add(monthlyRate);

        BigDecimal power =
                onePlusRate.pow(tenureMonths);

        BigDecimal numerator =
                principal
                        .multiply(monthlyRate)
                        .multiply(power);

        BigDecimal denominator =
                power.subtract(BigDecimal.ONE);

        return numerator.divide(
                denominator,
                CALCULATION_SCALE,
                RoundingMode.HALF_UP
        );
    }

    // =========================================================
    // CONVERT ENTITY TO RESPONSE
    // =========================================================

    private LoanResponse convertToResponse(Loan loan) {

        LoanResponse response = new LoanResponse();

        response.setId(
                loan.getId()
        );

        response.setCustomerName(
                loan.getCustomerName()
        );

        response.setLoanAmount(
                loan.getLoanAmount()
        );

        response.setAnnualInterestRate(
                loan.getAnnualInterestRate()
        );

        response.setLoanTenureMonths(
                loan.getLoanTenureMonths()
        );

        response.setStartDate(
                loan.getStartDate()
        );

        response.setMonthlyEmi(
                loan.getMonthlyEmi()
        );

        response.setTotalInterest(
                loan.getTotalInterest()
        );

        response.setTotalPayable(
                loan.getTotalPayable()
        );

        response.setRemainingPrincipal(
                loan.getRemainingPrincipal()
        );

        response.setStatus(
                loan.getStatus()
        );

        return response;
    }
}