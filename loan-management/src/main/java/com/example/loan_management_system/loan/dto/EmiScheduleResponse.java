package com.example.loan_management_system.loan.dto;

import com.example.loan_management_system.loan.entity.EmiStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmiScheduleResponse {

    private Long id;
    private Long loanId;

    private Integer installmentNo;

    private LocalDate dueDate;

    private BigDecimal emiAmount;

    private BigDecimal interest;

    private BigDecimal principal;

    private BigDecimal remainingPrincipal;

    private EmiStatus paymentStatus;

    public EmiScheduleResponse() {
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

    public Integer getInstallmentNo() {
        return installmentNo;
    }

    public void setInstallmentNo(Integer installmentNo) {
        this.installmentNo = installmentNo;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getEmiAmount() {
        return emiAmount;
    }

    public void setEmiAmount(BigDecimal emiAmount) {
        this.emiAmount = emiAmount;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getRemainingPrincipal() {
        return remainingPrincipal;
    }

    public void setRemainingPrincipal(BigDecimal remainingPrincipal) {
        this.remainingPrincipal = remainingPrincipal;
    }

    public EmiStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(EmiStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}