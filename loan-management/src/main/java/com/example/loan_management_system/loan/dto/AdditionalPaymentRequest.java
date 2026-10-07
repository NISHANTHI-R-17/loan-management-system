package com.example.loan_management_system.loan.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AdditionalPaymentRequest {

    private BigDecimal paymentAmount;

    private LocalDate paymentDate;

    public AdditionalPaymentRequest() {
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}