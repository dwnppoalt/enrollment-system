package org.dwnppo.enrollmentsystem.repository;

import org.dwnppo.enrollmentsystem.model.Enrollment;
import org.dwnppo.enrollmentsystem.util.db.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentRepository {

    public Optional<Enrollment> findById(String id) {
        String sql = """
                SELECT *
                FROM enrollments
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
            throw new RuntimeException("Failed to find enrollment", e);
        }

        return Optional.empty();
    }

    public List<Enrollment> findAll() {
        String sql = """
                SELECT *
                FROM enrollments
                ORDER BY enrollment_date DESC
                """;

        List<Enrollment> enrollments = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {
            while (result.next()) {
                enrollments.add(mapRow(result));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find enrollments", e);
        }

        return enrollments;
    }

    public List<Enrollment> findByStudentId(String studentId) {
        String sql = """
                SELECT *
                FROM enrollments
                WHERE student_id = ?
                ORDER BY enrollment_date DESC
                """;

        List<Enrollment> enrollments = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, studentId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    enrollments.add(mapRow(result));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find enrollments by student",
                    e
            );
        }

        return enrollments;
    }

    public List<Enrollment> findByTermId(String termId) {
        String sql = """
                SELECT *
                FROM enrollments
                WHERE term_id = ?
                ORDER BY enrollment_date DESC
                """;

        List<Enrollment> enrollments = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, termId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    enrollments.add(mapRow(result));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find enrollments by academic term",
                    e
            );
        }

        return enrollments;
    }

    public Optional<Enrollment> findByStudentAndTerm(
            String studentId,
            String termId
    ) {
        String sql = """
                SELECT *
                FROM enrollments
                WHERE student_id = ?
                  AND term_id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, studentId);
            statement.setString(2, termId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapRow(result));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find enrollment by student and term",
                    e
            );
        }

        return Optional.empty();
    }

    public void save(Enrollment enrollment) {
        String sql = """
                INSERT INTO enrollments (
                    id,
                    student_id,
                    term_id,
                    status
                )
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, enrollment.getId());
            statement.setString(2, enrollment.getStudentId());
            statement.setString(3, enrollment.getTermId());
            statement.setString(4, enrollment.getStatus());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save enrollment", e);
        }
    }

    public void update(Enrollment enrollment) {
        String sql = """
                UPDATE enrollments
                SET student_id = ?,
                    term_id = ?,
                    status = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, enrollment.getStudentId());
            statement.setString(2, enrollment.getTermId());
            statement.setString(3, enrollment.getStatus());
            statement.setString(4, enrollment.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update enrollment", e);
        }
    }

    public void deleteById(String id) {
        String sql = """
                DELETE FROM enrollments
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete enrollment", e);
        }
    }

    private Enrollment mapRow(ResultSet result) throws SQLException {
        Enrollment enrollment = new Enrollment();

        enrollment.setId(result.getString("id"));
        enrollment.setStudentId(result.getString("student_id"));
        enrollment.setTermId(result.getString("term_id"));

        Timestamp enrollmentDate = result.getTimestamp("enrollment_date");
        if (enrollmentDate != null) {
            enrollment.setEnrollmentDate(
                    enrollmentDate.toLocalDateTime()
            );
        }

        enrollment.setStatus(result.getString("status"));

        Timestamp createdAt = result.getTimestamp("created_at");
        if (createdAt != null) {
            enrollment.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        Timestamp updatedAt = result.getTimestamp("updated_at");
        if (updatedAt != null) {
            enrollment.setUpdatedAt(
                    updatedAt.toLocalDateTime()
            );
        }

        return enrollment;
    }
}