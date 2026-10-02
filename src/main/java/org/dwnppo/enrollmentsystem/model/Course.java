package org.dwnppo.enrollmentsystem.model;

import java.time.LocalDateTime;

public class Course {

    private String id;
    private String code;
    private String name;
    private String description;
    private double units;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Course() {
    }

    public Course(
            String id,
            String code,
            String name,
            String description,
            double units
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.units = units;
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

    public double getUnits() {
        return units;
    }

    public void setUnits(double units) {
        this.units = units;
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