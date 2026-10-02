package org.dwnppo.enrollmentsystem.model;

import java.time.LocalDateTime;

public class Program {

    private String id;
    private String code;
    private String name;
    private String description;
    private int durationYears;
    private String department;
    private String college;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Program() {
    }

    public Program(
            String id,
            String code,
            String name,
            String description,
            int durationYears,
            String department,
            String college
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.durationYears = durationYears;
        this.department = department;
        this.college = college;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public int getDurationYears() {
        return durationYears;
    }

    public void setDurationYears(int durationYears) {
        this.durationYears = durationYears;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }
}