package org.dwnppo.enrollmentsystem.ui.students;

import org.dwnppo.enrollmentsystem.model.Program;
import org.dwnppo.enrollmentsystem.model.Student;
import org.dwnppo.enrollmentsystem.service.ProgramService;
import org.dwnppo.enrollmentsystem.service.StudentService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class StudentPanel extends JPanel {

    private final StudentService studentService;
    private final ProgramService programService;

    private final DefaultTableModel tableModel;
    private final JTable studentTable;

    private final JTextField searchField;

    public StudentPanel(
            StudentService studentService,
            ProgramService programService
    ) {

        this.studentService = studentService;
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
                        "Student ID",
                        "Name",
                        "Sex",
                        "Birth Date",
                        "Email",
                        "Program"
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

        studentTable =
                new JTable(tableModel);

        studentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        studentTable.setRowHeight(30);

        add(
                createTopPanel(),
                BorderLayout.NORTH
        );

        add(
                new JScrollPane(studentTable),
                BorderLayout.CENTER
        );

        add(
                createBottomPanel(),
                BorderLayout.SOUTH
        );

        loadStudents();
    }

    private JPanel createTopPanel() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(10, 10)
                );

        JLabel title =
                new JLabel("Student Information");

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
                event -> searchStudents()
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
                new JButton("Add Student");

        JButton editButton =
                new JButton("Edit");

        JButton deleteButton =
                new JButton("Delete");

        JButton refreshButton =
                new JButton("Refresh");

        addButton.addActionListener(
                event -> showStudentDialog(null)
        );

        editButton.addActionListener(
                event -> editSelectedStudent()
        );

        deleteButton.addActionListener(
                event -> deleteSelectedStudent()
        );

        refreshButton.addActionListener(
                event -> loadStudents()
        );

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);

        return panel;
    }

    private void loadStudents() {

        try {

            List<Student> students =
                    studentService.getAllStudents();

            displayStudents(students);

        } catch (Exception exception) {

            showError(
                    "Failed to load students.",
                    exception
            );
        }
    }

    private void searchStudents() {

        String search =
                searchField
                        .getText()
                        .trim()
                        .toLowerCase();

        if (search.isBlank()) {
            loadStudents();
            return;
        }

        try {

            List<Student> students =
                    studentService
                            .getAllStudents()
                            .stream()
                            .filter(student ->
                                    matchesSearch(
                                            student,
                                            search
                                    )
                            )
                            .toList();

            displayStudents(students);

        } catch (Exception exception) {

            showError(
                    "Failed to search students.",
                    exception
            );
        }
    }

    private boolean matchesSearch(
            Student student,
            String search
    ) {

        return contains(
                student.getId(),
                search
        )
                || contains(
                student.getFirstName(),
                search
        )
                || contains(
                student.getMiddleName(),
                search
        )
                || contains(
                student.getLastName(),
                search
        )
                || contains(
                student.getEmail(),
                search
        )
                || contains(
                student.getPhone(),
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

    private void displayStudents(
            List<Student> students
    ) {

        tableModel.setRowCount(0);

        for (Student student : students) {

            Program program = null;

            try {

                program =
                        programService.getProgram(
                                student.getProgramId()
                        );

            } catch (Exception ignored) {
            }

            tableModel.addRow(
                    new Object[]{
                            student.getId(),
                            buildFullName(student),
                            student.getSex(),
                            student.getBirthDate(),
                            student.getEmail(),
                            program != null
                                    ? program.getCode()
                                    : "Unknown"
                    }
            );
        }
    }

    private String buildFullName(
            Student student
    ) {

        StringBuilder name =
                new StringBuilder();

        name.append(
                student.getFirstName()
        );

        if (student.getMiddleName() != null
                && !student.getMiddleName().isBlank()) {

            name.append(" ")
                    .append(
                            student.getMiddleName()
                    );
        }

        name.append(" ")
                .append(
                        student.getLastName()
                );

        if (student.getSuffix() != null
                && !student.getSuffix().isBlank()) {

            name.append(" ")
                    .append(
                            student.getSuffix()
                    );
        }

        return name.toString();
    }

    private void editSelectedStudent() {

        int row =
                studentTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student first.",
                    "No Student Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String studentId =
                tableModel
                        .getValueAt(row, 0)
                        .toString();

        try {

            Student student =
                    studentService.getStudent(
                            studentId
                    );

            showStudentDialog(student);

        } catch (Exception exception) {

            showError(
                    "Failed to load student.",
                    exception
            );
        }
    }

    private void deleteSelectedStudent() {

        int row =
                studentTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student first.",
                    "No Student Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String studentId =
                tableModel
                        .getValueAt(row, 0)
                        .toString();

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete student "
                                + studentId
                                + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            studentService.deleteStudent(
                    studentId
            );

            loadStudents();

            JOptionPane.showMessageDialog(
                    this,
                    "Student deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception exception) {

            showError(
                    "Failed to delete student.",
                    exception
            );
        }
    }

    private void showStudentDialog(
            Student existingStudent
    ) {

        boolean editing =
                existingStudent != null;

        JTextField firstNameField =
                new JTextField();

        JTextField middleNameField =
                new JTextField();

        JTextField lastNameField =
                new JTextField();

        JTextField suffixField =
                new JTextField();

        JTextField birthDateField =
                new JTextField();

        JComboBox<String> sexComboBox =
                new JComboBox<>(
                        new String[]{
                                "Male",
                                "Female"
                        }
                );

        JTextField emailField =
                new JTextField();

        JTextField phoneField =
                new JTextField();

        JTextField addressField =
                new JTextField();

        /*
         * Create compact searchable program selector.
         */
        ProgramSelector programSelector =
                new ProgramSelector(
                        programService.getAllPrograms()
                );

        /*
         * Populate fields when editing.
         */
        if (editing) {

            firstNameField.setText(
                    existingStudent.getFirstName()
            );

            middleNameField.setText(
                    nullToEmpty(
                            existingStudent.getMiddleName()
                    )
            );

            lastNameField.setText(
                    existingStudent.getLastName()
            );

            suffixField.setText(
                    nullToEmpty(
                            existingStudent.getSuffix()
                    )
            );

            birthDateField.setText(
                    existingStudent
                            .getBirthDate()
                            .toString()
            );

            sexComboBox.setSelectedItem(
                    existingStudent.getSex()
            );

            emailField.setText(
                    nullToEmpty(
                            existingStudent.getEmail()
                    )
            );

            phoneField.setText(
                    nullToEmpty(
                            existingStudent.getPhone()
                    )
            );

            addressField.setText(
                    nullToEmpty(
                            existingStudent.getAddress()
                    )
            );

            programSelector.setSelectedProgram(
                    existingStudent.getProgramId()
            );
        }

        /*
         * Form.
         */
        JPanel form =
                new JPanel(
                        new GridLayout(
                                0,
                                2,
                                8,
                                8
                        )
                );

        form.add(
                new JLabel("First Name *")
        );

        form.add(
                firstNameField
        );

        form.add(
                new JLabel("Middle Name")
        );

        form.add(
                middleNameField
        );

        form.add(
                new JLabel("Last Name *")
        );

        form.add(
                lastNameField
        );

        form.add(
                new JLabel("Suffix")
        );

        form.add(
                suffixField
        );

        form.add(
                new JLabel("Birth Date *")
        );

        form.add(
                createDateHintField(
                        birthDateField
                )
        );

        form.add(
                new JLabel("Sex *")
        );

        form.add(
                sexComboBox
        );

        form.add(
                new JLabel("Email")
        );

        form.add(
                emailField
        );

        form.add(
                new JLabel("Phone")
        );

        form.add(
                phoneField
        );

        form.add(
                new JLabel("Address")
        );

        form.add(
                addressField
        );

        form.add(
                new JLabel("Program *")
        );

        form.add(
                programSelector
        );

        /*
         * Show dialog.
         */
        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        form,
                        editing
                                ? "Edit Student"
                                : "Register Student",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            LocalDate birthDate =
                    LocalDate.parse(
                            birthDateField
                                    .getText()
                                    .trim()
                    );

            Program program =
                    programSelector
                            .getSelectedProgram();

            if (program == null) {

                throw new IllegalArgumentException(
                        "Please select a program."
                );
            }

            Student student =
                    editing
                            ? existingStudent
                            : new Student();

            student.setFirstName(
                    firstNameField
                            .getText()
                            .trim()
            );

            student.setMiddleName(
                    emptyToNull(
                            middleNameField
                                    .getText()
                                    .trim()
                    )
            );

            student.setLastName(
                    lastNameField
                            .getText()
                            .trim()
            );

            student.setSuffix(
                    emptyToNull(
                            suffixField
                                    .getText()
                                    .trim()
                    )
            );

            student.setBirthDate(
                    birthDate
            );

            student.setSex(
                    sexComboBox
                            .getSelectedItem()
                            .toString()
            );

            student.setEmail(
                    emptyToNull(
                            emailField
                                    .getText()
                                    .trim()
                    )
            );

            student.setPhone(
                    emptyToNull(
                            phoneField
                                    .getText()
                                    .trim()
                    )
            );

            student.setAddress(
                    emptyToNull(
                            addressField
                                    .getText()
                                    .trim()
                    )
            );

            /*
             * Store the program ID, not the program code.
             */
            student.setProgramId(
                    program.getId()
            );

            if (editing) {

                studentService.updateStudent(
                        student
                );

            } else {

                studentService.registerStudent(
                        student
                );
            }

            loadStudents();

            JOptionPane.showMessageDialog(
                    this,
                    editing
                            ? "Student updated successfully."
                            : "Student registered successfully.\n"
                            + "Student ID: "
                            + student.getId(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception exception) {

            showError(
                    "Failed to save student.",
                    exception
            );
        }
    }

    private JComponent createDateHintField(
            JTextField field
    ) {

        JPanel panel =
                new JPanel(
                        new BorderLayout(5, 0)
                );

        panel.add(
                field,
                BorderLayout.CENTER
        );

        panel.add(
                new JLabel("YYYY-MM-DD"),
                BorderLayout.EAST
        );

        return panel;
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

        JOptionPane.showMessageDialog(
                this,
                message
                        + "\n\n"
                        + exception.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Compact program selector.
     *
     * Displays the currently selected program as a normal
     * field. Clicking the button opens a small searchable
     * popup instead of making the entire student dialog tall.
     */
    private static class ProgramSelector
            extends JPanel {

        private final List<Program> programs;

        private final JTextField displayField;
        private final JButton button;

        private Program selectedProgram;

        public ProgramSelector(
                List<Program> programs
        ) {

            this.programs = programs;

            setLayout(
                    new BorderLayout(5, 0)
            );

            displayField =
                    new JTextField();

            displayField.setEditable(false);

            displayField.setFocusable(false);

            button =
                    new JButton("▼");

            button.setFocusable(false);

            button.setPreferredSize(
                    new Dimension(
                            45,
                            25
                    )
            );

            button.addActionListener(
                    event -> showPopup()
            );

            /*
             * Clicking the field itself should also open
             * the program selector.
             */
            displayField.addMouseListener(
                    new java.awt.event.MouseAdapter() {

                        @Override
                        public void mouseClicked(
                                java.awt.event.MouseEvent event
                        ) {
                            showPopup();
                        }
                    }
            );

            add(
                    displayField,
                    BorderLayout.CENTER
            );

            add(
                    button,
                    BorderLayout.EAST
            );
        }

        public Program getSelectedProgram() {

            return selectedProgram;
        }

        public void setSelectedProgram(
                String programId
        ) {

            if (programId == null) {
                return;
            }

            for (Program program : programs) {

                if (program.getId().equals(programId)) {

                    selectedProgram =
                            program;

                    displayField.setText(
                            program.toString()
                    );

                    return;
                }
            }
        }

        private void showPopup() {

            JPopupMenu popup =
                    new JPopupMenu();

            popup.setLayout(
                    new BorderLayout(
                            5,
                            5
                    )
            );

            popup.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    Color.GRAY
                            ),
                            BorderFactory.createEmptyBorder(
                                    5,
                                    5,
                                    5,
                                    5
                            )
                    )
            );

            JTextField searchField =
                    new JTextField();

            searchField.putClientProperty(
                    "JTextField.placeholderText",
                    "Search programs..."
            );

            DefaultListModel<Program> model =
                    new DefaultListModel<>();

            JList<Program> list =
                    new JList<>(model);

            list.setSelectionMode(
                    ListSelectionModel.SINGLE_SELECTION
            );

            list.setVisibleRowCount(6);

            JScrollPane scrollPane =
                    new JScrollPane(list);

            scrollPane.setPreferredSize(
                    new Dimension(
                            350,
                            150
                    )
            );

            /*
             * Populate initial list.
             */
            for (Program program : programs) {

                model.addElement(
                        program
                );
            }

            /*
             * Search/filter programs.
             */
            searchField
                    .getDocument()
                    .addDocumentListener(
                            new DocumentListener() {

                                private void filter() {

                                    String query =
                                            searchField
                                                    .getText()
                                                    .trim()
                                                    .toLowerCase();

                                    model.clear();

                                    for (Program program :
                                            programs) {

                                        boolean matches =
                                                query.isBlank()
                                                        || contains(
                                                        program.getCode(),
                                                        query
                                                )
                                                        || contains(
                                                        program.getName(),
                                                        query
                                                );

                                        if (matches) {

                                            model.addElement(
                                                    program
                                            );
                                        }
                                    }

                                    if (!model.isEmpty()) {

                                        list.setSelectedIndex(0);
                                    }
                                }

                                @Override
                                public void insertUpdate(
                                        DocumentEvent event
                                ) {
                                    filter();
                                }

                                @Override
                                public void removeUpdate(
                                        DocumentEvent event
                                ) {
                                    filter();
                                }

                                @Override
                                public void changedUpdate(
                                        DocumentEvent event
                                ) {
                                    filter();
                                }
                            }
                    );

            /*
             * Select program by double-clicking.
             */
            list.addMouseListener(
                    new java.awt.event.MouseAdapter() {

                        @Override
                        public void mouseClicked(
                                java.awt.event.MouseEvent event
                        ) {

                            if (event.getClickCount() != 2) {
                                return;
                            }

                            selectFromList(
                                    list,
                                    popup
                            );
                        }
                    }
            );

            /*
             * Press Enter to select.
             */
            list.addKeyListener(
                    new java.awt.event.KeyAdapter() {

                        @Override
                        public void keyPressed(
                                java.awt.event.KeyEvent event
                        ) {

                            if (event.getKeyCode()
                                    == java.awt.event.KeyEvent.VK_ENTER) {

                                selectFromList(
                                        list,
                                        popup
                                );
                            }
                        }
                    }
            );

            popup.add(
                    searchField,
                    BorderLayout.NORTH
            );

            popup.add(
                    scrollPane,
                    BorderLayout.CENTER
            );

            popup.show(
                    this,
                    0,
                    getHeight()
            );

            SwingUtilities.invokeLater(
                    () -> {

                        searchField
                                .requestFocusInWindow();

                        if (!model.isEmpty()) {

                            list.setSelectedIndex(0);
                        }
                    }
            );
        }

        private void selectFromList(
                JList<Program> list,
                JPopupMenu popup
        ) {

            Program program =
                    list.getSelectedValue();

            if (program == null) {
                return;
            }

            selectedProgram =
                    program;

            displayField.setText(
                    program.toString()
            );

            popup.setVisible(false);
        }

        private static boolean contains(
                String value,
                String search
        ) {

            return value != null
                    && value
                    .toLowerCase()
                    .contains(search);
        }
    }
}