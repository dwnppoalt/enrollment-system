package org.dwnppo.enrollmentsystem.service;

import org.dwnppo.enrollmentsystem.model.AcademicTerm;
import org.dwnppo.enrollmentsystem.repository.AcademicTermRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class AcademicTermService {

    private final AcademicTermRepository academicTermRepository;

    public AcademicTermService(
            AcademicTermRepository academicTermRepository
    ) {
        this.academicTermRepository =
                academicTermRepository;
    }

    public AcademicTerm getTerm(
            String id
    ) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "Academic term ID is required."
            );
        }

        return academicTermRepository
                .findById(id)
                .orElse(null);
    }

    public AcademicTerm getTermBySchoolYearAndSemester(
            String schoolYear,
            String semester
    ) {

        validateSchoolYear(schoolYear);
        validateSemester(semester);

        return academicTermRepository
                .findBySchoolYearAndSemester(
                        schoolYear,
                        semester
                )
                .orElse(null);
    }

    public List<AcademicTerm> getAllTerms() {

        return academicTermRepository.findAll();
    }

    public List<AcademicTerm> getTermsBySchoolYear(
            String schoolYear
    ) {

        validateSchoolYear(schoolYear);

        return academicTermRepository
                .findBySchoolYear(schoolYear);
    }

    public void createTerm(
            AcademicTerm term
    ) {

        if (term == null) {
            throw new IllegalArgumentException(
                    "Academic term is required."
            );
        }

        validateTerm(term);

        /*
         * Generate an ID if one was not supplied.
         */
        if (term.getId() == null
                || term.getId().isBlank()) {

            term.setId(
                    UUID.randomUUID().toString()
            );
        }

        /*
         * Prevent duplicate school year + semester.
         */
        AcademicTerm existing =
                academicTermRepository
                        .findBySchoolYearAndSemester(
                                term.getSchoolYear(),
                                term.getSemester()
                        )
                        .orElse(null);

        if (existing != null) {

            throw new IllegalArgumentException(
                    "An academic term for "
                            + term.getSchoolYear()
                            + " "
                            + term.getSemester()
                            + " already exists."
            );
        }

        academicTermRepository.save(term);
    }

    public void updateTerm(
            AcademicTerm term
    ) {

        if (term == null) {
            throw new IllegalArgumentException(
                    "Academic term is required."
            );
        }

        if (term.getId() == null
                || term.getId().isBlank()) {

            throw new IllegalArgumentException(
                    "Academic term ID is required."
            );
        }

        validateTerm(term);

        /*
         * Make sure the term actually exists.
         */
        AcademicTerm existingById =
                academicTermRepository
                        .findById(term.getId())
                        .orElse(null);

        if (existingById == null) {

            throw new IllegalArgumentException(
                    "Academic term does not exist."
            );
        }

        /*
         * Prevent changing the term into an already
         * existing school year + semester combination.
         */
        AcademicTerm duplicate =
                academicTermRepository
                        .findBySchoolYearAndSemester(
                                term.getSchoolYear(),
                                term.getSemester()
                        )
                        .orElse(null);

        if (duplicate != null
                && !duplicate.getId().equals(
                term.getId()
        )) {

            throw new IllegalArgumentException(
                    "An academic term for "
                            + term.getSchoolYear()
                            + " "
                            + term.getSemester()
                            + " already exists."
            );
        }

        academicTermRepository.update(term);
    }

    public void deleteTerm(
            String id
    ) {

        if (id == null || id.isBlank()) {

            throw new IllegalArgumentException(
                    "Academic term ID is required."
            );
        }

        AcademicTerm existing =
                academicTermRepository
                        .findById(id)
                        .orElse(null);

        if (existing == null) {

            throw new IllegalArgumentException(
                    "Academic term does not exist."
            );
        }

        academicTermRepository.deleteById(id);
    }

    private void validateTerm(
            AcademicTerm term
    ) {

        validateSchoolYear(
                term.getSchoolYear()
        );

        validateSemester(
                term.getSemester()
        );

        LocalDate startDate =
                term.getStartDate();

        LocalDate endDate =
                term.getEndDate();

        if (startDate != null
                && endDate != null
                && endDate.isBefore(startDate)) {

            throw new IllegalArgumentException(
                    "End date cannot be earlier than start date."
            );
        }
    }

    private void validateSchoolYear(
            String schoolYear
    ) {

        if (schoolYear == null
                || schoolYear.isBlank()) {

            throw new IllegalArgumentException(
                    "School year is required."
            );
        }

        if (!schoolYear.matches(
                "\\d{4}-\\d{4}"
        )) {

            throw new IllegalArgumentException(
                    "School year must use the format YYYY-YYYY."
            );
        }

        String[] years =
                schoolYear.split("-");

        int startYear =
                Integer.parseInt(years[0]);

        int endYear =
                Integer.parseInt(years[1]);

        if (endYear != startYear + 1) {

            throw new IllegalArgumentException(
                    "School year must contain consecutive years."
            );
        }
    }

    private void validateSemester(
            String semester
    ) {

        if (semester == null
                || semester.isBlank()) {

            throw new IllegalArgumentException(
                    "Semester is required."
            );
        }

        if (!semester.equals("1st")
                && !semester.equals("2nd")
                && !semester.equals("Summer")) {

            throw new IllegalArgumentException(
                    "Invalid semester."
            );
        }
    }
}