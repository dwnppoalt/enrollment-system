package org.dwnppo.enrollmentsystem.repository;

import org.dwnppo.enrollmentsystem.model.Student;
import org.dwnppo.enrollmentsystem.util.db.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentRepository {
    public Optional<Student> findById(String id) {

        String sql = """
                SELECT *
                FROM students
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
            throw new RuntimeException("Failed to find student: " + id, e);
        }
    }

    public List<Student> findAll() {

        String sql = """
                SELECT *
                FROM students
                ORDER BY last_name, first_name
                """;

        List<Student> students = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {

            while (result.next()) {
                students.add(mapRow(result));
            }

            return students;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve students", e);
        }
    }

    public List<Student> findByProgramId(String programId) {

        String sql = """
                SELECT *
                FROM students
                WHERE program_id = ?
                ORDER BY last_name, first_name
                """;

        List<Student> students = new ArrayList<>();

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, programId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    students.add(mapRow(result));
                }

            }

            return students;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find students for program: " + programId,
                    e
            );
        }
    }

    public void save(Student student) {

        String sql = """
                INSERT INTO students (
                    id,
                    first_name,
                    middle_name,
                    last_name,
                    suffix,
                    birth_date,
                    sex,
                    email,
                    phone,
                    address,
                    program_id
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, student.getId());
            statement.setString(2, student.getFirstName());
            statement.setString(3, student.getMiddleName());
            statement.setString(4, student.getLastName());
            statement.setString(5, student.getSuffix());
            statement.setDate(
                    6,
                    Date.valueOf(student.getBirthDate())
            );
            statement.setString(7, student.getSex());
            statement.setString(8, student.getEmail());
            statement.setString(9, student.getPhone());
            statement.setString(10, student.getAddress());
            statement.setString(11, student.getProgramId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save student", e);
        }
    }

    public void update(Student student) {

        String sql = """
                UPDATE students
                SET
                    first_name = ?,
                    middle_name = ?,
                    last_name = ?,
                    suffix = ?,
                    birth_date = ?,
                    sex = ?,
                    email = ?,
                    phone = ?,
                    address = ?,
                    program_id = ?
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getMiddleName());
            statement.setString(3, student.getLastName());
            statement.setString(4, student.getSuffix());
            statement.setDate(
                    5,
                    Date.valueOf(student.getBirthDate())
            );
            statement.setString(6, student.getSex());
            statement.setString(7, student.getEmail());
            statement.setString(8, student.getPhone());
            statement.setString(9, student.getAddress());
            statement.setString(10, student.getProgramId());
            statement.setString(11, student.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update student", e);
        }
    }

    public void deleteById(String id) {

        String sql = """
                DELETE FROM students
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
                    "Failed to delete student: " + id,
                    e
            );
        }
    }

    private Student mapRow(ResultSet result) throws SQLException {

        Student student = new Student();

        student.setId(result.getString("id"));
        student.setFirstName(result.getString("first_name"));
        student.setMiddleName(result.getString("middle_name"));
        student.setLastName(result.getString("last_name"));
        student.setSuffix(result.getString("suffix"));

        Date birthDate = result.getDate("birth_date");

        if (birthDate != null) {
            student.setBirthDate(birthDate.toLocalDate());
        }

        student.setSex(result.getString("sex"));
        student.setEmail(result.getString("email"));
        student.setPhone(result.getString("phone"));
        student.setAddress(result.getString("address"));
        student.setProgramId(result.getString("program_id"));

        Timestamp createdAt = result.getTimestamp("created_at");
        Timestamp updatedAt = result.getTimestamp("updated_at");

        if (createdAt != null) {
            student.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            student.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return student;
    }
}