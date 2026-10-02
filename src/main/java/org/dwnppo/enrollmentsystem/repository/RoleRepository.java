package org.dwnppo.enrollmentsystem.repository;

import org.dwnppo.enrollmentsystem.model.Role;
import org.dwnppo.enrollmentsystem.util.db.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleRepository {

    public Optional<Role> findById(String id) {
        String sql = """
                SELECT *
                FROM roles
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapRow(result));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find role", e);
        }

        return Optional.empty();
    }

    public Optional<Role> findByName(String name) {
        String sql = """
                SELECT *
                FROM roles
                WHERE name = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, name);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapRow(result));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find role by name", e);
        }

        return Optional.empty();
    }

    public List<Role> findAll() {
        String sql = """
                SELECT *
                FROM roles
                ORDER BY name
                """;

        List<Role> roles = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {
            while (result.next()) {
                roles.add(mapRow(result));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find roles", e);
        }

        return roles;
    }

    public void save(Role role) {
        String sql = """
                INSERT INTO roles (
                    id,
                    name,
                    description
                )
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, role.getId());
            statement.setString(2, role.getName());
            statement.setString(3, role.getDescription());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save role", e);
        }
    }

    public void update(Role role) {
        String sql = """
                UPDATE roles
                SET name = ?,
                    description = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, role.getName());
            statement.setString(2, role.getDescription());
            statement.setString(3, role.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update role", e);
        }
    }

    public void deleteById(String id) {
        String sql = """
                DELETE FROM roles
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete role", e);
        }
    }

    private Role mapRow(ResultSet result) throws SQLException {
        Role role = new Role();

        role.setId(result.getString("id"));
        role.setName(result.getString("name"));
        role.setDescription(result.getString("description"));

        return role;
    }
}