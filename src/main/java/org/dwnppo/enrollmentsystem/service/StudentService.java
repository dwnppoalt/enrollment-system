package org.dwnppo.enrollmentsystem.service;

import org.dwnppo.enrollmentsystem.model.Program;
import org.dwnppo.enrollmentsystem.model.Student;
import org.dwnppo.enrollmentsystem.repository.ProgramRepository;
import org.dwnppo.enrollmentsystem.repository.StudentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class StudentService {

    private final StudentRepository studentRepository;
    private final ProgramRepository programRepository;

    public StudentService(
            StudentRepository studentRepository,
            ProgramRepository programRepository
    ) {
        this.studentRepository = studentRepository;
        this.programRepository = programRepository;
    }

    public void registerStudent(Student student) {

        validateStudent(student);

        if (student.getId() == null || student.getId().isBlank()) {
            student.setId(generateStudentId());
        }

        if (studentRepository.findById(student.getId()).isPresent()) {
            throw new IllegalStateException(
                    "Student ID already exists."
            );
        }

        validateProgram(student.getProgramId());

        studentRepository.save(student);
    }

    public void updateStudent(Student student) {

        validateStudent(student);

        if (student.getId() == null || student.getId().isBlank()) {
            throw new IllegalArgumentException(
                    "Student ID is required."
            );
        }

        if (studentRepository.findById(student.getId()).isEmpty()) {
            throw new IllegalArgumentException(
                    "Student does not exist."
            );
        }

        validateProgram(student.getProgramId());

        studentRepository.update(student);
    }

    public void deleteStudent(String studentId) {

        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Student ID is required."
            );
        }

        if (studentRepository.findById(studentId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Student does not exist."
            );
        }

        studentRepository.deleteById(studentId);
    }

    public Student getStudent(String studentId) {

        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Student ID is required."
            );
        }

        return studentRepository
                .findById(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student does not exist."
                        )
                );
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> getStudentsByProgram(String programId) {

        if (programId == null || programId.isBlank()) {
            throw new IllegalArgumentException(
                    "Program ID is required."
            );
        }

        return studentRepository.findByProgramId(programId);
    }

    private void validateStudent(Student student) {

        if (student == null) {
            throw new IllegalArgumentException(
                    "Student is required."
            );
        }

        if (isBlank(student.getFirstName())) {
            throw new IllegalArgumentException(
                    "First name is required."
            );
        }

        if (isBlank(student.getLastName())) {
            throw new IllegalArgumentException(
                    "Last name is required."
            );
        }

        if (student.getBirthDate() == null) {
            throw new IllegalArgumentException(
                    "Birth date is required."
            );
        }

        if (student.getBirthDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Birth date cannot be in the future."
            );
        }

        if (isBlank(student.getSex())) {
            throw new IllegalArgumentException(
                    "Sex is required."
            );
        }

        if (isBlank(student.getProgramId())) {
            throw new IllegalArgumentException(
                    "Program is required."
            );
        }

        if (!isBlank(student.getEmail())
                && !student.getEmail().contains("@")) {
            throw new IllegalArgumentException(
                    "Invalid email address."
            );
        }
    }

    private void validateProgram(String programId) {

        Program program = programRepository
                .findById(programId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Selected program does not exist."
                        )
                );
    }

    private String generateStudentId() {

        /*
         * Current format:
         *
         * 26-0001
         *
         * The first part represents the year.
         * The second part is a random 4-digit number.
         *
         * This is intentionally simple for the project.
         */

        String year = String.format(
                "%02d",
                LocalDate.now().getYear() % 100
        );

        for (int i = 0; i < 10000; i++) {

            int number = (int) (Math.random() * 10000);

            String studentId = String.format(
                    "%s-%04d",
                    year,
                    number
            );

            if (studentRepository.findById(studentId).isEmpty()) {
                return studentId;
            }
        }

        throw new IllegalStateException(
                "Unable to generate a unique student ID."
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}