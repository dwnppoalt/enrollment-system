package org.dwnppo.enrollmentsystem.repository;

import org.dwnppo.enrollmentsystem.model.Assessment;
import org.dwnppo.enrollmentsystem.util.db.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssessmentRepository {

    public Optional<Assessment> findById(String id) {
        String sql = """
                SELECT *
                FROM assessments
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
            throw new RuntimeException("Failed to find assessment", e);
        }

        return Optional.empty();
    }

    public Optional<Assessment> findByEnrollmentId(String enrollmentId) {
        String sql = """
                SELECT *
                FROM assessments
                WHERE enrollment_id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, enrollmentId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapRow(result));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find assessment by enrollment",
                    e
            );
        }

        return Optional.empty();
    }

    public List<Assessment> findAll() {
        String sql = """
                SELECT *
                FROM assessments
                ORDER BY created_at DESC
                """;

        List<Assessment> assessments = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {
            while (result.next()) {
                assessments.add(mapRow(result));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find assessments", e);
        }

        return assessments;
    }

    public void save(Assessment assessment) {
        String sql = """
                INSERT INTO assessments (
                    id,
                    enrollment_id,
                    tuition_fee,
                    miscellaneous_fee,
                    laboratory_fee,
                    other_fee,
                    total_amount,
                    amount_paid,
                    balance,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, assessment.getId());
            statement.setString(2, assessment.getEnrollmentId());

            statement.setBigDecimal(
                    3,
                    assessment.getTuitionFee()
            );

            statement.setBigDecimal(
                    4,
                    assessment.getMiscellaneousFee()
            );

            statement.setBigDecimal(
                    5,
                    assessment.getLaboratoryFee()
            );

            statement.setBigDecimal(
                    6,
                    assessment.getOtherFee()
            );

            statement.setBigDecimal(
                    7,
                    assessment.getTotalAmount()
            );

            statement.setBigDecimal(
                    8,
                    assessment.getAmountPaid()
            );

            statement.setBigDecimal(
                    9,
                    assessment.getBalance()
            );

            statement.setString(
                    10,
                    assessment.getStatus()
            );

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save assessment", e);
        }
    }

    public void update(Assessment assessment) {
        String sql = """
                UPDATE assessments
                SET tuition_fee = ?,
                    miscellaneous_fee = ?,
                    laboratory_fee = ?,
                    other_fee = ?,
                    total_amount = ?,
                    amount_paid = ?,
                    balance = ?,
                    status = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setBigDecimal(
                    1,
                    assessment.getTuitionFee()
            );

            statement.setBigDecimal(
                    2,
                    assessment.getMiscellaneousFee()
            );

            statement.setBigDecimal(
                    3,
                    assessment.getLaboratoryFee()
            );

            statement.setBigDecimal(
                    4,
                    assessment.getOtherFee()
            );

            statement.setBigDecimal(
                    5,
                    assessment.getTotalAmount()
            );

            statement.setBigDecimal(
                    6,
                    assessment.getAmountPaid()
            );

            statement.setBigDecimal(
                    7,
                    assessment.getBalance()
            );

            statement.setString(
                    8,
                    assessment.getStatus()
            );

            statement.setString(
                    9,
                    assessment.getId()
            );

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update assessment", e);
        }
    }

    public void deleteById(String id) {
        String sql = """
                DELETE FROM assessments
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete assessment", e);
        }
    }

    private Assessment mapRow(ResultSet result) throws SQLException {
        Assessment assessment = new Assessment();

        assessment.setId(result.getString("id"));
        assessment.setEnrollmentId(
                result.getString("enrollment_id")
        );

        assessment.setTuitionFee(
                result.getBigDecimal("tuition_fee")
        );

        assessment.setMiscellaneousFee(
                result.getBigDecimal("miscellaneous_fee")
        );

        assessment.setLaboratoryFee(
                result.getBigDecimal("laboratory_fee")
        );

        assessment.setOtherFee(
                result.getBigDecimal("other_fee")
        );

        assessment.setTotalAmount(
                result.getBigDecimal("total_amount")
        );

        assessment.setAmountPaid(
                result.getBigDecimal("amount_paid")
        );

        assessment.setBalance(
                result.getBigDecimal("balance")
        );

        assessment.setStatus(
                result.getString("status")
        );

        Timestamp createdAt = result.getTimestamp("created_at");
        if (createdAt != null) {
            assessment.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        Timestamp updatedAt = result.getTimestamp("updated_at");
        if (updatedAt != null) {
            assessment.setUpdatedAt(
                    updatedAt.toLocalDateTime()
            );
        }

        return assessment;
    }
}