package org.dwnppo.enrollmentsystem.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {

    private String id;
    private String username;
    private String passwordHash;
    private String studentId;
    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<Role> roles = new ArrayList<>();

    public User() {
    }

    public User(
            String id,
            String username,
            String passwordHash,
            String studentId,
            boolean active
    ) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.studentId = studentId;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(role ->
                        role.getName().equalsIgnoreCase(roleName)
                );
    }

    @Override
    public String toString() {
        return username;
    }
}