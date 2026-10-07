package com.example.loan_management_system.loan.repository;

import com.example.loan_management_system.loan.entity.Repayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepaymentRepository extends JpaRepository<Repayment, Long> {

    List<Repayment> findByLoanIdOrderByRepaymentDateAsc(Long loanId);
}