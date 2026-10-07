package com.example.loan_management_system.loan.service;

import com.example.loan_management_system.loan.dto.AdditionalPaymentRequest;
import com.example.loan_management_system.loan.dto.AdditionalPaymentResponse;
import com.example.loan_management_system.loan.entity.AdditionalPayment;
import com.example.loan_management_system.loan.entity.Loan;
import com.example.loan_management_system.loan.entity.LoanStatus;
import com.example.loan_management_system.loan.repository.AdditionalPaymentRepository;
import com.example.loan_management_system.loan.repository.LoanRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AdditionalPaymentService {

    private static final int MONEY_SCALE = 2;

    private final LoanRepository loanRepository;
    private final AdditionalPaymentRepository additionalPaymentRepository;

    public AdditionalPaymentService(
            LoanRepository loanRepository,
            AdditionalPaymentRepository additionalPaymentRepository) {

        this.loanRepository = loanRepository;
        this.additionalPaymentRepository = additionalPaymentRepository;
    }

    @Transactional
    public AdditionalPaymentResponse makeAdditionalPayment(
            Long loanId,
            AdditionalPaymentRequest request) {

        // 1. Find loan
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Loan not found with id: " + loanId));

        // 2. Check loan status
        if (loan.getStatus() == LoanStatus.COMPLETED) {
            throw new RuntimeException(
                    "Completed loan cannot receive additional payment");
        }

        // 3. Validate payment amount
        if (request.getPaymentAmount() == null) {
            throw new RuntimeException(
                    "Additional payment amount is required");
        }

        BigDecimal paymentAmount = request.getPaymentAmount()
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Additional payment must be greater than 0");
        }

        // 4. Get current outstanding principal
        BigDecimal currentPrincipal = loan.getRemainingPrincipal()
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        // 5. Payment cannot exceed outstanding principal
        if (paymentAmount.compareTo(currentPrincipal) > 0) {
            throw new RuntimeException(
                    "Additional payment cannot exceed outstanding principal");
        }

        // 6. Calculate new remaining principal
        BigDecimal newRemainingPrincipal = currentPrincipal
                .subtract(paymentAmount)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        if (newRemainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
            newRemainingPrincipal = BigDecimal.ZERO;
        }

        // 7. Store additional payment
        AdditionalPayment additionalPayment = new AdditionalPayment();

        additionalPayment.setLoan(loan);
        additionalPayment.setPaymentAmount(paymentAmount);
        additionalPayment.setRemainingPrincipal(newRemainingPrincipal);
        additionalPayment.setPaymentDate(request.getPaymentDate());

        AdditionalPayment savedPayment =
                additionalPaymentRepository.save(additionalPayment);

        // 8. Update loan principal immediately
        loan.setRemainingPrincipal(newRemainingPrincipal);

        // 9. Complete loan if principal becomes zero
        if (newRemainingPrincipal.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.COMPLETED);
        } else {
            loan.setStatus(LoanStatus.ACTIVE);
        }

        loanRepository.save(loan);

        // 10. Return response
        return convertToResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public List<AdditionalPaymentResponse> getAdditionalPaymentHistory(
            Long loanId) {

        if (!loanRepository.existsById(loanId)) {
            throw new RuntimeException(
                    "Loan not found with id: " + loanId);
        }

        List<AdditionalPayment> payments =
                additionalPaymentRepository
                        .findByLoanIdOrderByPaymentDateAsc(loanId);

        return payments.stream()
                .map(this::convertToResponse)
                .toList();
    }

    private AdditionalPaymentResponse convertToResponse(
            AdditionalPayment payment) {

        AdditionalPaymentResponse response =
                new AdditionalPaymentResponse();

        response.setId(payment.getId());
        response.setLoanId(payment.getLoan().getId());
        response.setPaymentDate(payment.getPaymentDate());
        response.setPaymentAmount(payment.getPaymentAmount());
        response.setRemainingPrincipal(
                payment.getRemainingPrincipal());

        return response;
    }
}