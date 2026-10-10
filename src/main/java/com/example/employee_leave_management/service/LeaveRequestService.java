package com.example.employee_leave_management.service;

import com.example.employee_leave_management.entity.Employee;
import com.example.employee_leave_management.entity.LeaveBalance;
import com.example.employee_leave_management.entity.LeaveRequest;
import com.example.employee_leave_management.entity.LeaveType;

import com.example.employee_leave_management.repository.EmployeeRepository;
import com.example.employee_leave_management.repository.LeaveBalanceRepository;
import com.example.employee_leave_management.repository.LeaveRequestRepository;
import com.example.employee_leave_management.repository.LeaveTypeRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    public LeaveRequestService(
            LeaveRequestRepository leaveRequestRepository,
            EmployeeRepository employeeRepository,
            LeaveTypeRepository leaveTypeRepository,
            LeaveBalanceRepository leaveBalanceRepository) {

        this.leaveRequestRepository =
                leaveRequestRepository;

        this.employeeRepository =
                employeeRepository;

        this.leaveTypeRepository =
                leaveTypeRepository;

        this.leaveBalanceRepository =
                leaveBalanceRepository;
    }

    // =========================
    // APPLY LEAVE
    // =========================

    @Transactional
    public LeaveRequest applyLeave(
            Long employeeId,
            Long leaveTypeId,
            LeaveRequest request) {

        Employee employee =
                employeeRepository
                        .findById(employeeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee not found"
                                )
                        );

        LeaveType leaveType =
                leaveTypeRepository
                        .findById(leaveTypeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Leave type not found"
                                )
                        );

        if (request.getStartDate() == null ||
                request.getEndDate() == null) {

            throw new RuntimeException(
                    "Start date and end date are required"
            );
        }

        if (request.getEndDate()
                .isBefore(request.getStartDate())) {

            throw new RuntimeException(
                    "End date cannot be before start date"
            );
        }

        int days =
                (int) ChronoUnit.DAYS.between(
                        request.getStartDate(),
                        request.getEndDate()
                ) + 1;

        int currentYear =
                Year.now().getValue();

        LeaveBalance balance =
                leaveBalanceRepository
                        .findByEmployeeIdAndLeaveTypeIdAndYear(
                                employeeId,
                                leaveTypeId,
                                currentYear
                        )
                        .orElseGet(() -> {

                            LeaveBalance newBalance =
                                    new LeaveBalance();

                            newBalance.setEmployee(employee);
                            newBalance.setLeaveType(leaveType);

                            newBalance.setAllocatedDays(
                                    leaveType.getAnnualLimit()
                            );

                            newBalance.setUsedDays(0);

                            newBalance.setRemainingDays(
                                    leaveType.getAnnualLimit()
                            );

                            newBalance.setYear(currentYear);

                            return leaveBalanceRepository
                                    .save(newBalance);
                        });

        if (days >
                balance.getRemainingDays()) {

            throw new RuntimeException(
                    "Insufficient leave balance. Remaining days: "
                            + balance.getRemainingDays()
            );
        }

        request.setEmployee(employee);
        request.setLeaveType(leaveType);
        request.setNumberOfDays(days);
        request.setStatus("PENDING");

        return leaveRequestRepository
                .save(request);
    }

    // =========================
    // GET ALL
    // =========================

    public List<LeaveRequest>
    getAllRequests() {

        return leaveRequestRepository
                .findAll();
    }

    // =========================
    // EMPLOYEE HISTORY
    // =========================

    public List<LeaveRequest>
    getEmployeeRequests(
            Long employeeId) {

        return leaveRequestRepository
                .findByEmployeeId(employeeId);
    }

    // =========================
    // PENDING
    // =========================

    public List<LeaveRequest>
    getPendingRequests() {

        return leaveRequestRepository
                .findByStatus("PENDING");
    }

    // =========================
    // APPROVE
    // =========================

    @Transactional
    public LeaveRequest approveLeave(
            Long id,
            String comment) {

        LeaveRequest request =
                leaveRequestRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Leave request not found"
                                )
                        );

        if (!"PENDING".equalsIgnoreCase(
                request.getStatus())) {

            throw new RuntimeException(
                    "Only pending requests can be approved"
            );
        }

        int currentYear =
                Year.now().getValue();

        LeaveBalance balance =
                leaveBalanceRepository
                        .findByEmployeeIdAndLeaveTypeIdAndYear(
                                request.getEmployee().getId(),
                                request.getLeaveType().getId(),
                                currentYear
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Leave balance not found"
                                )
                        );

        if (request.getNumberOfDays() >
                balance.getRemainingDays()) {

            throw new RuntimeException(
                    "Insufficient leave balance"
            );
        }

        balance.setUsedDays(
                balance.getUsedDays()
                        + request.getNumberOfDays()
        );

        balance.setRemainingDays(
                balance.getRemainingDays()
                        - request.getNumberOfDays()
        );

        leaveBalanceRepository.save(balance);

        request.setStatus("APPROVED");
        request.setManagerComment(comment);

        return leaveRequestRepository
                .save(request);
    }

    // =========================
    // REJECT
    // =========================

    @Transactional
    public LeaveRequest rejectLeave(
            Long id,
            String comment) {

        LeaveRequest request =
                leaveRequestRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Leave request not found"
                                )
                        );

        if (!"PENDING".equalsIgnoreCase(
                request.getStatus())) {

            throw new RuntimeException(
                    "Only pending requests can be rejected"
            );
        }

        request.setStatus("REJECTED");
        request.setManagerComment(comment);

        return leaveRequestRepository
                .save(request);
    }
}