package org.dwnppo.enrollmentsystem.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.dwnppo.enrollmentsystem.model.Role;
import org.dwnppo.enrollmentsystem.model.User;
import org.dwnppo.enrollmentsystem.repository.RoleRepository;
import org.dwnppo.enrollmentsystem.repository.UserRepository;

import java.util.List;
import java.util.UUID;

public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User getUser(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("User ID is required.");
        }

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found.")
                );
    }

    public User getUserByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found.")
                );
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void createUser(
            String username,
            String password,
            String studentId,
            String roleName
    ) {
        validateUsername(username);
        validatePassword(password);

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(
                    "Username already exists."
            );
        }

        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Role is required.");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role does not exist."
                        )
                );

        String id = UUID.randomUUID().toString();

        String passwordHash = BCrypt.withDefaults()
                .hashToString(12, password.toCharArray());

        User user = new User(
                id,
                username,
                passwordHash,
                studentId == null || studentId.isBlank()
                        ? null
                        : studentId,
                true
        );

        user.addRole(role);

        userRepository.save(user);
    }

    public void updateUser(
            String id,
            String username,
            String studentId,
            boolean active
    ) {
        validateUsername(username);

        User user = getUser(id);

        userRepository.findByUsername(username)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Username already exists."
                    );
                });

        user.setUsername(username);
        user.setStudentId(
                studentId == null || studentId.isBlank()
                        ? null
                        : studentId
        );
        user.setActive(active);

        userRepository.update(user);
    }

    public void changePassword(
            String id,
            String newPassword
    ) {
        validatePassword(newPassword);

        User user = getUser(id);

        String passwordHash = BCrypt.withDefaults()
                .hashToString(12, newPassword.toCharArray());

        user.setPasswordHash(passwordHash);

        userRepository.update(user);
    }

    public void assignRole(
            String userId,
            String roleName
    ) {
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Role is required.");
        }

        getUser(userId);

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role does not exist."
                        )
                );

        userRepository.assignRole(userId, role.getId());
    }

    public void removeRole(
            String userId,
            String roleName
    ) {
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Role is required.");
        }

        getUser(userId);

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Role does not exist."
                        )
                );

        userRepository.removeRole(userId, role.getId());
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username is required."
            );
        }

        if (username.length() < 3) {
            throw new IllegalArgumentException(
                    "Username must be at least 3 characters."
            );
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required."
            );
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException(
                    "Password must be at least 6 characters."
            );
        }
    }
}