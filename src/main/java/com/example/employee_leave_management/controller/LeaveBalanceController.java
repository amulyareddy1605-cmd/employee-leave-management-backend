package com.example.employee_leave_management.controller;

import com.example.employee_leave_management.entity.LeaveBalance;
import com.example.employee_leave_management.service.LeaveBalanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leave-balance")
@CrossOrigin(origins = "http://localhost:5173")
public class LeaveBalanceController {

    private final LeaveBalanceService service;

    public LeaveBalanceController(
            LeaveBalanceService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public LeaveBalance createBalance(
            @RequestParam Long employeeId,
            @RequestParam Long leaveTypeId) {

        return service.createBalance(
                employeeId,
                leaveTypeId
        );
    }

    @GetMapping("/employee/{employeeId}")
    public List<LeaveBalance> getEmployeeBalances(
            @PathVariable Long employeeId) {

        return service.getEmployeeBalances(employeeId);
    }
}