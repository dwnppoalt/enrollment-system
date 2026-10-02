package org.dwnppo.enrollmentsystem.repository;

import org.dwnppo.enrollmentsystem.model.Program;
import org.dwnppo.enrollmentsystem.util.db.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProgramRepository {

    public Optional<Program> findById(String id) {

        String sql = """
                SELECT *
                FROM programs
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

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find program: " + id,
                    e
            );
        }
    }

    public Optional<Program> findByCode(String code) {

        String sql = """
                SELECT *
                FROM programs
                WHERE code = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, code);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return Optional.of(mapRow(result));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find program with code: " + code,
                    e
            );
        }
    }

    public List<Program> findAll() {

        String sql = """
                SELECT *
                FROM programs
                ORDER BY college, name
                """;

        List<Program> programs = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {

            while (result.next()) {
                programs.add(mapRow(result));
            }

            return programs;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to retrieve programs",
                    e
            );
        }
    }

    public List<Program> findByCollege(String college) {

        String sql = """
                SELECT *
                FROM programs
                WHERE college = ?
                ORDER BY name
                """;

        List<Program> programs = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, college);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    programs.add(mapRow(result));
                }
            }

            return programs;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find programs for college: " + college,
                    e
            );
        }
    }

    public List<Program> findByDepartment(String department) {

        String sql = """
                SELECT *
                FROM programs
                WHERE department = ?
                ORDER BY name
                """;

        List<Program> programs = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, department);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    programs.add(mapRow(result));
                }
            }

            return programs;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find programs for department: " + department,
                    e
            );
        }
    }

    public void save(Program program) {

        String sql = """
                INSERT INTO programs (
                    id,
                    code,
                    name,
                    description,
                    duration_years,
                    department,
                    college
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, program.getId());
            statement.setString(2, program.getCode());
            statement.setString(3, program.getName());
            statement.setString(4, program.getDescription());
            statement.setInt(5, program.getDurationYears());
            statement.setString(6, program.getDepartment());
            statement.setString(7, program.getCollege());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to save program",
                    e
            );
        }
    }

    public void update(Program program) {

        String sql = """
                UPDATE programs
                SET
                    code = ?,
                    name = ?,
                    description = ?,
                    duration_years = ?,
                    department = ?,
                    college = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, program.getCode());
            statement.setString(2, program.getName());
            statement.setString(3, program.getDescription());
            statement.setInt(4, program.getDurationYears());
            statement.setString(5, program.getDepartment());
            statement.setString(6, program.getCollege());
            statement.setString(7, program.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to update program: " + program.getId(),
                    e
            );
        }
    }

    public void deleteById(String id) {

        String sql = """
                DELETE FROM programs
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, id);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to delete program: " + id,
                    e
            );
        }
    }

    private Program mapRow(ResultSet result) throws SQLException {

        Program program = new Program();

        program.setId(result.getString("id"));
        program.setCode(result.getString("code"));
        program.setName(result.getString("name"));
        program.setDescription(result.getString("description"));
        program.setDurationYears(result.getInt("duration_years"));
        program.setDepartment(result.getString("department"));
        program.setCollege(result.getString("college"));

        Timestamp createdAt = result.getTimestamp("created_at");
        Timestamp updatedAt = result.getTimestamp("updated_at");

        if (createdAt != null) {
            program.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        if (updatedAt != null) {
            program.setUpdatedAt(
                    updatedAt.toLocalDateTime()
            );
        }

        return program;
    }
}