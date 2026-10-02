package org.dwnppo.enrollmentsystem.repository;

import org.dwnppo.enrollmentsystem.model.AcademicTerm;
import org.dwnppo.enrollmentsystem.util.db.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AcademicTermRepository {

    public Optional<AcademicTerm> findById(String id) {
        String sql = """
                SELECT *
                FROM academic_terms
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
            throw new RuntimeException("Failed to find academic term", e);
        }

        return Optional.empty();
    }

    public List<AcademicTerm> findAll() {
        String sql = """
                SELECT *
                FROM academic_terms
                ORDER BY school_year DESC, semester
                """;

        List<AcademicTerm> terms = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {
            while (result.next()) {
                terms.add(mapRow(result));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find academic terms", e);
        }

        return terms;
    }

    public List<AcademicTerm> findBySchoolYear(String schoolYear) {
        String sql = """
                SELECT *
                FROM academic_terms
                WHERE school_year = ?
                ORDER BY semester
                """;

        List<AcademicTerm> terms = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, schoolYear);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    terms.add(mapRow(result));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find academic terms by school year", e);
        }

        return terms;
    }

    public Optional<AcademicTerm> findBySchoolYearAndSemester(
            String schoolYear,
            String semester
    ) {
        String sql = """
                SELECT *
                FROM academic_terms
                WHERE school_year = ?
                  AND semester = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, schoolYear);
            statement.setString(2, semester);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapRow(result));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find academic term", e);
        }

        return Optional.empty();
    }

    public void save(AcademicTerm term) {
        String sql = """
                INSERT INTO academic_terms (
                    id,
                    school_year,
                    semester,
                    start_date,
                    end_date
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, term.getId());
            statement.setString(2, term.getSchoolYear());
            statement.setString(3, term.getSemester());

            if (term.getStartDate() != null) {
                statement.setDate(
                        4,
                        Date.valueOf(term.getStartDate())
                );
            } else {
                statement.setNull(4, Types.DATE);
            }

            if (term.getEndDate() != null) {
                statement.setDate(
                        5,
                        Date.valueOf(term.getEndDate())
                );
            } else {
                statement.setNull(5, Types.DATE);
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save academic term", e);
        }
    }

    public void update(AcademicTerm term) {
        String sql = """
                UPDATE academic_terms
                SET school_year = ?,
                    semester = ?,
                    start_date = ?,
                    end_date = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, term.getSchoolYear());
            statement.setString(2, term.getSemester());

            if (term.getStartDate() != null) {
                statement.setDate(
                        3,
                        Date.valueOf(term.getStartDate())
                );
            } else {
                statement.setNull(3, Types.DATE);
            }

            if (term.getEndDate() != null) {
                statement.setDate(
                        4,
                        Date.valueOf(term.getEndDate())
                );
            } else {
                statement.setNull(4, Types.DATE);
            }

            statement.setString(5, term.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update academic term", e);
        }
    }

    public void deleteById(String id) {
        String sql = """
                DELETE FROM academic_terms
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete academic term", e);
        }
    }

    private AcademicTerm mapRow(ResultSet result) throws SQLException {
        AcademicTerm term = new AcademicTerm();

        term.setId(result.getString("id"));
        term.setSchoolYear(result.getString("school_year"));
        term.setSemester(result.getString("semester"));

        Date startDate = result.getDate("start_date");
        if (startDate != null) {
            term.setStartDate(startDate.toLocalDate());
        }

        Date endDate = result.getDate("end_date");
        if (endDate != null) {
            term.setEndDate(endDate.toLocalDate());
        }

        Timestamp createdAt = result.getTimestamp("created_at");
        if (createdAt != null) {
            term.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = result.getTimestamp("updated_at");
        if (updatedAt != null) {
            term.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return term;
    }
}