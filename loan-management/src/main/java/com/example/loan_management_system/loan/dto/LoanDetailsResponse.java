package com.example.loan_management_system.loan.dto;

import com.example.loan_management_system.loan.entity.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class LoanDetailsResponse {

    private Long loanId;

    private String customerName;

    private BigDecimal originalLoanAmount;

    private BigDecimal interestRate;

    private Integer tenureMonths;

    private LocalDate startDate;

    private BigDecimal emi;

    private BigDecimal totalInterest;

    private BigDecimal totalPaid;

    private BigDecimal principalPaid;

    private BigDecimal interestPaid;

    private BigDecimal remainingPrincipal;

    private LoanStatus loanStatus;

    private List<RepaymentResponse> repaymentHistory;

    public LoanDetailsResponse() {
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getOriginalLoanAmount() {
        return originalLoanAmount;
    }

    public void setOriginalLoanAmount(BigDecimal originalLoanAmount) {
        this.originalLoanAmount = originalLoanAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public BigDecimal getEmi() {
        return emi;
    }

    public void setEmi(BigDecimal emi) {
        this.emi = emi;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public BigDecimal getPrincipalPaid() {
        return principalPaid;
    }

    public void setPrincipalPaid(BigDecimal principalPaid) {
        this.principalPaid = principalPaid;
    }

    public BigDecimal getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(BigDecimal interestPaid) {
        this.interestPaid = interestPaid;
    }

    public BigDecimal getRemainingPrincipal() {
        return remainingPrincipal;
    }

    public void setRemainingPrincipal(BigDecimal remainingPrincipal) {
        this.remainingPrincipal = remainingPrincipal;
    }

    public LoanStatus getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(LoanStatus loanStatus) {
        this.loanStatus = loanStatus;
    }

    public List<RepaymentResponse> getRepaymentHistory() {
        return repaymentHistory;
    }

    public void setRepaymentHistory(
            List<RepaymentResponse> repaymentHistory) {

        this.repaymentHistory = repaymentHistory;
    }
}