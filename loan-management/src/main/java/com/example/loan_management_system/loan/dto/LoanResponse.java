package com.example.loan_management_system.loan.dto;

import com.example.loan_management_system.loan.entity.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanResponse {

    private Long id;

    private String customerName;

    private BigDecimal loanAmount;

    private BigDecimal annualInterestRate;

    private Integer loanTenureMonths;

    private LocalDate startDate;

    private BigDecimal monthlyEmi;

    private BigDecimal totalInterest;

    private BigDecimal totalPayable;

    private BigDecimal remainingPrincipal;

    private LoanStatus status;

    // Default constructor
    public LoanResponse() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getMonthlyEmi() {
        return monthlyEmi;
    }

    public void setMonthlyEmi(BigDecimal monthlyEmi) {
        this.monthlyEmi = monthlyEmi;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public BigDecimal getTotalPayable() {
        return totalPayable;
    }

    public void setTotalPayable(BigDecimal totalPayable) {
        this.totalPayable = totalPayable;
    }

    public BigDecimal getRemainingPrincipal() {
        return remainingPrincipal;
    }

    public void setRemainingPrincipal(BigDecimal remainingPrincipal) {
        this.remainingPrincipal = remainingPrincipal;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }
}