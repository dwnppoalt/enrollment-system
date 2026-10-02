package org.dwnppo.enrollmentsystem.repository;

import org.dwnppo.enrollmentsystem.model.Payment;
import org.dwnppo.enrollmentsystem.util.db.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentRepository {

    public Optional<Payment> findById(String id) {
        String sql = """
                SELECT *
                FROM payments
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
            throw new RuntimeException("Failed to find payment", e);
        }

        return Optional.empty();
    }

    public List<Payment> findAll() {
        String sql = """
                SELECT *
                FROM payments
                ORDER BY paid_at DESC
                """;

        List<Payment> payments = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {
            while (result.next()) {
                payments.add(mapRow(result));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find payments", e);
        }

        return payments;
    }

    public List<Payment> findByAssessmentId(String assessmentId) {
        String sql = """
                SELECT *
                FROM payments
                WHERE assessment_id = ?
                ORDER BY paid_at DESC
                """;

        List<Payment> payments = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, assessmentId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    payments.add(mapRow(result));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find payments by assessment",
                    e
            );
        }

        return payments;
    }

    public List<Payment> findByCashierId(String cashierId) {
        String sql = """
                SELECT *
                FROM payments
                WHERE cashier_id = ?
                ORDER BY paid_at DESC
                """;

        List<Payment> payments = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, cashierId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    payments.add(mapRow(result));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find payments by cashier",
                    e
            );
        }

        return payments;
    }

    public void save(Payment payment) {
        String sql = """
                INSERT INTO payments (
                    id,
                    assessment_id,
                    cashier_id,
                    amount,
                    payment_method,
                    reference_number,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, payment.getId());
            statement.setString(2, payment.getAssessmentId());
            statement.setString(3, payment.getCashierId());

            statement.setBigDecimal(
                    4,
                    payment.getAmount()
            );

            statement.setString(
                    5,
                    payment.getPaymentMethod()
            );

            statement.setString(
                    6,
                    payment.getReferenceNumber()
            );

            statement.setString(
                    7,
                    payment.getStatus()
            );

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save payment", e);
        }
    }

    public void update(Payment payment) {
        String sql = """
                UPDATE payments
                SET amount = ?,
                    payment_method = ?,
                    reference_number = ?,
                    status = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setBigDecimal(
                    1,
                    payment.getAmount()
            );

            statement.setString(
                    2,
                    payment.getPaymentMethod()
            );

            statement.setString(
                    3,
                    payment.getReferenceNumber()
            );

            statement.setString(
                    4,
                    payment.getStatus()
            );

            statement.setString(
                    5,
                    payment.getId()
            );

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update payment", e);
        }
    }

    public void deleteById(String id) {
        String sql = """
                DELETE FROM payments
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete payment", e);
        }
    }

    private Payment mapRow(ResultSet result) throws SQLException {
        Payment payment = new Payment();

        payment.setId(result.getString("id"));
        payment.setAssessmentId(
                result.getString("assessment_id")
        );
        payment.setCashierId(
                result.getString("cashier_id")
        );

        payment.setAmount(
                result.getBigDecimal("amount")
        );

        payment.setPaymentMethod(
                result.getString("payment_method")
        );

        payment.setReferenceNumber(
                result.getString("reference_number")
        );

        Timestamp paidAt = result.getTimestamp("paid_at");
        if (paidAt != null) {
            payment.setPaidAt(
                    paidAt.toLocalDateTime()
            );
        }

        payment.setStatus(
                result.getString("status")
        );

        Timestamp createdAt = result.getTimestamp("created_at");
        if (createdAt != null) {
            payment.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        return payment;
    }
}