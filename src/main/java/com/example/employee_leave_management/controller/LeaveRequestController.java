package com.example.employee_leave_management.controller;

import com.example.employee_leave_management.entity.LeaveRequest;
import com.example.employee_leave_management.service.LeaveRequestService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leave")
@CrossOrigin(origins = "http://localhost:5173")
public class LeaveRequestController {

    private final LeaveRequestService service;

    public LeaveRequestController(
            LeaveRequestService service) {

        this.service = service;
    }

    // =========================
    // APPLY
    // =========================

    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(
            @RequestParam Long employeeId,
            @RequestParam Long leaveTypeId,
            @RequestBody LeaveRequest request) {

        try {

            return ResponseEntity.ok(
                    service.applyLeave(
                            employeeId,
                            leaveTypeId,
                            request
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================
    // ALL LEAVES
    // =========================

    @GetMapping
    public ResponseEntity<?> getAllLeaves() {

        return ResponseEntity.ok(
                service.getAllRequests()
        );
    }

    // =========================
    // PENDING
    // =========================

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingLeaves() {

        return ResponseEntity.ok(
                service.getPendingRequests()
        );
    }

    // =========================
    // EMPLOYEE HISTORY
    // =========================

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getEmployeeLeaves(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                service.getEmployeeRequests(
                        employeeId
                )
        );
    }

    // =========================
    // APPROVE
    // =========================

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveLeave(
            @PathVariable Long id,
            @RequestParam(
                    defaultValue = "Approved by Manager"
            )
            String comment) {

        try {

            return ResponseEntity.ok(
                    service.approveLeave(
                            id,
                            comment
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================
    // REJECT
    // =========================

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectLeave(
            @PathVariable Long id,
            @RequestParam(
                    defaultValue = "Rejected by Manager"
            )
            String comment) {

        try {

            return ResponseEntity.ok(
                    service.rejectLeave(
                            id,
                            comment
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}