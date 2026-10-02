package org.dwnppo.enrollmentsystem.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.dwnppo.enrollmentsystem.model.User;
import org.dwnppo.enrollmentsystem.repository.UserRepository;

public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username is required."
            );
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required."
            );
        }

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid username or password."
                        )
                );

        if (!user.isActive()) {
            throw new IllegalStateException(
                    "This account is inactive."
            );
        }

        BCrypt.Result result = BCrypt.verifyer()
                .verify(
                        password.toCharArray(),
                        user.getPasswordHash()
                );

        if (!result.verified) {
            throw new IllegalArgumentException(
                    "Invalid username or password."
            );
        }

        return user;
    }

    public String hashPassword(String password) {

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required."
            );
        }

        return BCrypt.withDefaults()
                .hashToString(
                        12,
                        password.toCharArray()
                );
    }
}