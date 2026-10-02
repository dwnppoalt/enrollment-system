package org.dwnppo.enrollmentsystem.service;

import org.dwnppo.enrollmentsystem.model.Assessment;
import org.dwnppo.enrollmentsystem.model.Enrollment;
import org.dwnppo.enrollmentsystem.repository.AssessmentRepository;
import org.dwnppo.enrollmentsystem.repository.EnrollmentRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AssessmentService(
            AssessmentRepository assessmentRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.assessmentRepository = assessmentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public Assessment createAssessment(
            String enrollmentId,
            BigDecimal tuitionFee,
            BigDecimal miscellaneousFee,
            BigDecimal laboratoryFee,
            BigDecimal otherFee
    ) {

        validateEnrollmentId(enrollmentId);

        validateFee(tuitionFee, "Tuition fee");
        validateFee(miscellaneousFee, "Miscellaneous fee");
        validateFee(laboratoryFee, "Laboratory fee");
        validateFee(otherFee, "Other fee");

        validateEnrollmentExists(enrollmentId);

        if (assessmentRepository
                .findByEnrollmentId(enrollmentId)
                .isPresent()) {

            throw new IllegalStateException(
                    "This enrollment already has an assessment."
            );
        }

        Assessment assessment = new Assessment(
                UUID.randomUUID().toString(),
                enrollmentId,
                tuitionFee,
                miscellaneousFee,
                laboratoryFee,
                otherFee
        );

        assessmentRepository.save(assessment);

        return assessment;
    }

    public Assessment getAssessment(String assessmentId) {

        if (assessmentId == null || assessmentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Assessment ID is required."
            );
        }

        return assessmentRepository
                .findById(assessmentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Assessment does not exist."
                        )
                );
    }

    public Assessment getAssessmentByEnrollment(
            String enrollmentId
    ) {

        validateEnrollmentId(enrollmentId);

        return assessmentRepository
                .findByEnrollmentId(enrollmentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Assessment does not exist for this enrollment."
                        )
                );
    }

    public List<Assessment> getAllAssessments() {
        return assessmentRepository.findAll();
    }

    public void updateFees(
            String assessmentId,
            BigDecimal tuitionFee,
            BigDecimal miscellaneousFee,
            BigDecimal laboratoryFee,
            BigDecimal otherFee
    ) {

        Assessment assessment = getAssessment(assessmentId);

        validateFee(tuitionFee, "Tuition fee");
        validateFee(miscellaneousFee, "Miscellaneous fee");
        validateFee(laboratoryFee, "Laboratory fee");
        validateFee(otherFee, "Other fee");

        /*
         * Do not allow changing the assessment's fees
         * if a payment has already been made.
         */
        if (assessment.getAmountPaid()
                .compareTo(BigDecimal.ZERO) > 0) {

            throw new IllegalStateException(
                    "Cannot change assessment fees after payment has been made."
            );
        }

        assessment.setTuitionFee(tuitionFee);
        assessment.setMiscellaneousFee(miscellaneousFee);
        assessment.setLaboratoryFee(laboratoryFee);
        assessment.setOtherFee(otherFee);

        assessment.calculateAmounts();

        assessmentRepository.update(assessment);
    }

    public void recalculate(String assessmentId) {

        Assessment assessment = getAssessment(assessmentId);

        assessment.calculateAmounts();

        assessmentRepository.update(assessment);
    }

    private void validateEnrollmentId(String enrollmentId) {

        if (enrollmentId == null || enrollmentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Enrollment ID is required."
            );
        }
    }

    private void validateEnrollmentExists(String enrollmentId) {

        enrollmentRepository
                .findById(enrollmentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Enrollment does not exist."
                        )
                );
    }

    private void validateFee(
            BigDecimal amount,
            String fieldName
    ) {

        if (amount == null) {
            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be negative."
            );
        }
    }
}