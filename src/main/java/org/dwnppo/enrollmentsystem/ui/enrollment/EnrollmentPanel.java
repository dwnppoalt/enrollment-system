package org.dwnppo.enrollmentsystem.ui.enrollment;

import org.dwnppo.enrollmentsystem.model.AcademicTerm;
import org.dwnppo.enrollmentsystem.model.Enrollment;
import org.dwnppo.enrollmentsystem.model.Student;
import org.dwnppo.enrollmentsystem.service.AcademicTermService;
import org.dwnppo.enrollmentsystem.service.EnrollmentService;
import org.dwnppo.enrollmentsystem.service.StudentService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class EnrollmentPanel extends JPanel {

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final AcademicTermService academicTermService;

    private final DefaultTableModel tableModel;
    private final JTable enrollmentTable;
    private final JTextField searchField;

    public EnrollmentPanel(
            EnrollmentService enrollmentService,
            StudentService studentService,
            AcademicTermService academicTermService
    ) {

        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.academicTermService = academicTermService;

        setLayout(
                new BorderLayout(10, 10)
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        15,
                        15,
                        15
                )
        );

        searchField =
                new JTextField();

        tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "Enrollment ID",
                                "Student ID",
                                "Student Name",
                                "School Year",
                                "Semester",
                                "Enrollment Date",
                                "Status"
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

        enrollmentTable =
                new JTable(tableModel);

        enrollmentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        enrollmentTable.setRowHeight(30);

        add(
                createTopPanel(),
                BorderLayout.NORTH
        );

        add(
                new JScrollPane(enrollmentTable),
                BorderLayout.CENTER
        );

        add(
                createBottomPanel(),
                BorderLayout.SOUTH
        );

        loadEnrollments();
    }

    private JPanel createTopPanel() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(10, 10)
                );

        JLabel title =
                new JLabel("Enrollment");

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
                event -> searchEnrollments()
        );

        searchField.addActionListener(
                event -> searchEnrollments()
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

        JButton enrollButton =
                new JButton("Enroll Student");

        JButton editButton =
                new JButton("Edit Status");

        JButton cancelButton =
                new JButton("Cancel Enrollment");

        JButton refreshButton =
                new JButton("Refresh");

        enrollButton.addActionListener(
                event -> showEnrollmentDialog()
        );

        editButton.addActionListener(
                event -> editSelectedEnrollment()
        );

        cancelButton.addActionListener(
                event -> cancelSelectedEnrollment()
        );

        refreshButton.addActionListener(
                event -> loadEnrollments()
        );

        panel.add(enrollButton);
        panel.add(editButton);
        panel.add(cancelButton);
        panel.add(refreshButton);

        return panel;
    }

    private void loadEnrollments() {

        try {

            List<Enrollment> enrollments =
                    enrollmentService
                            .getAllEnrollments();

            displayEnrollments(
                    enrollments
            );

        } catch (Exception exception) {

            showError(
                    "Failed to load enrollments.",
                    exception
            );
        }
    }

    private void searchEnrollments() {

        String query =
                searchField
                        .getText()
                        .trim()
                        .toLowerCase();

        if (query.isBlank()) {

            loadEnrollments();

            return;
        }

        try {

            List<Enrollment> enrollments =
                    enrollmentService
                            .getAllEnrollments()
                            .stream()
                            .filter(enrollment ->
                                    matchesSearch(
                                            enrollment,
                                            query
                                    )
                            )
                            .toList();

            displayEnrollments(
                    enrollments
            );

        } catch (Exception exception) {

            showError(
                    "Failed to search enrollments.",
                    exception
            );
        }
    }

    private boolean matchesSearch(
            Enrollment enrollment,
            String query
    ) {

        Student student =
                findStudent(
                        enrollment.getStudentId()
                );

        AcademicTerm term =
                findTerm(
                        enrollment.getTermId()
                );

        return contains(
                enrollment.getId(),
                query
        )
                || contains(
                enrollment.getStudentId(),
                query
        )
                || contains(
                enrollment.getStatus(),
                query
        )
                || (
                student != null
                        && (
                        contains(
                                student.getFirstName(),
                                query
                        )
                                || contains(
                                student.getMiddleName(),
                                query
                        )
                                || contains(
                                student.getLastName(),
                                query
                        )
                )
        )
                || (
                term != null
                        && (
                        contains(
                                term.getSchoolYear(),
                                query
                        )
                                || contains(
                                term.getSemester(),
                                query
                        )
                )
        );
    }

    private void displayEnrollments(
            List<Enrollment> enrollments
    ) {

        tableModel.setRowCount(0);

        for (Enrollment enrollment :
                enrollments) {

            Student student =
                    findStudent(
                            enrollment.getStudentId()
                    );

            AcademicTerm term =
                    findTerm(
                            enrollment.getTermId()
                    );

            String studentName =
                    student != null
                            ? buildFullName(student)
                            : "Unknown";

            String schoolYear =
                    term != null
                            ? term.getSchoolYear()
                            : "Unknown";

            String semester =
                    term != null
                            ? term.getSemester()
                            : "Unknown";

            tableModel.addRow(
                    new Object[]{
                            enrollment.getId(),
                            enrollment.getStudentId(),
                            studentName,
                            schoolYear,
                            semester,
                            enrollment.getEnrollmentDate(),
                            enrollment.getStatus()
                    }
            );
        }
    }

    private Student findStudent(
            String studentId
    ) {

        if (studentId == null
                || studentId.isBlank()) {

            return null;
        }

        try {

            return studentService
                    .getStudent(studentId);

        } catch (Exception exception) {

            return null;
        }
    }

    private AcademicTerm findTerm(
            String termId
    ) {

        if (termId == null
                || termId.isBlank()) {

            return null;
        }

        try {

            return academicTermService
                    .getTerm(termId);

        } catch (Exception exception) {

            return null;
        }
    }

    private void showEnrollmentDialog() {

        List<Student> students;

        List<AcademicTerm> terms;

        try {

            students =
                    studentService
                            .getAllStudents();

            terms =
                    academicTermService
                            .getAllTerms();

        } catch (Exception exception) {

            showError(
                    "Failed to load students or academic terms.",
                    exception
            );

            return;
        }

        if (students.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "There are no students available for enrollment.",
                    "No Students",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (terms.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "There are no academic terms available.",
                    "No Academic Terms",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        StudentSelector studentSelector =
                new StudentSelector(
                        students
                );

        TermSelector termSelector =
                new TermSelector(
                        terms
                );

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
                new JLabel("Student *")
        );

        form.add(
                studentSelector
        );

        form.add(
                new JLabel("Academic Term *")
        );

        form.add(
                termSelector
        );

        form.add(
                new JLabel("Status")
        );

        JLabel statusLabel =
                new JLabel("ENROLLED");

        statusLabel.setFont(
                statusLabel.getFont()
                        .deriveFont(
                                Font.BOLD
                        )
        );

        form.add(
                statusLabel
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        form,
                        "Enroll Student",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        Student student =
                studentSelector
                        .getSelectedStudent();

        AcademicTerm term =
                termSelector
                        .getSelectedTerm();

        if (student == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (term == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an academic term.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            Enrollment enrollment =
                    enrollmentService.enrollStudent(
                            student.getId(),
                            term.getId()
                    );

            loadEnrollments();

            JOptionPane.showMessageDialog(
                    this,
                    "Student enrolled successfully.\n\n"
                            + "Enrollment ID: "
                            + enrollment.getId(),
                    "Enrollment Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception exception) {

            showError(
                    "Failed to enroll student.",
                    exception
            );
        }
    }

    private void editSelectedEnrollment() {

        int row =
                enrollmentTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an enrollment first.",
                    "No Enrollment Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String enrollmentId =
                tableModel
                        .getValueAt(row, 0)
                        .toString();

        Enrollment enrollment;

        try {

            enrollment =
                    enrollmentService
                            .getEnrollment(
                                    enrollmentId
                            );

        } catch (Exception exception) {

            showError(
                    "Failed to load enrollment.",
                    exception
            );

            return;
        }

        if (enrollment == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "The selected enrollment could not be found.",
                    "Enrollment Not Found",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        String currentStatus =
                enrollment.getStatus();

        String[] statuses = {
                "ENROLLED",
                "PENDING",
                "CANCELLED"
        };

        JComboBox<String> statusComboBox =
                new JComboBox<>(statuses);

        statusComboBox.setSelectedItem(
                currentStatus
        );

        JPanel panel =
                new JPanel(
                        new BorderLayout(8, 8)
                );

        panel.add(
                new JLabel("Status:"),
                BorderLayout.WEST
        );

        panel.add(
                statusComboBox,
                BorderLayout.CENTER
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Edit Enrollment Status",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String newStatus =
                statusComboBox
                        .getSelectedItem()
                        .toString();

        if (newStatus.equals(currentStatus)) {
            return;
        }

        try {

            enrollmentService.updateStatus(
                    enrollmentId,
                    newStatus
            );

            loadEnrollments();

            JOptionPane.showMessageDialog(
                    this,
                    "Enrollment status updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception exception) {

            showError(
                    "Failed to update enrollment status.",
                    exception
            );
        }
    }

    private void cancelSelectedEnrollment() {

        int row =
                enrollmentTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an enrollment first.",
                    "No Enrollment Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String enrollmentId =
                tableModel
                        .getValueAt(row, 0)
                        .toString();

        String studentId =
                tableModel
                        .getValueAt(row, 1)
                        .toString();

        String studentName =
                tableModel
                        .getValueAt(row, 2)
                        .toString();

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Cancel enrollment for "
                                + studentName
                                + " ("
                                + studentId
                                + ")?",
                        "Confirm Cancellation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            enrollmentService.cancelEnrollment(
                    enrollmentId
            );

            loadEnrollments();

            JOptionPane.showMessageDialog(
                    this,
                    "Enrollment cancelled successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception exception) {

            showError(
                    "Failed to cancel enrollment.",
                    exception
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

    private boolean contains(
            String value,
            String query
    ) {

        return value != null
                && value
                .toLowerCase()
                .contains(query);
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
     * Compact searchable student selector.
     */
    private static class StudentSelector
            extends JPanel {

        private final List<Student> students;

        private final JTextField displayField;
        private final JButton button;

        private Student selectedStudent;

        public StudentSelector(
                List<Student> students
        ) {

            this.students = students;

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

            displayField.addMouseListener(
                    new MouseAdapter() {

                        @Override
                        public void mouseClicked(
                                MouseEvent event
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

        public Student getSelectedStudent() {

            return selectedStudent;
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

            DefaultListModel<Student> model =
                    new DefaultListModel<>();

            JList<Student> list =
                    new JList<>(model);

            list.setCellRenderer(
                    new DefaultListCellRenderer() {

                        @Override
                        public Component getListCellRendererComponent(
                                JList<?> list,
                                Object value,
                                int index,
                                boolean isSelected,
                                boolean cellHasFocus
                        ) {

                            super.getListCellRendererComponent(
                                    list,
                                    value,
                                    index,
                                    isSelected,
                                    cellHasFocus
                            );

                            if (value instanceof Student student) {

                                setText(
                                        student.getId()
                                                + " - "
                                                + buildFullName(student)
                                );
                            }

                            return this;
                        }
                    }
            );

            list.setSelectionMode(
                    ListSelectionModel.SINGLE_SELECTION
            );

            list.setVisibleRowCount(6);

            JScrollPane scrollPane =
                    new JScrollPane(list);

            scrollPane.setPreferredSize(
                    new Dimension(
                            380,
                            150
                    )
            );

            for (Student student : students) {
                model.addElement(student);
            }

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

                                    for (Student student :
                                            students) {

                                        String fullName =
                                                buildFullName(
                                                        student
                                                );

                                        if (
                                                query.isBlank()
                                                        || contains(
                                                        student.getId(),
                                                        query
                                                )
                                                        || contains(
                                                        fullName,
                                                        query
                                                )
                                        ) {

                                            model.addElement(
                                                    student
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

            list.addMouseListener(
                    new MouseAdapter() {

                        @Override
                        public void mouseClicked(
                                MouseEvent event
                        ) {

                            if (event.getClickCount() == 2) {

                                selectStudent(
                                        list,
                                        popup
                                );
                            }
                        }
                    }
            );

            list.addKeyListener(
                    new KeyAdapter() {

                        @Override
                        public void keyPressed(
                                KeyEvent event
                        ) {

                            if (event.getKeyCode()
                                    == KeyEvent.VK_ENTER) {

                                selectStudent(
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
                    () -> searchField
                            .requestFocusInWindow()
            );
        }

        private void selectStudent(
                JList<Student> list,
                JPopupMenu popup
        ) {

            Student student =
                    list.getSelectedValue();

            if (student == null) {
                return;
            }

            selectedStudent =
                    student;

            displayField.setText(
                    student.getId()
                            + " - "
                            + buildFullName(student)
            );

            popup.setVisible(false);
        }

        private static String buildFullName(
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

        private static boolean contains(
                String value,
                String query
        ) {

            return value != null
                    && value
                    .toLowerCase()
                    .contains(query);
        }
    }

    /**
     * Compact searchable academic-term selector.
     */
    private static class TermSelector
            extends JPanel {

        private final List<AcademicTerm> terms;

        private final JTextField displayField;
        private final JButton button;

        private AcademicTerm selectedTerm;

        public TermSelector(
                List<AcademicTerm> terms
        ) {

            this.terms = terms;

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

            displayField.addMouseListener(
                    new MouseAdapter() {

                        @Override
                        public void mouseClicked(
                                MouseEvent event
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

        public AcademicTerm getSelectedTerm() {

            return selectedTerm;
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

            DefaultListModel<AcademicTerm> model =
                    new DefaultListModel<>();

            JList<AcademicTerm> list =
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

            for (AcademicTerm term : terms) {
                model.addElement(term);
            }

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

                                    for (AcademicTerm term :
                                            terms) {

                                        String display =
                                                term.getSchoolYear()
                                                        + " - "
                                                        + term.getSemester();

                                        if (
                                                query.isBlank()
                                                        || contains(
                                                        display,
                                                        query
                                                )
                                        ) {

                                            model.addElement(
                                                    term
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

            list.addMouseListener(
                    new MouseAdapter() {

                        @Override
                        public void mouseClicked(
                                MouseEvent event
                        ) {

                            if (event.getClickCount() == 2) {

                                selectTerm(
                                        list,
                                        popup
                                );
                            }
                        }
                    }
            );

            list.addKeyListener(
                    new KeyAdapter() {

                        @Override
                        public void keyPressed(
                                KeyEvent event
                        ) {

                            if (event.getKeyCode()
                                    == KeyEvent.VK_ENTER) {

                                selectTerm(
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
                    () -> searchField
                            .requestFocusInWindow()
            );
        }

        private void selectTerm(
                JList<AcademicTerm> list,
                JPopupMenu popup
        ) {

            AcademicTerm term =
                    list.getSelectedValue();

            if (term == null) {
                return;
            }

            selectedTerm =
                    term;

            displayField.setText(
                    term.getSchoolYear()
                            + " - "
                            + term.getSemester()
            );

            popup.setVisible(false);
        }

        private static boolean contains(
                String value,
                String query
        ) {

            return value != null
                    && value
                    .toLowerCase()
                    .contains(query);
        }
    }
}