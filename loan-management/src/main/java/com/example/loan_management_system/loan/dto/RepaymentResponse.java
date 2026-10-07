package com.example.loan_management_system.loan.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RepaymentResponse {

    private Long id;
    private Long loanId;
    private Long emiScheduleId;

    // Actual EMI installment number: 1, 2, 3...
    private Integer installmentNo;

    private LocalDate date;
    private BigDecimal paymentAmount;
    private BigDecimal interestPaid;
    private BigDecimal principalPaid;
    private BigDecimal remainingPrincipal;

    public RepaymentResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public Long getEmiScheduleId() {
        return emiScheduleId;
    }

    public void setEmiScheduleId(Long emiScheduleId) {
        this.emiScheduleId = emiScheduleId;
    }

    public Integer getInstallmentNo() {
        return installmentNo;
    }

    public void setInstallmentNo(Integer installmentNo) {
        this.installmentNo = installmentNo;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(BigDecimal interestPaid) {
        this.interestPaid = interestPaid;
    }

    public BigDecimal getPrincipalPaid() {
        return principalPaid;
    }

    public void setPrincipalPaid(BigDecimal principalPaid) {
        this.principalPaid = principalPaid;
    }

    public BigDecimal getRemainingPrincipal() {
        return remainingPrincipal;
    }

    public void setRemainingPrincipal(BigDecimal remainingPrincipal) {
        this.remainingPrincipal = remainingPrincipal;
    }
}