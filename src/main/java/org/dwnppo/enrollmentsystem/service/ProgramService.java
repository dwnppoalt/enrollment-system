package org.dwnppo.enrollmentsystem.service;

import org.dwnppo.enrollmentsystem.model.Program;
import org.dwnppo.enrollmentsystem.repository.ProgramRepository;

import java.util.List;

public class ProgramService {

    private final ProgramRepository programRepository;

    public ProgramService(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    public void createProgram(Program program) {

        validateProgram(program);

        if (programRepository.findByCode(program.getCode()).isPresent()) {
            throw new IllegalStateException(
                    "A program with this code already exists."
            );
        }

        if (program.getId() == null || program.getId().isBlank()) {
            throw new IllegalArgumentException(
                    "Program ID is required."
            );
        }

        programRepository.save(program);
    }

    public void updateProgram(Program program) {

        validateProgram(program);

        if (program.getId() == null || program.getId().isBlank()) {
            throw new IllegalArgumentException(
                    "Program ID is required."
            );
        }

        Program existing = programRepository
                .findById(program.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Program does not exist."
                        )
                );

        if (!existing.getCode().equalsIgnoreCase(program.getCode())) {

            if (programRepository
                    .findByCode(program.getCode())
                    .isPresent()) {

                throw new IllegalStateException(
                        "A program with this code already exists."
                );
            }
        }

        programRepository.update(program);
    }

    public void deleteProgram(String programId) {

        if (programId == null || programId.isBlank()) {
            throw new IllegalArgumentException(
                    "Program ID is required."
            );
        }

        if (programRepository.findById(programId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Program does not exist."
            );
        }

        programRepository.deleteById(programId);
    }

    public Program getProgram(String programId) {

        if (programId == null || programId.isBlank()) {
            throw new IllegalArgumentException(
                    "Program ID is required."
            );
        }

        return programRepository
                .findById(programId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Program does not exist."
                        )
                );
    }

    public Program getProgramByCode(String code) {

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(
                    "Program code is required."
            );
        }

        return programRepository
                .findByCode(code)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Program does not exist."
                        )
                );
    }

    public List<Program> getAllPrograms() {
        return programRepository.findAll();
    }

    public List<Program> getProgramsByCollege(String college) {

        if (college == null || college.isBlank()) {
            throw new IllegalArgumentException(
                    "College is required."
            );
        }

        return programRepository.findByCollege(college);
    }

    public List<Program> getProgramsByDepartment(String department) {

        if (department == null || department.isBlank()) {
            throw new IllegalArgumentException(
                    "Department is required."
            );
        }

        return programRepository.findByDepartment(department);
    }

    private void validateProgram(Program program) {

        if (program == null) {
            throw new IllegalArgumentException(
                    "Program is required."
            );
        }

        if (isBlank(program.getCode())) {
            throw new IllegalArgumentException(
                    "Program code is required."
            );
        }

        if (isBlank(program.getName())) {
            throw new IllegalArgumentException(
                    "Program name is required."
            );
        }

        if (program.getDurationYears() <= 0) {
            throw new IllegalArgumentException(
                    "Program duration must be greater than zero."
            );
        }

        if (isBlank(program.getDepartment())) {
            throw new IllegalArgumentException(
                    "Department is required."
            );
        }

        if (isBlank(program.getCollege())) {
            throw new IllegalArgumentException(
                    "College is required."
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}