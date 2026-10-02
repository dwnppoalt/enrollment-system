package org.dwnppo.enrollmentsystem.ui.dashboard;

import org.dwnppo.enrollmentsystem.model.User;
import org.dwnppo.enrollmentsystem.repository.*;
import org.dwnppo.enrollmentsystem.service.*;
import org.dwnppo.enrollmentsystem.ui.assessment.AssessmentPanel;
import org.dwnppo.enrollmentsystem.ui.enrollment.EnrollmentPanel;
import org.dwnppo.enrollmentsystem.ui.login.LoginFrame;
import org.dwnppo.enrollmentsystem.ui.students.StudentPanel;
import org.dwnppo.enrollmentsystem.util.Session;
import org.dwnppo.enrollmentsystem.ui.programs.ProgramPanel;
import org.dwnppo.enrollmentsystem.ui.payments.PaymentPanel;
import org.dwnppo.enrollmentsystem.ui.users.UserPanel;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private JPanel contentPanel;
    private JLabel pageTitleLabel;
    private StudentPanel studentPanel;
    private ProgramPanel programPanel;
    private EnrollmentPanel enrollmentPanel;
    private AssessmentPanel assessmentPanel;
    private PaymentPanel paymentPanel;
    private UserPanel userPanel;

    public DashboardFrame() {

        if (!Session.isLoggedIn()) {
            throw new IllegalStateException(
                    "No user is currently logged in."
            );
        }

        setTitle("Enrollment System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create repositories
        StudentRepository studentRepository =
                new StudentRepository();

        ProgramRepository programRepository =
                new ProgramRepository();

        EnrollmentRepository enrollmentRepository = new EnrollmentRepository();

        AcademicTermRepository academicTermRepository = new AcademicTermRepository();

        AssessmentRepository assessmentRepository = new AssessmentRepository();

        PaymentRepository paymentRepository = new PaymentRepository();

        EnrollmentService enrollmentService = new EnrollmentService(
                enrollmentRepository,
                studentRepository,
                academicTermRepository);

        // Create services
        StudentService studentService =
                new StudentService(
                        studentRepository,
                        programRepository
                );

        ProgramService programService =
                new ProgramService(
                        programRepository
                );

        AcademicTermService academicTermService = new AcademicTermService(academicTermRepository);

        AssessmentService assessmentService = new AssessmentService(
                assessmentRepository,
                enrollmentRepository
        );

        PaymentService paymentService = new PaymentService(
                paymentRepository,
                assessmentRepository
        );

        // Create student panel BEFORE creating sidebar
        studentPanel =
                new StudentPanel(
                        studentService,
                        programService
                );

        programPanel = new ProgramPanel(
                programService
        );

        enrollmentPanel = new EnrollmentPanel(
                enrollmentService,
                studentService,
                new AcademicTermService(academicTermRepository)
        );

        assessmentPanel = new AssessmentPanel(
                assessmentService,
                enrollmentService,
                studentService,
                academicTermService
        );

        paymentPanel = new PaymentPanel(
                paymentService,
                assessmentService,
                enrollmentService,
                studentService,
                academicTermService
        );

        userPanel = new UserPanel(
                new UserService(
                        new UserRepository(),
                        new RoleRepository()
                )
        );

        JPanel rootPanel =
                new JPanel(new BorderLayout());

        rootPanel.add(
                createSidebar(),
                BorderLayout.WEST
        );

        rootPanel.add(
                createContent(),
                BorderLayout.CENTER
        );

        add(rootPanel);

        showDashboard();
    }

    private void showStudents() {

        pageTitleLabel.setText("Students");

        contentPanel.removeAll();

        contentPanel.add(
                studentPanel,
                BorderLayout.CENTER
        );

        refreshContent();
    }

    private void showPrograms() {

        pageTitleLabel.setText("Programs");

        contentPanel.removeAll();

        contentPanel.add(
                programPanel,
                BorderLayout.CENTER
        );

        refreshContent();
    }

    private void showEnrollment() {
        pageTitleLabel.setText("Enrollment");

        contentPanel.removeAll();

        contentPanel.add(
                enrollmentPanel,
                BorderLayout.CENTER
        );

        refreshContent();
    }

    private void showAssessments() {
        pageTitleLabel.setText("Assessments");
        contentPanel.removeAll();

        contentPanel.add(
                assessmentPanel,
                BorderLayout.CENTER
        );

        refreshContent();
    }

    private void showPayment() {
        pageTitleLabel.setText("Payment");
        contentPanel.removeAll();

        contentPanel.add(
                paymentPanel,
                BorderLayout.CENTER
        );

        refreshContent();
    }

    private void showUsers() {
        pageTitleLabel.setText("Users");
        contentPanel.removeAll();

        contentPanel.add(
                userPanel,
                BorderLayout.CENTER
        );

        refreshContent();
    }

    private JPanel createSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setPreferredSize(
                new Dimension(220, 0)
        );

        sidebar.setLayout(
                new BoxLayout(
                        sidebar,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel title =
                new JLabel("Enrollment System");

        title.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        20
                )
        );

        title.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        sidebar.add(title);

        sidebar.add(
                createNavigationButton(
                        "Dashboard",
                        this::showDashboard
                )
        );

        if (Session.hasRole("REGISTRAR")
                || Session.hasRole("ADMIN")) {
            sidebar.add(
                    createNavigationButton(
                            "Students",
                            this::showStudents
                    )
            );
        }

        if (Session.hasRole("REGISTRAR")
                || Session.hasRole("ADMIN")) {
            sidebar.add(
                    createNavigationButton(
                            "Programs",
                            this::showPrograms
                    )
            );
        }

            sidebar.add(
                    createNavigationButton(
                            "Enrollment",
                            this::showEnrollment
                    )
            );


            sidebar.add(
                    createNavigationButton(
                            "Assessments",
                            this::showAssessments
                    )
            );


        if (Session.hasRole("CASHIER")
                || Session.hasRole("ADMIN")) {

            sidebar.add(
                    createNavigationButton(
                            "Payments",
                            this::showPayment
                    )
            );
        }

        if (Session.hasRole("ADMIN")) {

            sidebar.add(
                    createNavigationButton(
                            "Users",
                            this::showUsers
                    )
            );
        }

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);

        sidebar.add(spacer);

        sidebar.add(
                createNavigationButton(
                        "Logout",
                        this::logout
                )
        );

        return sidebar;
    }

    private JButton createNavigationButton(
            String text,
            Runnable action
    ) {

        JButton button =
                new JButton(text);

        button.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        button.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        45
                )
        );

        button.addActionListener(
                event -> action.run()
        );

        return button;
    }

    private JPanel createContent() {

        JPanel container =
                new JPanel(new BorderLayout());

        pageTitleLabel =
                new JLabel();

        pageTitleLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        24
                )
        );

        pageTitleLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        25,
                        20,
                        25
                )
        );

        container.add(
                pageTitleLabel,
                BorderLayout.NORTH
        );

        contentPanel =
                new JPanel(new BorderLayout());

        container.add(
                contentPanel,
                BorderLayout.CENTER
        );

        return container;
    }

    private void showDashboard() {

        pageTitleLabel.setText("Dashboard");

        contentPanel.removeAll();

        User user =
                Session.getCurrentUser();

        JPanel dashboard =
                new JPanel(new GridBagLayout());

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        15,
                        15,
                        15,
                        15
                );

        JLabel welcomeLabel =
                new JLabel(
                        "Welcome, "
                                + user.getUsername()
                                + "!"
                );

        welcomeLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        20
                )
        );

        gbc.gridx = 0;
        gbc.gridy = 0;

        dashboard.add(
                welcomeLabel,
                gbc
        );

        JLabel roleLabel =
                new JLabel(
                        "Role: "
                                + getUserRoles()
                );

        gbc.gridy++;

        dashboard.add(
                roleLabel,
                gbc
        );

        contentPanel.add(
                dashboard,
                BorderLayout.NORTH
        );

        refreshContent();
    }

    private void showPlaceholder(String page) {

        pageTitleLabel.setText(page);

        contentPanel.removeAll();

        JLabel label =
                new JLabel(
                        page + " module coming soon."
                );

        label.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        label.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        18
                )
        );

        contentPanel.add(
                label,
                BorderLayout.CENTER
        );

        refreshContent();
    }

    private String getUserRoles() {

        User user =
                Session.getCurrentUser();

        return user.getRoles()
                .stream()
                .map(role -> role.getName())
                .reduce(
                        (first, second) ->
                                first + ", " + second
                )
                .orElse("No role");
    }

    private void logout() {

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to logout?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION
                );

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        Session.logout();

        dispose();

        UserRepository userRepository =
                new UserRepository();

        AuthenticationService authenticationService =
                new AuthenticationService(
                        userRepository
                );

        LoginFrame loginFrame =
                new LoginFrame(
                        authenticationService
                );

        loginFrame.setVisible(true);
    }

    private void refreshContent() {

        contentPanel.revalidate();
        contentPanel.repaint();
    }
}