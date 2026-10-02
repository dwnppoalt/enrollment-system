package org.dwnppo.enrollmentsystem.service;

import org.dwnppo.enrollmentsystem.model.AcademicTerm;
import org.dwnppo.enrollmentsystem.model.Enrollment;
import org.dwnppo.enrollmentsystem.model.Student;
import org.dwnppo.enrollmentsystem.repository.AcademicTermRepository;
import org.dwnppo.enrollmentsystem.repository.EnrollmentRepository;
import org.dwnppo.enrollmentsystem.repository.StudentRepository;

import java.util.List;
import java.util.UUID;

public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final AcademicTermRepository academicTermRepository;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            StudentRepository studentRepository,
            AcademicTermRepository academicTermRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.academicTermRepository = academicTermRepository;
    }

    public Enrollment enrollStudent(
            String studentId,
            String termId
    ) {

        validateStudentId(studentId);
        validateTermId(termId);

        validateStudentExists(studentId);
        validateTermExists(termId);

        if (enrollmentRepository
                .findByStudentAndTerm(studentId, termId)
                .isPresent()) {

            throw new IllegalStateException(
                    "Student is already enrolled for this academic term."
            );
        }

        Enrollment enrollment = new Enrollment(
                UUID.randomUUID().toString(),
                studentId,
                termId,
                "ENROLLED"
        );

        enrollmentRepository.save(enrollment);

        return enrollment;
    }

    public Enrollment getEnrollment(String enrollmentId) {

        if (enrollmentId == null || enrollmentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Enrollment ID is required."
            );
        }

        return enrollmentRepository
                .findById(enrollmentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Enrollment does not exist."
                        )
                );
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public List<Enrollment> getStudentEnrollments(
            String studentId
    ) {

        validateStudentId(studentId);
        validateStudentExists(studentId);

        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> getTermEnrollments(
            String termId
    ) {

        validateTermId(termId);
        validateTermExists(termId);

        return enrollmentRepository.findByTermId(termId);
    }

    public void updateStatus(
            String enrollmentId,
            String status
    ) {

        if (enrollmentId == null || enrollmentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Enrollment ID is required."
            );
        }

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "Enrollment status is required."
            );
        }

        Enrollment enrollment = enrollmentRepository
                .findById(enrollmentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Enrollment does not exist."
                        )
                );

        enrollment.setStatus(status);

        enrollmentRepository.update(enrollment);
    }

    public void cancelEnrollment(String enrollmentId) {

        Enrollment enrollment = getEnrollment(enrollmentId);

        if ("CANCELLED".equalsIgnoreCase(enrollment.getStatus())) {
            throw new IllegalStateException(
                    "Enrollment is already cancelled."
            );
        }

        enrollment.setStatus("CANCELLED");

        enrollmentRepository.update(enrollment);
    }

    private void validateStudentId(String studentId) {

        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Student ID is required."
            );
        }
    }

    private void validateTermId(String termId) {

        if (termId == null || termId.isBlank()) {
            throw new IllegalArgumentException(
                    "Academic term is required."
            );
        }
    }

    private void validateStudentExists(String studentId) {

        studentRepository
                .findById(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student does not exist."
                        )
                );
    }

    private void validateTermExists(String termId) {

        academicTermRepository
                .findById(termId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Academic term does not exist."
                        )
                );
    }
}