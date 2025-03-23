package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import services.EmailService;
import services.ForgotPasswordService;
import services.UserService;

import java.util.Optional;

@Controller
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label loginStatusLabel;

    @Autowired
    private UserController userController; // Inject UserController

    private final UserService userService;
    private final EmailService emailService;
    private final ForgotPasswordService forgotPasswordService;

    @Autowired
    public LoginController(UserService userService, EmailService emailService, ForgotPasswordService forgotPasswordService) {
        this.userService = userService;
        this.emailService = emailService;
        this.forgotPasswordService = forgotPasswordService;
    }

    @FXML
    protected void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setStyle("-fx-text-fill: red;");
            loginStatusLabel.setText("Vui lòng nhập thông tin!");
            return;
        }

        try {
            Optional<User> userOpt = userService.loginUser(username, password);

            if (userOpt.isPresent()) {
                loginStatusLabel.setStyle("-fx-text-fill: green;");
                loginStatusLabel.setText("Đăng nhập thành công!");

                // Thực hiện chuyển hướng hoặc các hành động tiếp theo sau khi đăng nhập thành công
                //HERE//
            } else {
                loginStatusLabel.setStyle("-fx-text-fill: red;");
                loginStatusLabel.setText("Tên đăng nhập hoặc mật khẩu sai!");
            }
        } catch (Exception e) {
            loginStatusLabel.setStyle("-fx-text-fill: red;");
            loginStatusLabel.setText("Lỗi khi đăng nhập: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    @FXML
    protected void handleRegisterClick() {
        userController.showRegisterBox();
    }

    @FXML
    protected void handleForgotPasswordClick() {
        userController.showForgotPasswordBox();
    }
}