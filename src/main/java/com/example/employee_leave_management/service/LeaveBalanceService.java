package com.example.employee_leave_management.service;

import com.example.employee_leave_management.entity.Employee;
import com.example.employee_leave_management.entity.LeaveBalance;
import com.example.employee_leave_management.entity.LeaveType;
import com.example.employee_leave_management.repository.EmployeeRepository;
import com.example.employee_leave_management.repository.LeaveBalanceRepository;
import com.example.employee_leave_management.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;

@Service
public class LeaveBalanceService {

    private final LeaveBalanceRepository balanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveBalanceService(
            LeaveBalanceRepository balanceRepository,
            EmployeeRepository employeeRepository,
            LeaveTypeRepository leaveTypeRepository) {

        this.balanceRepository = balanceRepository;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public LeaveBalance createBalance(
            Long employeeId,
            Long leaveTypeId) {

        Employee employee = employeeRepository
                .findById(employeeId)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        LeaveType leaveType = leaveTypeRepository
                .findById(leaveTypeId)
                .orElseThrow(() ->
                        new RuntimeException("Leave type not found"));

        int currentYear = Year.now().getValue();

        LeaveBalance balance =
                balanceRepository
                        .findByEmployeeIdAndLeaveTypeIdAndYear(
                                employeeId,
                                leaveTypeId,
                                currentYear)
                        .orElse(new LeaveBalance());

        balance.setEmployee(employee);
        balance.setLeaveType(leaveType);
        balance.setAllocatedDays(leaveType.getAnnualLimit());
        balance.setUsedDays(0);
        balance.setRemainingDays(leaveType.getAnnualLimit());
        balance.setYear(currentYear);

        return balanceRepository.save(balance);
    }

    public List<LeaveBalance> getEmployeeBalances(
            Long employeeId) {

        return balanceRepository
                .findByEmployeeIdAndYear(
                        employeeId,
                        Year.now().getValue());
    }
}