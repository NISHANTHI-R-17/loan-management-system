package com.example.loan_management_system.loan.repository;

import com.example.loan_management_system.loan.entity.AdditionalPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdditionalPaymentRepository extends JpaRepository<AdditionalPayment, Long> {

    List<AdditionalPayment> findByLoanIdOrderByPaymentDateAsc(Long loanId);
}