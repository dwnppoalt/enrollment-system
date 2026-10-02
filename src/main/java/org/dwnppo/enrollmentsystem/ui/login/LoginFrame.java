package org.dwnppo.enrollmentsystem.ui.login;

import org.dwnppo.enrollmentsystem.model.User;
import org.dwnppo.enrollmentsystem.service.AuthenticationService;
import org.dwnppo.enrollmentsystem.util.Session;
import org.dwnppo.enrollmentsystem.ui.dashboard.DashboardFrame;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final AuthenticationService authenticationService;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame(
            AuthenticationService authenticationService
    ) {
        this.authenticationService = authenticationService;

        initializeFrame();
        initializeComponents();
        initializeLayout();
        initializeActions();
    }

    private void initializeFrame() {

        setTitle("Enrollment System - Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initializeComponents() {

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        usernameField.setPreferredSize(
                new Dimension(250, 40)
        );

        passwordField.setPreferredSize(
                new Dimension(250, 40)
        );

        loginButton = new JButton("Login");

        loginButton.setPreferredSize(
                new Dimension(250, 40)
        );
    }

    private void initializeLayout() {

        JPanel mainPanel = new JPanel(
                new GridBagLayout()
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel =
                new JLabel("Enrollment System");

        titleLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        24
                )
        );

        titleLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        JLabel subtitleLabel =
                new JLabel("Login to your account");

        subtitleLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        JLabel usernameLabel =
                new JLabel("Username");

        JLabel passwordLabel =
                new JLabel("Password");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        mainPanel.add(titleLabel, gbc);

        gbc.gridy++;

        mainPanel.add(subtitleLabel, gbc);

        gbc.gridwidth = 1;

        gbc.gridy++;
        gbc.gridx = 0;

        mainPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;

        mainPanel.add(usernameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;

        mainPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;

        mainPanel.add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        mainPanel.add(loginButton, gbc);

        add(mainPanel);
    }

    private void initializeActions() {

        loginButton.addActionListener(event ->
                login()
        );

        passwordField.addActionListener(event ->
                login()
        );
    }

    private void login() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        try {

            User user =
                    authenticationService.login(
                            username,
                            password
                    );

            Session.login(user);

            JOptionPane.showMessageDialog(
                    this,
                    "Welcome, " + user.getUsername() + "!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

            DashboardFrame dashboardFrame = new DashboardFrame();

            dashboardFrame.setVisible(true);

        } catch (IllegalArgumentException
                 | IllegalStateException exception) {

            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );

            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
}