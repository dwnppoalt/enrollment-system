package org.dwnppo.enrollmentsystem.ui.assessment;

import org.dwnppo.enrollmentsystem.model.AcademicTerm;
import org.dwnppo.enrollmentsystem.model.Assessment;
import org.dwnppo.enrollmentsystem.model.Enrollment;
import org.dwnppo.enrollmentsystem.model.Student;
import org.dwnppo.enrollmentsystem.service.AcademicTermService;
import org.dwnppo.enrollmentsystem.service.AssessmentService;
import org.dwnppo.enrollmentsystem.service.EnrollmentService;
import org.dwnppo.enrollmentsystem.service.StudentService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AssessmentPanel extends JPanel {

    private final AssessmentService assessmentService;
    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final AcademicTermService academicTermService;

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AssessmentPanel(
            AssessmentService assessmentService,
            EnrollmentService enrollmentService,
            StudentService studentService,
            AcademicTermService academicTermService
    ) {
        this.assessmentService = assessmentService;
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.academicTermService = academicTermService;

        setLayout(new BorderLayout(10, 10));
        setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15
                )
        );

        JPanel topPanel =
                new JPanel(new BorderLayout(10, 10));

        searchField = new JTextField();

        searchField.putClientProperty(
                "JTextField.placeholderText",
                "Search assessments..."
        );

        JButton searchButton =
                new JButton("Search");

        JButton refreshButton =
                new JButton("Refresh");

        JButton addButton =
                new JButton("Add Assessment");

        JButton editButton =
                new JButton("Edit");

        JPanel searchPanel =
                new JPanel(new BorderLayout(5, 5));

        searchPanel.add(
                searchField,
                BorderLayout.CENTER
        );

        searchPanel.add(
                searchButton,
                BorderLayout.EAST
        );

        JPanel actionPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                5,
                                0
                        )
                );

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(refreshButton);

        topPanel.add(
                searchPanel,
                BorderLayout.CENTER
        );

        topPanel.add(
                actionPanel,
                BorderLayout.EAST
        );

        add(
                topPanel,
                BorderLayout.NORTH
        );

        tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "Assessment ID",
                                "Enrollment ID",
                                "Student",
                                "Term",
                                "Tuition",
                                "Miscellaneous",
                                "Laboratory",
                                "Other",
                                "Total",
                                "Paid",
                                "Balance",
                                "Status",
                                "Created"
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

        table = new JTable(tableModel);

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.setAutoCreateRowSorter(true);
        table.setRowHeight(28);

        add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        searchButton.addActionListener(
                e -> loadAssessments(
                        searchField.getText()
                )
        );

        searchField.addActionListener(
                e -> loadAssessments(
                        searchField.getText()
                )
        );

        refreshButton.addActionListener(
                e -> {
                    searchField.setText("");
                    loadAssessments("");
                }
        );

        addButton.addActionListener(
                e -> showAssessmentDialog(null)
        );

        editButton.addActionListener(
                e -> {
                    Assessment assessment =
                            getSelectedAssessment();

                    if (assessment == null) {
                        showMessage(
                                "Please select an assessment first.",
                                "No Selection",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }

                    showAssessmentDialog(assessment);
                }
        );

        loadAssessments("");
    }

    private void loadAssessments(String search) {

        tableModel.setRowCount(0);

        List<Assessment> assessments =
                assessmentService.getAllAssessments();

        String query =
                search == null
                        ? ""
                        : search.trim().toLowerCase();

        for (Assessment assessment : assessments) {

            Enrollment enrollment =
                    findEnrollment(
                            assessment.getEnrollmentId()
                    );

            if (enrollment == null) {
                continue;
            }

            Student student =
                    findStudent(
                            enrollment.getStudentId()
                    );

            AcademicTerm term =
                    findTerm(
                            enrollment.getTermId()
                    );

            String studentName =
                    student == null
                            ? enrollment.getStudentId()
                            : buildFullName(student);

            String termName =
                    term == null
                            ? enrollment.getTermId()
                            : term.toString();

            if (!query.isEmpty()) {

                String searchable =
                        assessment.getId()
                                + " "
                                + assessment.getEnrollmentId()
                                + " "
                                + enrollment.getStudentId()
                                + " "
                                + studentName
                                + " "
                                + termName
                                + " "
                                + assessment.getStatus();

                if (!searchable
                        .toLowerCase()
                        .contains(query)) {

                    continue;
                }
            }

            tableModel.addRow(
                    new Object[]{
                            assessment.getId(),
                            assessment.getEnrollmentId(),
                            studentName,
                            termName,
                            money(
                                    assessment.getTuitionFee()
                            ),
                            money(
                                    assessment.getMiscellaneousFee()
                            ),
                            money(
                                    assessment.getLaboratoryFee()
                            ),
                            money(
                                    assessment.getOtherFee()
                            ),
                            money(
                                    assessment.getTotalAmount()
                            ),
                            money(
                                    assessment.getAmountPaid()
                            ),
                            money(
                                    assessment.getBalance()
                            ),
                            assessment.getStatus(),
                            assessment.getCreatedAt() == null
                                    ? ""
                                    : assessment
                                    .getCreatedAt()
                                    .format(dateFormatter)
                    }
            );
        }
    }

    private void showAssessmentDialog(
            Assessment existing
    ) {

        boolean editing =
                existing != null;

        JTextField enrollmentField =
                new JTextField();

        enrollmentField.setEditable(false);

        JButton enrollmentButton =
                new JButton("▼");

        JPanel enrollmentPanel =
                new JPanel(
                        new BorderLayout(5, 5)
                );

        enrollmentPanel.add(
                enrollmentField,
                BorderLayout.CENTER
        );

        enrollmentPanel.add(
                enrollmentButton,
                BorderLayout.EAST
        );

        final Enrollment[] selectedEnrollment =
                new Enrollment[1];

        if (editing) {

            Enrollment enrollment =
                    findEnrollment(
                            existing.getEnrollmentId()
                    );

            selectedEnrollment[0] =
                    enrollment;

            if (enrollment != null) {

                enrollmentField.setText(
                        buildEnrollmentDisplay(
                                enrollment
                        )
                );
            }

            enrollmentButton.setEnabled(false);
        }

        enrollmentButton.addActionListener(
                e -> {

                    EnrollmentSelector selector =
                            new EnrollmentSelector(
                                    enrollmentService,
                                    studentService,
                                    academicTermService
                            );

                    Enrollment selected =
                            selector.showSelector(this);

                    if (selected != null) {

                        selectedEnrollment[0] =
                                selected;

                        enrollmentField.setText(
                                buildEnrollmentDisplay(
                                        selected
                                )
                        );
                    }
                }
        );

        JTextField tuitionField =
                new JTextField(
                        editing
                                ? existing
                                .getTuitionFee()
                                .toPlainString()
                                : "0.00"
                );

        JTextField miscellaneousField =
                new JTextField(
                        editing
                                ? existing
                                .getMiscellaneousFee()
                                .toPlainString()
                                : "0.00"
                );

        JTextField laboratoryField =
                new JTextField(
                        editing
                                ? existing
                                .getLaboratoryFee()
                                .toPlainString()
                                : "0.00"
                );

        JTextField otherField =
                new JTextField(
                        editing
                                ? existing
                                .getOtherFee()
                                .toPlainString()
                                : "0.00"
                );

        JLabel totalLabel =
                new JLabel(
                        editing
                                ? money(
                                existing
                                        .getTotalAmount()
                        )
                                : "₱0.00"
                );

        JLabel paidLabel =
                new JLabel(
                        editing
                                ? money(
                                existing
                                        .getAmountPaid()
                        )
                                : "₱0.00"
                );

        JLabel balanceLabel =
                new JLabel(
                        editing
                                ? money(
                                existing
                                        .getBalance()
                        )
                                : "₱0.00"
                );

        JLabel statusLabel =
                new JLabel(
                        editing
                                ? existing.getStatus()
                                : "UNPAID"
                );

        Runnable calculatePreview =
                () -> {

                    try {

                        BigDecimal tuition =
                                parseMoney(
                                        tuitionField
                                                .getText()
                                );

                        BigDecimal miscellaneous =
                                parseMoney(
                                        miscellaneousField
                                                .getText()
                                );

                        BigDecimal laboratory =
                                parseMoney(
                                        laboratoryField
                                                .getText()
                                );

                        BigDecimal other =
                                parseMoney(
                                        otherField
                                                .getText()
                                );

                        BigDecimal total =
                                tuition
                                        .add(miscellaneous)
                                        .add(laboratory)
                                        .add(other);

                        BigDecimal paid =
                                editing
                                        ? existing
                                        .getAmountPaid()
                                        : BigDecimal.ZERO;

                        BigDecimal balance =
                                total.subtract(paid);

                        String status;

                        if (balance.compareTo(
                                BigDecimal.ZERO
                        ) <= 0) {

                            status = "PAID";

                        } else if (paid.compareTo(
                                BigDecimal.ZERO
                        ) > 0) {

                            status = "PARTIAL";

                        } else {

                            status = "UNPAID";
                        }

                        totalLabel.setText(
                                money(total)
                        );

                        paidLabel.setText(
                                money(paid)
                        );

                        balanceLabel.setText(
                                money(balance)
                        );

                        statusLabel.setText(
                                status
                        );

                    } catch (Exception ignored) {

                        totalLabel.setText(
                                "Invalid"
                        );

                        balanceLabel.setText(
                                "Invalid"
                        );

                        statusLabel.setText(
                                "-"
                        );
                    }
                };

        addDocumentListener(
                tuitionField,
                calculatePreview
        );

        addDocumentListener(
                miscellaneousField,
                calculatePreview
        );

        addDocumentListener(
                laboratoryField,
                calculatePreview
        );

        addDocumentListener(
                otherField,
                calculatePreview
        );

        JPanel form =
                new JPanel(
                        new GridBagLayout()
                );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        5, 5, 5, 5
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        addRow(
                form,
                gbc,
                0,
                "Enrollment:",
                enrollmentPanel
        );

        addRow(
                form,
                gbc,
                1,
                "Tuition Fee:",
                tuitionField
        );

        addRow(
                form,
                gbc,
                2,
                "Miscellaneous Fee:",
                miscellaneousField
        );

        addRow(
                form,
                gbc,
                3,
                "Laboratory Fee:",
                laboratoryField
        );

        addRow(
                form,
                gbc,
                4,
                "Other Fee:",
                otherField
        );

        addRow(
                form,
                gbc,
                5,
                "Total:",
                totalLabel
        );

        addRow(
                form,
                gbc,
                6,
                "Amount Paid:",
                paidLabel
        );

        addRow(
                form,
                gbc,
                7,
                "Balance:",
                balanceLabel
        );

        addRow(
                form,
                gbc,
                8,
                "Status:",
                statusLabel
        );

        if (editing
                && existing
                .getAmountPaid()
                .compareTo(BigDecimal.ZERO) > 0) {

            JLabel warning =
                    new JLabel(
                            "Fees cannot be changed after payment."
                    );

            warning.setForeground(
                    Color.RED
            );

            gbc.gridx = 0;
            gbc.gridy = 9;
            gbc.gridwidth = 2;

            form.add(
                    warning,
                    gbc
            );

            tuitionField.setEnabled(false);
            miscellaneousField.setEnabled(false);
            laboratoryField.setEnabled(false);
            otherField.setEnabled(false);
        }

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        form,
                        editing
                                ? "Edit Assessment"
                                : "Create Assessment",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            if (selectedEnrollment[0] == null) {

                throw new IllegalArgumentException(
                        "Please select an enrollment."
                );
            }

            BigDecimal tuition =
                    parseMoney(
                            tuitionField.getText()
                    );

            BigDecimal miscellaneous =
                    parseMoney(
                            miscellaneousField.getText()
                    );

            BigDecimal laboratory =
                    parseMoney(
                            laboratoryField.getText()
                    );

            BigDecimal other =
                    parseMoney(
                            otherField.getText()
                    );

            if (editing) {

                assessmentService.updateFees(
                        existing.getId(),
                        tuition,
                        miscellaneous,
                        laboratory,
                        other
                );

                showMessage(
                        "Assessment updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                assessmentService.createAssessment(
                        selectedEnrollment[0].getId(),
                        tuition,
                        miscellaneous,
                        laboratory,
                        other
                );

                showMessage(
                        "Assessment created successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            loadAssessments(
                    searchField.getText()
            );

        } catch (Exception ex) {

            showMessage(
                    ex.getMessage() == null
                            ? "An error occurred."
                            : ex.getMessage(),
                    "Assessment Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Assessment getSelectedAssessment() {

        int row =
                table.getSelectedRow();

        if (row < 0) {
            return null;
        }

        row =
                table.convertRowIndexToModel(row);

        String id =
                tableModel
                        .getValueAt(row, 0)
                        .toString();

        return assessmentService
                .getAssessment(id);
    }

    private Enrollment findEnrollment(
            String id
    ) {

        try {
            return enrollmentService
                    .getEnrollment(id);

        } catch (Exception e) {

            return null;
        }
    }

    private Student findStudent(
            String id
    ) {

        try {
            return studentService
                    .getStudent(id);

        } catch (Exception e) {

            return null;
        }
    }

    private AcademicTerm findTerm(
            String id
    ) {

        try {
            return academicTermService
                    .getTerm(id);

        } catch (Exception e) {

            return null;
        }
    }

    private String buildEnrollmentDisplay(
            Enrollment enrollment
    ) {

        Student student =
                findStudent(
                        enrollment.getStudentId()
                );

        AcademicTerm term =
                findTerm(
                        enrollment.getTermId()
                );

        String studentName =
                student == null
                        ? enrollment.getStudentId()
                        : buildFullName(student);

        String termName =
                term == null
                        ? enrollment.getTermId()
                        : term.toString();

        return enrollment.getId()
                + " - "
                + studentName
                + " - "
                + termName;
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
                && !student
                .getMiddleName()
                .isBlank()) {

            name.append(" ")
                    .append(
                            student
                                    .getMiddleName()
                    );
        }

        name.append(" ")
                .append(
                        student.getLastName()
                );

        if (student.getSuffix() != null
                && !student
                .getSuffix()
                .isBlank()) {

            name.append(" ")
                    .append(
                            student.getSuffix()
                    );
        }

        return name.toString();
    }

    private BigDecimal parseMoney(
            String value
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            return BigDecimal.ZERO;
        }

        return new BigDecimal(
                value.trim()
                        .replace(",", "")
        );
    }

    private String money(
            BigDecimal value
    ) {

        if (value == null) {
            value = BigDecimal.ZERO;
        }

        return "₱"
                + String.format(
                "%,.2f",
                value
        );
    }

    private void addRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            Component component
    ) {

        gbc.gridy = row;
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.weightx = 0;

        panel.add(
                new JLabel(label),
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        panel.add(
                component,
                gbc
        );
    }

    private void addDocumentListener(
            JTextField field,
            Runnable runnable
    ) {

        field.getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e
                            ) {
                                runnable.run();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent e
                            ) {
                                runnable.run();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent e
                            ) {
                                runnable.run();
                            }
                        }
                );
    }

    private void showMessage(
            String message,
            String title,
            int type
    ) {

        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                type
        );
    }

    private static class EnrollmentSelector {

        private final EnrollmentService enrollmentService;
        private final StudentService studentService;
        private final AcademicTermService academicTermService;

        EnrollmentSelector(
                EnrollmentService enrollmentService,
                StudentService studentService,
                AcademicTermService academicTermService
        ) {

            this.enrollmentService =
                    enrollmentService;

            this.studentService =
                    studentService;

            this.academicTermService =
                    academicTermService;
        }

        Enrollment showSelector(
                Component parent
        ) {

            List<Enrollment> enrollments =
                    enrollmentService
                            .getAllEnrollments();

            DefaultListModel<Enrollment> model =
                    new DefaultListModel<>();

            for (Enrollment enrollment :
                    enrollments) {

                model.addElement(
                        enrollment
                );
            }

            JList<Enrollment> list =
                    new JList<>(model);

            list.setSelectionMode(
                    ListSelectionModel.SINGLE_SELECTION
            );

            list.setVisibleRowCount(8);

            list.setCellRenderer(
                    new DefaultListCellRenderer() {

                        @Override
                        public Component
                        getListCellRendererComponent(
                                JList<?> list,
                                Object value,
                                int index,
                                boolean isSelected,
                                boolean cellHasFocus
                        ) {

                            super
                                    .getListCellRendererComponent(
                                            list,
                                            value,
                                            index,
                                            isSelected,
                                            cellHasFocus
                                    );

                            if (value
                                    instanceof Enrollment enrollment) {

                                setText(
                                        buildEnrollmentDisplay(
                                                enrollment
                                        )
                                );
                            }

                            return this;
                        }
                    }
            );

            JTextField searchField =
                    new JTextField();

            searchField.putClientProperty(
                    "JTextField.placeholderText",
                    "Search enrollment..."
            );

            JPanel panel =
                    new JPanel(
                            new BorderLayout(
                                    5, 5
                            )
                    );

            panel.add(
                    searchField,
                    BorderLayout.NORTH
            );

            panel.add(
                    new JScrollPane(list),
                    BorderLayout.CENTER
            );

            List<Enrollment> original =
                    List.copyOf(enrollments);

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

                                    for (Enrollment enrollment :
                                            original) {

                                        String text =
                                                buildEnrollmentDisplay(
                                                        enrollment
                                                )
                                                        .toLowerCase();

                                        if (text.contains(
                                                query
                                        )) {

                                            model.addElement(
                                                    enrollment
                                            );
                                        }
                                    }
                                }

                                @Override
                                public void insertUpdate(
                                        DocumentEvent e
                                ) {
                                    filter();
                                }

                                @Override
                                public void removeUpdate(
                                        DocumentEvent e
                                ) {
                                    filter();
                                }

                                @Override
                                public void changedUpdate(
                                        DocumentEvent e
                                ) {
                                    filter();
                                }
                            }
                    );

            int result =
                    JOptionPane.showConfirmDialog(
                            parent,
                            panel,
                            "Select Enrollment",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                    );

            if (result != JOptionPane.OK_OPTION) {
                return null;
            }

            return list.getSelectedValue();
        }

        private String buildEnrollmentDisplay(
                Enrollment enrollment
        ) {

            Student student =
                    findStudent(
                            enrollment.getStudentId()
                    );

            AcademicTerm term =
                    findTerm(
                            enrollment.getTermId()
                    );

            String studentName =
                    student == null
                            ? enrollment.getStudentId()
                            : buildFullName(student);

            String termName =
                    term == null
                            ? enrollment.getTermId()
                            : term.toString();

            return enrollment.getId()
                    + " - "
                    + studentName
                    + " - "
                    + termName;
        }

        private Student findStudent(
                String id
        ) {

            try {

                return studentService
                        .getStudent(id);

            } catch (Exception e) {

                return null;
            }
        }

        private AcademicTerm findTerm(
                String id
        ) {

            try {

                return academicTermService
                        .getTerm(id);

            } catch (Exception e) {

                return null;
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
                    && !student
                    .getMiddleName()
                    .isBlank()) {

                name.append(" ")
                        .append(
                                student
                                        .getMiddleName()
                        );
            }

            name.append(" ")
                    .append(
                            student.getLastName()
                    );

            if (student.getSuffix() != null
                    && !student
                    .getSuffix()
                    .isBlank()) {

                name.append(" ")
                        .append(
                                student.getSuffix()
                        );
            }

            return name.toString();
        }
    }
}