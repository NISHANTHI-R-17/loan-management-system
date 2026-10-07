package com.example.loan_management_system.loan.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RepaymentRequest {

    private Long emiScheduleId;

    private BigDecimal repaymentAmount;

    private LocalDate repaymentDate;

    public RepaymentRequest() {
    }

    public Long getEmiScheduleId() {
        return emiScheduleId;
    }

    public void setEmiScheduleId(Long emiScheduleId) {
        this.emiScheduleId = emiScheduleId;
    }

    public BigDecimal getRepaymentAmount() {
        return repaymentAmount;
    }

    public void setRepaymentAmount(BigDecimal repaymentAmount) {
        this.repaymentAmount = repaymentAmount;
    }

    public LocalDate getRepaymentDate() {
        return repaymentDate;
    }

    public void setRepaymentDate(LocalDate repaymentDate) {
        this.repaymentDate = repaymentDate;
    }
}