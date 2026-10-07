package com.example.loan_management_system.loan.service;

import com.example.loan_management_system.loan.dto.RepaymentRequest;
import com.example.loan_management_system.loan.dto.RepaymentResponse;
import com.example.loan_management_system.loan.entity.EmiSchedule;
import com.example.loan_management_system.loan.entity.EmiStatus;
import com.example.loan_management_system.loan.entity.Loan;
import com.example.loan_management_system.loan.entity.LoanStatus;
import com.example.loan_management_system.loan.entity.Repayment;
import com.example.loan_management_system.loan.repository.EmiScheduleRepository;
import com.example.loan_management_system.loan.repository.LoanRepository;
import com.example.loan_management_system.loan.repository.RepaymentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class RepaymentService {

    private final LoanRepository loanRepository;
    private final EmiScheduleRepository emiScheduleRepository;
    private final RepaymentRepository repaymentRepository;

    private static final int MONEY_SCALE = 2;
    private static final int CALCULATION_SCALE = 10;

    public RepaymentService(
            LoanRepository loanRepository,
            EmiScheduleRepository emiScheduleRepository,
            RepaymentRepository repaymentRepository) {

        this.loanRepository = loanRepository;
        this.emiScheduleRepository = emiScheduleRepository;
        this.repaymentRepository = repaymentRepository;
    }

    // ============================================================
    // MAKE REPAYMENT
    // ============================================================

    @Transactional
    public RepaymentResponse makeRepayment(
            Long loanId,
            RepaymentRequest request) {

        // --------------------------------------------------------
        // 1. Find Loan
        // --------------------------------------------------------

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Loan not found with ID: " + loanId
                        )
                );

        // --------------------------------------------------------
        // 2. Check Loan Status
        // --------------------------------------------------------

        if (loan.getStatus() == LoanStatus.COMPLETED) {

            throw new RuntimeException(
                    "Completed loan cannot receive further repayments"
            );
        }

        // --------------------------------------------------------
        // 3. Validate EMI Schedule ID
        // --------------------------------------------------------

        if (request.getEmiScheduleId() == null) {

            throw new RuntimeException(
                    "EMI schedule ID is required"
            );
        }

        // --------------------------------------------------------
        // 4. Validate Repayment Amount
        // --------------------------------------------------------

        if (request.getRepaymentAmount() == null ||
                request.getRepaymentAmount()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Repayment amount must be greater than 0"
            );
        }

        // --------------------------------------------------------
        // 5. Find EMI Schedule
        // --------------------------------------------------------

        EmiSchedule emiSchedule =
                emiScheduleRepository
                        .findByIdAndLoanId(
                                request.getEmiScheduleId(),
                                loanId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "EMI schedule not found for this loan"
                                )
                        );

        // --------------------------------------------------------
        // 6. Check EMI Already Paid
        // --------------------------------------------------------

        if (emiSchedule.getStatus() == EmiStatus.PAID) {

            throw new RuntimeException(
                    "This EMI has already been paid"
            );
        }

        // --------------------------------------------------------
        // 7. Get Current Remaining Principal
        // --------------------------------------------------------

        BigDecimal currentPrincipal =
                loan.getRemainingPrincipal();

        if (currentPrincipal == null) {

            currentPrincipal =
                    loan.getLoanAmount();
        }

        currentPrincipal = currentPrincipal.setScale(
                MONEY_SCALE,
                RoundingMode.HALF_UP
        );

        // --------------------------------------------------------
        // 8. Get Repayment Amount
        // --------------------------------------------------------

        BigDecimal repaymentAmount =
                request.getRepaymentAmount()
                        .setScale(
                                MONEY_SCALE,
                                RoundingMode.HALF_UP
                        );

        // --------------------------------------------------------
        // 9. Repayment Cannot Exceed Outstanding Principal
        // --------------------------------------------------------

        if (repaymentAmount.compareTo(currentPrincipal) > 0) {

            throw new RuntimeException(
                    "Repayment amount cannot exceed outstanding principal"
            );
        }

        // --------------------------------------------------------
        // 10. Calculate Monthly Interest Rate
        // --------------------------------------------------------

        BigDecimal annualRate =
                loan.getAnnualInterestRate();

        if (annualRate == null) {

            annualRate = BigDecimal.ZERO;
        }

        BigDecimal monthlyRate;

        if (annualRate.compareTo(BigDecimal.ZERO) == 0) {

            monthlyRate = BigDecimal.ZERO;

        } else {

            monthlyRate = annualRate
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
        }

        // --------------------------------------------------------
        // 11. Calculate Interest
        //
        // Interest is calculated on CURRENT remaining principal.
        // --------------------------------------------------------

        BigDecimal interestAmount =
                currentPrincipal
                        .multiply(monthlyRate)
                        .setScale(
                                MONEY_SCALE,
                                RoundingMode.HALF_UP
                        );

        // --------------------------------------------------------
        // 12. Calculate Principal Paid
        // --------------------------------------------------------

        BigDecimal principalAmount =
                repaymentAmount
                        .subtract(interestAmount)
                        .setScale(
                                MONEY_SCALE,
                                RoundingMode.HALF_UP
                        );

        // --------------------------------------------------------
        // 13. Validate Principal Amount
        // --------------------------------------------------------

        if (principalAmount.compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "Repayment amount is not sufficient to cover interest"
            );
        }

        // --------------------------------------------------------
        // 14. Prevent Principal From Exceeding Balance
        // --------------------------------------------------------

        if (principalAmount.compareTo(currentPrincipal) > 0) {

            principalAmount = currentPrincipal;
        }

        // --------------------------------------------------------
        // 15. Calculate New Remaining Principal
        // --------------------------------------------------------

        BigDecimal newRemainingPrincipal =
                currentPrincipal
                        .subtract(principalAmount)
                        .setScale(
                                MONEY_SCALE,
                                RoundingMode.HALF_UP
                        );

        // Never allow negative balance
        if (newRemainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {

            newRemainingPrincipal = BigDecimal.ZERO;
        }

        // --------------------------------------------------------
        // 16. Create Repayment
        // --------------------------------------------------------

        Repayment repayment = new Repayment();

        repayment.setLoan(loan);

        repayment.setEmiSchedule(emiSchedule);

        repayment.setRepaymentAmount(
                repaymentAmount
        );

        repayment.setInterestAmount(
                interestAmount
        );

        repayment.setPrincipalAmount(
                principalAmount
        );

        repayment.setRemainingPrincipal(
                newRemainingPrincipal
        );

        // --------------------------------------------------------
        // 17. Repayment Date
        // --------------------------------------------------------

        if (request.getRepaymentDate() != null) {

            repayment.setRepaymentDate(
                    request.getRepaymentDate()
            );

        } else {

            repayment.setRepaymentDate(
                    LocalDate.now()
            );
        }

        // --------------------------------------------------------
        // 18. Save Repayment
        // --------------------------------------------------------

        Repayment savedRepayment =
                repaymentRepository.save(repayment);

        // --------------------------------------------------------
        // 19. Update Loan Remaining Principal
        // --------------------------------------------------------

        loan.setRemainingPrincipal(
                newRemainingPrincipal
        );

        // --------------------------------------------------------
        // 20. Update Loan Status
        // --------------------------------------------------------

        if (newRemainingPrincipal.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            loan.setStatus(
                    LoanStatus.COMPLETED
            );

        } else {

            loan.setStatus(
                    LoanStatus.ACTIVE
            );
        }

        loanRepository.save(loan);

        // --------------------------------------------------------
        // 21. Mark EMI as PAID
        // --------------------------------------------------------

        emiSchedule.setStatus(
                EmiStatus.PAID
        );

        emiSchedule.setRemainingPrincipal(
                newRemainingPrincipal
        );

        emiScheduleRepository.save(
                emiSchedule
        );

        // --------------------------------------------------------
        // 22. Return Response
        // --------------------------------------------------------

        return convertToResponse(
                savedRepayment
        );
    }

    // ============================================================
    // GET REPAYMENT HISTORY
    // ============================================================

    public List<RepaymentResponse> getRepaymentHistory(
            Long loanId) {

        // Check loan exists
        loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Loan not found with ID: " + loanId
                        )
                );

        List<Repayment> repayments =
                repaymentRepository
                        .findByLoanIdOrderByRepaymentDateAsc(
                                loanId
                        );

        return repayments.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // ============================================================
    // CONVERT ENTITY TO RESPONSE
    // ============================================================

    private RepaymentResponse convertToResponse(
            Repayment repayment) {

        RepaymentResponse response =
                new RepaymentResponse();

        // Repayment ID
        response.setId(
                repayment.getId()
        );

        // Loan ID
        response.setLoanId(
                repayment.getLoan().getId()
        );

        // Database EMI Schedule ID
        response.setEmiScheduleId(
                repayment.getEmiSchedule().getId()
        );

        // ========================================================
        // IMPORTANT
        // ========================================================
        // Database ID and EMI installment number are different.
        //
        // Example:
        //
        // EMI database ID       = 13
        // EMI installment No.   = 1
        //
        // We want the frontend to display:
        //
        // EMI #1
        // ========================================================

        response.setInstallmentNo(
                repayment.getEmiSchedule().getEmiNumber()
        );

        // Repayment Date
        response.setDate(
                repayment.getRepaymentDate()
        );

        // Total Payment
        response.setPaymentAmount(
                repayment.getRepaymentAmount()
        );

        // Interest Paid
        response.setInterestPaid(
                repayment.getInterestAmount()
        );

        // Principal Paid
        response.setPrincipalPaid(
                repayment.getPrincipalAmount()
        );

        // Remaining Principal
        response.setRemainingPrincipal(
                repayment.getRemainingPrincipal()
        );

        return response;
    }
}