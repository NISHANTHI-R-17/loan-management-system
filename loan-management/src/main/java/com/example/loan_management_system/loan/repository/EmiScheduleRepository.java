package com.example.loan_management_system.loan.repository;

import com.example.loan_management_system.loan.entity.EmiSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmiScheduleRepository extends JpaRepository<EmiSchedule, Long> {

    List<EmiSchedule> findByLoanIdOrderByEmiNumberAsc(Long loanId);

    Optional<EmiSchedule> findByIdAndLoanId(Long id, Long loanId);
}