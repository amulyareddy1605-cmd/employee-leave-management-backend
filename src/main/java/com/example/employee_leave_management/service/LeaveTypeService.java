package com.example.employee_leave_management.service;

import com.example.employee_leave_management.entity.LeaveType;
import com.example.employee_leave_management.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveTypeService {

    private final LeaveTypeRepository repository;

    public LeaveTypeService(LeaveTypeRepository repository) {
        this.repository = repository;
    }

    public LeaveType addLeaveType(LeaveType leaveType) {
        return repository.save(leaveType);
    }

    public List<LeaveType> getAllLeaveTypes() {
        return repository.findAll();
    }

    public void deleteLeaveType(Long id) {
        repository.deleteById(id);
    }
}