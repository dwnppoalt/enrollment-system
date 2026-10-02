package org.dwnppo.enrollmentsystem.repository;

import org.dwnppo.enrollmentsystem.model.Role;
import org.dwnppo.enrollmentsystem.model.User;
import org.dwnppo.enrollmentsystem.util.db.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    public Optional<User> findById(String id) {
        String sql = """
                SELECT *
                FROM users
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    User user = mapRow(result);
                    loadRoles(user);
                    return Optional.of(user);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user", e);
        }

        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) {
        String sql = """
                SELECT *
                FROM users
                WHERE username = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    User user = mapRow(result);
                    loadRoles(user);
                    return Optional.of(user);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find user by username",
                    e
            );
        }

        return Optional.empty();
    }

    public List<User> findAll() {
        String sql = """
                SELECT *
                FROM users
                ORDER BY username
                """;

        List<User> users = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {
            while (result.next()) {
                User user = mapRow(result);
                loadRoles(user);
                users.add(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find users", e);
        }

        return users;
    }

    public List<User> findByRole(String roleName) {
        String sql = """
                SELECT u.*
                FROM users u
                INNER JOIN user_roles ur
                    ON u.id = ur.user_id
                INNER JOIN roles r
                    ON ur.role_id = r.id
                WHERE r.name = ?
                ORDER BY u.username
                """;

        List<User> users = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, roleName);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    User user = mapRow(result);
                    loadRoles(user);
                    users.add(user);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find users by role",
                    e
            );
        }

        return users;
    }

    public void save(User user) {
        String sql = """
                INSERT INTO users (
                    id,
                    username,
                    password_hash,
                    student_id,
                    active
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, user.getId());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPasswordHash());

            if (user.getStudentId() == null) {
                statement.setNull(4, Types.VARCHAR);
            } else {
                statement.setString(4, user.getStudentId());
            }

            statement.setBoolean(5, user.isActive());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    public void update(User user) {
        String sql = """
                UPDATE users
                SET username = ?,
                    password_hash = ?,
                    student_id = ?,
                    active = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());

            if (user.getStudentId() == null) {
                statement.setNull(3, Types.VARCHAR);
            } else {
                statement.setString(3, user.getStudentId());
            }

            statement.setBoolean(4, user.isActive());
            statement.setString(5, user.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    public void deleteById(String id) {
        String sql = """
                DELETE FROM users
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    public void assignRole(String userId, String roleId) {
        String sql = """
                INSERT INTO user_roles (
                    user_id,
                    role_id
                )
                VALUES (?, ?)
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, userId);
            statement.setString(2, roleId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to assign role", e);
        }
    }

    public void removeRole(String userId, String roleId) {
        String sql = """
                DELETE FROM user_roles
                WHERE user_id = ?
                  AND role_id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, userId);
            statement.setString(2, roleId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove role", e);
        }
    }

    private void loadRoles(User user) {
        String sql = """
                SELECT r.*
                FROM roles r
                INNER JOIN user_roles ur
                    ON r.id = ur.role_id
                WHERE ur.user_id = ?
                ORDER BY r.name
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, user.getId());

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Role role = new Role();

                    role.setId(result.getString("id"));
                    role.setName(result.getString("name"));
                    role.setDescription(
                            result.getString("description")
                    );

                    user.addRole(role);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to load user roles",
                    e
            );
        }
    }

    private User mapRow(ResultSet result) throws SQLException {
        User user = new User();

        user.setId(result.getString("id"));
        user.setUsername(result.getString("username"));
        user.setPasswordHash(
                result.getString("password_hash")
        );
        user.setStudentId(
                result.getString("student_id")
        );
        user.setActive(
                result.getBoolean("active")
        );

        Timestamp createdAt = result.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        Timestamp updatedAt = result.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(
                    updatedAt.toLocalDateTime()
            );
        }

        return user;
    }
}