package com.example.employee_leave_management.controller;

import com.example.employee_leave_management.entity.LeaveType;
import com.example.employee_leave_management.service.LeaveTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leave-types")
@CrossOrigin(origins = "http://localhost:5173")
public class LeaveTypeController {

    private final LeaveTypeService service;

    public LeaveTypeController(LeaveTypeService service) {
        this.service = service;
    }

    @PostMapping
    public LeaveType addLeaveType(
            @RequestBody LeaveType leaveType) {

        return service.addLeaveType(leaveType);
    }

    @GetMapping
    public List<LeaveType> getAllLeaveTypes() {
        return service.getAllLeaveTypes();
    }

    @DeleteMapping("/{id}")
    public String deleteLeaveType(@PathVariable Long id) {

        service.deleteLeaveType(id);

        return "Leave type deleted successfully";
    }
}