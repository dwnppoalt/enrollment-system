package org.dwnppo.enrollmentsystem.ui.programs;

import org.dwnppo.enrollmentsystem.model.Program;
import org.dwnppo.enrollmentsystem.service.ProgramService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProgramPanel extends JPanel {

    private final ProgramService programService;

    private final DefaultTableModel tableModel;
    private final JTable programTable;
    private final JTextField searchField;

    public ProgramPanel(ProgramService programService) {

        this.programService = programService;

        setLayout(new BorderLayout(10, 10));

        setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        15,
                        15,
                        15
                )
        );

        searchField = new JTextField();

        tableModel = new DefaultTableModel(
                new Object[]{
                        "Code",
                        "Program Name",
                        "Department",
                        "College",
                        "Duration"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        programTable =
                new JTable(tableModel);

        programTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        programTable.setRowHeight(30);

        add(
                createTopPanel(),
                BorderLayout.NORTH
        );

        add(
                new JScrollPane(programTable),
                BorderLayout.CENTER
        );

        add(
                createBottomPanel(),
                BorderLayout.SOUTH
        );

        loadPrograms();
    }

    private JPanel createTopPanel() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(10, 10)
                );

        JLabel title =
                new JLabel("Program Management");

        title.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        22
                )
        );

        JPanel searchPanel =
                new JPanel(
                        new BorderLayout(5, 5)
                );

        searchPanel.add(
                new JLabel("Search:"),
                BorderLayout.WEST
        );

        searchPanel.add(
                searchField,
                BorderLayout.CENTER
        );

        JButton searchButton =
                new JButton("Search");

        searchButton.addActionListener(
                event -> searchPrograms()
        );

        searchPanel.add(
                searchButton,
                BorderLayout.EAST
        );

        panel.add(
                title,
                BorderLayout.NORTH
        );

        panel.add(
                searchPanel,
                BorderLayout.SOUTH
        );

        return panel;
    }

    private JPanel createBottomPanel() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        JButton addButton =
                new JButton("Add Program");

        JButton editButton =
                new JButton("Edit");

        JButton deleteButton =
                new JButton("Delete");

        JButton refreshButton =
                new JButton("Refresh");

        addButton.addActionListener(
                event -> showProgramDialog(null)
        );

        editButton.addActionListener(
                event -> editSelectedProgram()
        );

        deleteButton.addActionListener(
                event -> deleteSelectedProgram()
        );

        refreshButton.addActionListener(
                event -> loadPrograms()
        );

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);

        return panel;
    }

    private void loadPrograms() {

        try {

            List<Program> programs =
                    programService.getAllPrograms();

            displayPrograms(programs);

        } catch (Exception exception) {

            showError(
                    "Failed to load programs.",
                    exception
            );
        }
    }

    private void searchPrograms() {

        String search =
                searchField
                        .getText()
                        .trim()
                        .toLowerCase();

        if (search.isBlank()) {
            loadPrograms();
            return;
        }

        try {

            List<Program> programs =
                    programService.getAllPrograms()
                            .stream()
                            .filter(program ->
                                    matchesSearch(
                                            program,
                                            search
                                    )
                            )
                            .toList();

            displayPrograms(programs);

        } catch (Exception exception) {

            showError(
                    "Failed to search programs.",
                    exception
            );
        }
    }

    private boolean matchesSearch(
            Program program,
            String search
    ) {

        return contains(
                program.getCode(),
                search
        )
                || contains(
                program.getName(),
                search
        )
                || contains(
                program.getDepartment(),
                search
        )
                || contains(
                program.getCollege(),
                search
        );
    }

    private boolean contains(
            String value,
            String search
    ) {

        return value != null
                && value
                .toLowerCase()
                .contains(search);
    }

    private void displayPrograms(
            List<Program> programs
    ) {

        tableModel.setRowCount(0);

        for (Program program : programs) {

            tableModel.addRow(
                    new Object[]{
                            program.getCode(),
                            program.getName(),
                            program.getDepartment(),
                            program.getCollege(),
                            program.getDurationYears()
                                    + " years"
                    }
            );
        }
    }

    private void editSelectedProgram() {

        int row =
                programTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a program first.",
                    "No Program Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String code =
                tableModel
                        .getValueAt(row, 0)
                        .toString();

        try {

            Program program =
                    programService.getProgramByCode(
                            code
                    );

            showProgramDialog(program);

        } catch (Exception exception) {

            showError(
                    "Failed to load program.",
                    exception
            );
        }
    }

    private void deleteSelectedProgram() {

        int row =
                programTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a program first.",
                    "No Program Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String code =
                tableModel
                        .getValueAt(row, 0)
                        .toString();

        Program program;

        try {

            program =
                    programService.getProgramByCode(
                            code
                    );

        } catch (Exception exception) {

            showError(
                    "Failed to load program.",
                    exception
            );

            return;
        }

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete program "
                                + program.getCode()
                                + " - "
                                + program.getName()
                                + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            programService.deleteProgram(
                    program.getId()
            );

            loadPrograms();

            JOptionPane.showMessageDialog(
                    this,
                    "Program deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception exception) {

            showError(
                    "Failed to delete program.\n"
                            + "The program may still be "
                            + "referenced by students.",
                    exception
            );
        }
    }

    private void showProgramDialog(
            Program existingProgram
    ) {

        boolean editing =
                existingProgram != null;

        JTextField codeField =
                new JTextField();

        JTextField nameField =
                new JTextField();

        JTextArea descriptionArea =
                new JTextArea(4, 20);

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JSpinner durationSpinner =
                new JSpinner(
                        new SpinnerNumberModel(
                                4,
                                1,
                                10,
                                1
                        )
                );

        JTextField departmentField =
                new JTextField();

        JTextField collegeField =
                new JTextField();

        if (editing) {

            codeField.setText(
                    existingProgram.getCode()
            );

            nameField.setText(
                    existingProgram.getName()
            );

            descriptionArea.setText(
                    nullToEmpty(
                            existingProgram.getDescription()
                    )
            );

            durationSpinner.setValue(
                    existingProgram.getDurationYears()
            );

            departmentField.setText(
                    existingProgram.getDepartment()
            );

            collegeField.setText(
                    existingProgram.getCollege()
            );
        }

        JPanel form =
                new JPanel(
                        new GridBagLayout()
                );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(5, 5, 5, 5);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        form.add(
                new JLabel("Program Code *"),
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        form.add(
                codeField,
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;

        form.add(
                new JLabel("Program Name *"),
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        form.add(
                nameField,
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;

        form.add(
                new JLabel("Description"),
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        form.add(
                new JScrollPane(
                        descriptionArea
                ),
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;

        form.add(
                new JLabel("Duration (years) *"),
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        form.add(
                durationSpinner,
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;

        form.add(
                new JLabel("Department *"),
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        form.add(
                departmentField,
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;

        form.add(
                new JLabel("College *"),
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        form.add(
                collegeField,
                gbc
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        form,
                        editing
                                ? "Edit Program"
                                : "Add Program",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            String code =
                    codeField
                            .getText()
                            .trim()
                            .toUpperCase();

            String name =
                    nameField
                            .getText()
                            .trim();

            String description =
                    emptyToNull(
                            descriptionArea
                                    .getText()
                                    .trim()
                    );

            int durationYears =
                    (Integer)
                            durationSpinner
                                    .getValue();

            String department =
                    departmentField
                            .getText()
                            .trim();

            String college =
                    collegeField
                            .getText()
                            .trim();

            Program program =
                    editing
                            ? existingProgram
                            : new Program();

            if (!editing) {

                program.setId(
                        java.util.UUID
                                .randomUUID()
                                .toString()
                );
            }

            program.setCode(code);
            program.setName(name);
            program.setDescription(description);
            program.setDurationYears(durationYears);
            program.setDepartment(department);
            program.setCollege(college);

            if (editing) {

                programService.updateProgram(
                        program
                );

            } else {

                programService.createProgram(
                        program
                );
            }

            loadPrograms();

            JOptionPane.showMessageDialog(
                    this,
                    editing
                            ? "Program updated successfully."
                            : "Program added successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception exception) {

            showError(
                    "Failed to save program.",
                    exception
            );
        }
    }

    private String nullToEmpty(
            String value
    ) {

        return value == null
                ? ""
                : value;
    }

    private String emptyToNull(
            String value
    ) {

        return value == null
                || value.isBlank()
                ? null
                : value;
    }

    private void showError(
            String message,
            Exception exception
    ) {

        showError(
                message
                        + "\n\n"
                        + exception.getMessage()
        );
    }

    private void showError(
            String message
    ) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}