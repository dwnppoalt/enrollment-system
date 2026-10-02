package org.dwnppo.enrollmentsystem.ui.payments;

import org.dwnppo.enrollmentsystem.model.AcademicTerm;
import org.dwnppo.enrollmentsystem.model.Assessment;
import org.dwnppo.enrollmentsystem.model.Enrollment;
import org.dwnppo.enrollmentsystem.model.Payment;
import org.dwnppo.enrollmentsystem.model.Student;
import org.dwnppo.enrollmentsystem.model.User;
import org.dwnppo.enrollmentsystem.service.AcademicTermService;
import org.dwnppo.enrollmentsystem.service.AssessmentService;
import org.dwnppo.enrollmentsystem.service.EnrollmentService;
import org.dwnppo.enrollmentsystem.service.PaymentService;
import org.dwnppo.enrollmentsystem.service.StudentService;
import org.dwnppo.enrollmentsystem.util.Session;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaymentPanel extends JPanel {

    private final PaymentService paymentService;
    private final AssessmentService assessmentService;
    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final AcademicTermService academicTermService;

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PaymentPanel(
            PaymentService paymentService,
            AssessmentService assessmentService,
            EnrollmentService enrollmentService,
            StudentService studentService,
            AcademicTermService academicTermService
    ) {
        this.paymentService = paymentService;
        this.assessmentService = assessmentService;
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.academicTermService = academicTermService;

        setLayout(new BorderLayout(10, 10));

        setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        15,
                        15,
                        15
                )
        );

        JPanel topPanel =
                new JPanel(new BorderLayout(10, 10));

        searchField = new JTextField();

        searchField.putClientProperty(
                "JTextField.placeholderText",
                "Search payments..."
        );

        JButton searchButton =
                new JButton("Search");

        JButton refreshButton =
                new JButton("Refresh");

        JButton recordButton =
                new JButton("Record Payment");

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

        actionPanel.add(recordButton);
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
                                "Payment ID",
                                "Assessment ID",
                                "Student",
                                "Term",
                                "Amount",
                                "Method",
                                "Reference",
                                "Cashier",
                                "Status",
                                "Paid At"
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

        table =
                new JTable(tableModel);

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
                e -> loadPayments(
                        searchField.getText()
                )
        );

        searchField.addActionListener(
                e -> loadPayments(
                        searchField.getText()
                )
        );

        refreshButton.addActionListener(
                e -> {
                    searchField.setText("");
                    loadPayments("");
                }
        );

        recordButton.addActionListener(
                e -> showPaymentDialog()
        );

        loadPayments("");
    }

    private void loadPayments(String search) {

        tableModel.setRowCount(0);

        List<Payment> payments =
                paymentService.getAllPayments();

        String query =
                search == null
                        ? ""
                        : search.trim().toLowerCase();

        for (Payment payment : payments) {

            Assessment assessment =
                    findAssessment(
                            payment.getAssessmentId()
                    );

            String studentName = "";
            String termName = "";

            if (assessment != null) {

                Enrollment enrollment =
                        findEnrollment(
                                assessment.getEnrollmentId()
                        );

                if (enrollment != null) {

                    Student student =
                            findStudent(
                                    enrollment.getStudentId()
                            );

                    AcademicTerm term =
                            findTerm(
                                    enrollment.getTermId()
                            );

                    if (student != null) {
                        studentName =
                                buildFullName(student);
                    }

                    if (term != null) {
                        termName =
                                term.toString();
                    }
                }
            }

            if (studentName.isBlank()) {
                studentName = "-";
            }

            if (termName.isBlank()) {
                termName = "-";
            }

            if (!query.isEmpty()) {

                String searchable =
                        payment.getId()
                                + " "
                                + payment.getAssessmentId()
                                + " "
                                + studentName
                                + " "
                                + termName
                                + " "
                                + payment.getPaymentMethod()
                                + " "
                                + nullSafe(
                                payment.getReferenceNumber()
                        )
                                + " "
                                + payment.getCashierId()
                                + " "
                                + payment.getStatus();

                if (!searchable
                        .toLowerCase()
                        .contains(query)) {

                    continue;
                }
            }

            tableModel.addRow(
                    new Object[]{
                            payment.getId(),
                            payment.getAssessmentId(),
                            studentName,
                            termName,
                            money(payment.getAmount()),
                            payment.getPaymentMethod(),
                            nullSafe(
                                    payment.getReferenceNumber()
                            ),
                            payment.getCashierId(),
                            payment.getStatus(),
                            payment.getPaidAt() == null
                                    ? ""
                                    : payment
                                    .getPaidAt()
                                    .format(dateFormatter)
                    }
            );
        }
    }

    private void showPaymentDialog() {

        JTextField assessmentField =
                new JTextField();

        assessmentField.setEditable(false);

        JButton assessmentButton =
                new JButton("▼");

        JPanel assessmentPanel =
                new JPanel(
                        new BorderLayout(5, 5)
                );

        assessmentPanel.add(
                assessmentField,
                BorderLayout.CENTER
        );

        assessmentPanel.add(
                assessmentButton,
                BorderLayout.EAST
        );

        final Assessment[] selectedAssessment =
                new Assessment[1];

        JLabel totalLabel =
                new JLabel("₱0.00");

        JLabel paidLabel =
                new JLabel("₱0.00");

        JLabel balanceLabel =
                new JLabel("₱0.00");

        JLabel cashierLabel =
                new JLabel();

        User currentUser =
                Session.getCurrentUser();

        if (currentUser != null) {
            cashierLabel.setText(
                    currentUser.getUsername()
            );
        } else {
            cashierLabel.setText(
                    "Not logged in"
            );
        }

        JTextField amountField =
                new JTextField("0.00");

        JComboBox<String> paymentMethodCombo =
                new JComboBox<>(
                        new String[]{
                                "Cash",
                                "Bank Transfer",
                                "GCash",
                                "Maya",
                                "Card",
                                "Check"
                        }
                );

        JTextField referenceField =
                new JTextField();

        assessmentButton.addActionListener(
                e -> {

                    AssessmentSelector selector =
                            new AssessmentSelector();

                    Assessment selected =
                            selector.showSelector(this);

                    if (selected != null) {

                        selectedAssessment[0] =
                                selected;

                        assessmentField.setText(
                                buildAssessmentDisplay(
                                        selected
                                )
                        );

                        totalLabel.setText(
                                money(
                                        selected
                                                .getTotalAmount()
                                )
                        );

                        paidLabel.setText(
                                money(
                                        selected
                                                .getAmountPaid()
                                )
                        );

                        balanceLabel.setText(
                                money(
                                        selected
                                                .getBalance()
                                )
                        );

                        amountField.setText(
                                selected
                                        .getBalance()
                                        .toPlainString()
                        );
                    }
                }
        );

        JPanel form =
                new JPanel(
                        new GridBagLayout()
                );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        5,
                        5,
                        5,
                        5
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        addRow(
                form,
                gbc,
                0,
                "Assessment:",
                assessmentPanel
        );

        addRow(
                form,
                gbc,
                1,
                "Total:",
                totalLabel
        );

        addRow(
                form,
                gbc,
                2,
                "Amount Paid:",
                paidLabel
        );

        addRow(
                form,
                gbc,
                3,
                "Balance:",
                balanceLabel
        );

        addRow(
                form,
                gbc,
                4,
                "Payment Amount:",
                amountField
        );

        addRow(
                form,
                gbc,
                5,
                "Payment Method:",
                paymentMethodCombo
        );

        addRow(
                form,
                gbc,
                6,
                "Reference Number:",
                referenceField
        );

        addRow(
                form,
                gbc,
                7,
                "Cashier:",
                cashierLabel
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        form,
                        "Record Payment",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            if (selectedAssessment[0] == null) {

                throw new IllegalArgumentException(
                        "Please select an assessment."
                );
            }

            if (currentUser == null) {

                throw new IllegalStateException(
                        "You must be logged in to record a payment."
                );
            }

            BigDecimal amount =
                    parseMoney(
                            amountField.getText()
                    );

            String paymentMethod =
                    paymentMethodCombo
                            .getSelectedItem()
                            .toString();

            String referenceNumber =
                    referenceField
                            .getText()
                            .trim();

            paymentService.recordPayment(
                    selectedAssessment[0].getId(),
                    currentUser.getId(),
                    amount,
                    paymentMethod,
                    referenceNumber.isBlank()
                            ? null
                            : referenceNumber
            );

            showMessage(
                    "Payment recorded successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadPayments(
                    searchField.getText()
            );

        } catch (Exception ex) {

            showMessage(
                    ex.getMessage() == null
                            ? "Unable to record payment."
                            : ex.getMessage(),
                    "Payment Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private class AssessmentSelector {

        private final DefaultListModel<Assessment> model =
                new DefaultListModel<>();

        private final JList<Assessment> list =
                new JList<>(model);

        private final JTextField searchField =
                new JTextField();

        private final List<Assessment> original;

        AssessmentSelector() {

            original =
                    assessmentService
                            .getAllAssessments();

            for (Assessment assessment :
                    original) {

                if (assessment
                        .getBalance()
                        .compareTo(
                                BigDecimal.ZERO
                        ) > 0) {

                    model.addElement(
                            assessment
                    );
                }
            }

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
                                    instanceof Assessment assessment) {

                                setText(
                                        buildAssessmentDisplay(
                                                assessment
                                        )
                                );
                            }

                            return this;
                        }
                    }
            );
        }

        Assessment showSelector(
                Component parent
        ) {

            JPanel panel =
                    new JPanel(
                            new BorderLayout(
                                    5,
                                    5
                            )
                    );

            searchField.putClientProperty(
                    "JTextField.placeholderText",
                    "Search assessment..."
            );

            panel.add(
                    searchField,
                    BorderLayout.NORTH
            );

            panel.add(
                    new JScrollPane(list),
                    BorderLayout.CENTER
            );

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

                                    for (Assessment assessment :
                                            original) {

                                        if (assessment
                                                .getBalance()
                                                .compareTo(
                                                        BigDecimal.ZERO
                                                ) <= 0) {

                                            continue;
                                        }

                                        String text =
                                                buildAssessmentDisplay(
                                                        assessment
                                                )
                                                        .toLowerCase();

                                        if (text.contains(
                                                query
                                        )) {

                                            model.addElement(
                                                    assessment
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
                            "Select Assessment",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                    );

            if (result != JOptionPane.OK_OPTION) {
                return null;
            }

            return list.getSelectedValue();
        }
    }

    private String buildAssessmentDisplay(
            Assessment assessment
    ) {

        Enrollment enrollment =
                findEnrollment(
                        assessment.getEnrollmentId()
                );

        if (enrollment == null) {

            return assessment.getId()
                    + " - Balance: "
                    + money(
                    assessment.getBalance()
            );
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

        return assessment.getId()
                + " - "
                + studentName
                + " - "
                + termName
                + " - Balance: "
                + money(
                assessment.getBalance()
        );
    }

    private Assessment findAssessment(
            String id
    ) {

        try {

            return assessmentService
                    .getAssessment(id);

        } catch (Exception e) {

            return null;
        }
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
                            student.getMiddleName()
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

    private String nullSafe(
            String value
    ) {

        return value == null
                ? ""
                : value;
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
}