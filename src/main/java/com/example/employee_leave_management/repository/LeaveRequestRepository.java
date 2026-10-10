package com.example.employee_leave_management.repository;

import com.example.employee_leave_management.entity.LeaveRequest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository
        extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest>
    findByEmployeeId(Long employeeId);

    List<LeaveRequest>
    findByStatus(String status);
}