package com.example.loan_management_system.loan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "0.01", message = "Loan amount must be greater than 0")
    private BigDecimal loanAmount;

    @NotNull(message = "Annual interest rate is required")
    @DecimalMin(value = "0.0", message = "Annual interest rate cannot be negative")
    private BigDecimal annualInterestRate;

    @NotNull(message = "Loan tenure is required")
    @Min(value = 1, message = "Loan tenure must be greater than 0")
    private Integer loanTenureMonths;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    public LoanRequest() {
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(BigDecimal annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public Integer getLoanTenureMonths() {
        return loanTenureMonths;
    }

    public void setLoanTenureMonths(Integer loanTenureMonths) {
        this.loanTenureMonths = loanTenureMonths;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}