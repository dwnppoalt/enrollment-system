package org.dwnppo.enrollmentsystem.repository;

import org.dwnppo.enrollmentsystem.model.Course;
import org.dwnppo.enrollmentsystem.util.db.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseRepository {

    public Optional<Course> findById(String id) {
        String sql = """
                SELECT *
                FROM courses
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
            throw new RuntimeException("Failed to find course", e);
        }

        return Optional.empty();
    }

    public Optional<Course> findByCode(String code) {
        String sql = """
                SELECT *
                FROM courses
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
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find course by code", e);
        }

        return Optional.empty();
    }

    public List<Course> findAll() {
        String sql = """
                SELECT *
                FROM courses
                ORDER BY code
                """;

        List<Course> courses = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {
            while (result.next()) {
                courses.add(mapRow(result));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find courses", e);
        }

        return courses;
    }

    public List<Course> search(String keyword) {
        String sql = """
                SELECT *
                FROM courses
                WHERE code LIKE ?
                   OR name LIKE ?
                ORDER BY code
                """;

        List<Course> courses = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            String searchPattern = "%" + keyword + "%";

            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    courses.add(mapRow(result));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to search courses", e);
        }

        return courses;
    }

    public void save(Course course) {
        String sql = """
                INSERT INTO courses (
                    id,
                    code,
                    name,
                    description,
                    units
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, course.getId());
            statement.setString(2, course.getCode());
            statement.setString(3, course.getName());
            statement.setString(4, course.getDescription());
            statement.setDouble(5, course.getUnits());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save course", e);
        }
    }

    public void update(Course course) {
        String sql = """
                UPDATE courses
                SET code = ?,
                    name = ?,
                    description = ?,
                    units = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, course.getCode());
            statement.setString(2, course.getName());
            statement.setString(3, course.getDescription());
            statement.setDouble(4, course.getUnits());
            statement.setString(5, course.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update course", e);
        }
    }

    public void deleteById(String id) {
        String sql = """
                DELETE FROM courses
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete course", e);
        }
    }

    private Course mapRow(ResultSet result) throws SQLException {
        Course course = new Course();

        course.setId(result.getString("id"));
        course.setCode(result.getString("code"));
        course.setName(result.getString("name"));
        course.setDescription(result.getString("description"));
        course.setUnits(result.getDouble("units"));

        Timestamp createdAt = result.getTimestamp("created_at");
        if (createdAt != null) {
            course.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = result.getTimestamp("updated_at");
        if (updatedAt != null) {
            course.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return course;
    }
}