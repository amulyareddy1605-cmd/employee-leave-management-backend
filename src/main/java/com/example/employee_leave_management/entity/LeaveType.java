package com.example.employee_leave_management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_types")
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private int annualLimit;

    public LeaveType() {
    }

    public LeaveType(Long id, String name,
                     String description, int annualLimit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.annualLimit = annualLimit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAnnualLimit() {
        return annualLimit;
    }

    public void setAnnualLimit(int annualLimit) {
        this.annualLimit = annualLimit;
    }
}
