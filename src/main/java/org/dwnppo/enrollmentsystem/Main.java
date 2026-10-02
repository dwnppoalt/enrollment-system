package org.dwnppo.enrollmentsystem;

import org.dwnppo.enrollmentsystem.repository.UserRepository;
import org.dwnppo.enrollmentsystem.service.AuthenticationService;
import org.dwnppo.enrollmentsystem.ui.login.LoginFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

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
        });
    }
}