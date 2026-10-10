package com.example.employee_leave_management.repository;

import com.example.employee_leave_management.entity.LeaveBalance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository
        extends JpaRepository<LeaveBalance, Long> {

    Optional<LeaveBalance>
    findByEmployeeIdAndLeaveTypeIdAndYear(
            Long employeeId,
            Long leaveTypeId,
            int year
    );

    List<LeaveBalance>
    findByEmployeeIdAndYear(
            Long employeeId,
            int year
    );
}