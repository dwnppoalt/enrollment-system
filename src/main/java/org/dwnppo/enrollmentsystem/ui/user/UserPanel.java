package org.dwnppo.enrollmentsystem.ui.users;

import org.dwnppo.enrollmentsystem.model.Role;
import org.dwnppo.enrollmentsystem.model.User;
import org.dwnppo.enrollmentsystem.service.UserService;
import org.dwnppo.enrollmentsystem.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserPanel extends JPanel {

    private final UserService userService;

    private final JTextField searchField = new JTextField();
    private final JTable userTable;
    private final DefaultTableModel tableModel;

    public UserPanel(UserService userService) {
        this.userService = userService;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(
                titleLabel.getFont().deriveFont(Font.BOLD, 22f)
        );

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));

        searchField.putClientProperty(
                "JTextField.placeholderText",
                "Search username, role, or student ID..."
        );

        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // ---------------------------------------------------------
        // Table
        // ---------------------------------------------------------

        tableModel = new DefaultTableModel(
                new Object[]{
                        "Username",
                        "Student ID",
                        "Role",
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

        userTable = new JTable(tableModel);
        userTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        userTable.setRowHeight(28);

        add(
                new JScrollPane(userTable),
                BorderLayout.CENTER
        );

        // ---------------------------------------------------------
        // Bottom
        // ---------------------------------------------------------

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit");
        JButton passwordButton = new JButton("Change Password");
        JButton roleButton = new JButton("Manage Roles");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(passwordButton);
        buttonPanel.add(roleButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // ---------------------------------------------------------
        // Events
        // ---------------------------------------------------------

        searchButton.addActionListener(e -> loadUsers());
        refreshButton.addActionListener(e -> loadUsers());

        searchField.addActionListener(e -> loadUsers());

        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        passwordButton.addActionListener(e -> showPasswordDialog());
        roleButton.addActionListener(e -> showRoleDialog());

        loadUsers();
    }

    private void loadUsers() {
        tableModel.setRowCount(0);

        String search = searchField.getText()
                .trim()
                .toLowerCase();

        List<User> users = userService.getAllUsers();

        for (User user : users) {

            String roles = user.getRoles()
                    .stream()
                    .map(Role::getName)
                    .reduce(
                            (a, b) -> a + ", " + b
                    )
                    .orElse("No Role");

            String studentId =
                    user.getStudentId() == null
                            ? ""
                            : user.getStudentId();

            String status =
                    user.isActive()
                            ? "Active"
                            : "Inactive";

            String searchable = (
                    user.getUsername()
                            + " "
                            + studentId
                            + " "
                            + roles
                            + " "
                            + status
            ).toLowerCase();

            if (!search.isBlank()
                    && !searchable.contains(search)) {
                continue;
            }

            tableModel.addRow(new Object[]{
                    user.getUsername(),
                    studentId,
                    roles,
                    status
            });
        }
    }

    private User getSelectedUser() {
        int row = userTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a user first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return null;
        }

        String username =
                tableModel.getValueAt(row, 0).toString();

        try {
            return userService.getUserByUsername(username);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return null;
        }
    }

    // =============================================================
    // ADD USER
    // =============================================================

    private void showAddDialog() {

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField studentIdField = new JTextField();

        JComboBox<String> roleCombo =
                new JComboBox<>();

        for (Role role : userService.getAllRoles()) {
            roleCombo.addItem(role.getName());
        }

        JPanel panel = new JPanel(
                new GridLayout(0, 2, 8, 8)
        );

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        panel.add(new JLabel("Student ID:"));
        panel.add(studentIdField);

        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add User",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            userService.createUser(
                    usernameField.getText().trim(),
                    new String(passwordField.getPassword()),
                    studentIdField.getText().trim(),
                    (String) roleCombo.getSelectedItem()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "User created successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadUsers();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Unable to Create User",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =============================================================
    // EDIT USER
    // =============================================================

    private void showEditDialog() {

        User user = getSelectedUser();

        if (user == null) {
            return;
        }

        JTextField usernameField =
                new JTextField(user.getUsername());

        JTextField studentIdField =
                new JTextField(
                        user.getStudentId() == null
                                ? ""
                                : user.getStudentId()
                );

        JCheckBox activeCheckBox =
                new JCheckBox(
                        "Account is active",
                        user.isActive()
                );

        JPanel panel = new JPanel(
                new GridLayout(0, 2, 8, 8)
        );

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);

        panel.add(new JLabel("Student ID:"));
        panel.add(studentIdField);

        panel.add(new JLabel("Status:"));
        panel.add(activeCheckBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit User",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            userService.updateUser(
                    user.getId(),
                    usernameField.getText().trim(),
                    studentIdField.getText().trim(),
                    activeCheckBox.isSelected()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "User updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadUsers();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Unable to Update User",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =============================================================
    // PASSWORD
    // =============================================================

    private void showPasswordDialog() {

        User user = getSelectedUser();

        if (user == null) {
            return;
        }

        JPasswordField passwordField =
                new JPasswordField();

        JPasswordField confirmField =
                new JPasswordField();

        JPanel panel = new JPanel(
                new GridLayout(0, 2, 8, 8)
        );

        panel.add(new JLabel("New Password:"));
        panel.add(passwordField);

        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Change Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String password =
                new String(passwordField.getPassword());

        String confirmation =
                new String(confirmField.getPassword());

        if (!password.equals(confirmation)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match.",
                    "Invalid Password",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            userService.changePassword(
                    user.getId(),
                    password
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Password changed successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Unable to Change Password",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =============================================================
    // ROLES
    // =============================================================

    private void showRoleDialog() {

        User user = getSelectedUser();

        if (user == null) {
            return;
        }

        List<Role> roles = userService.getAllRoles();

        String[] roleNames = roles.stream()
                .map(Role::getName)
                .toArray(String[]::new);

        String currentRole = user.getRoles()
                .stream()
                .map(Role::getName)
                .findFirst()
                .orElse(null);

        JComboBox<String> roleCombo =
                new JComboBox<>(roleNames);

        if (currentRole != null) {
            roleCombo.setSelectedItem(currentRole);
        }

        JPanel panel = new JPanel(
                new GridLayout(0, 2, 8, 8)
        );

        panel.add(new JLabel("User:"));
        panel.add(new JLabel(user.getUsername()));

        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Manage Role",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String selectedRole =
                (String) roleCombo.getSelectedItem();

        if (selectedRole == null) {
            return;
        }

        try {

            /*
             * For this system we use one primary role per account.
             * Remove existing roles first.
             */
            for (Role role : user.getRoles()) {
                userService.removeRole(
                        user.getId(),
                        role.getName()
                );
            }

            userService.assignRole(
                    user.getId(),
                    selectedRole
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Role updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadUsers();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Unable to Update Role",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}