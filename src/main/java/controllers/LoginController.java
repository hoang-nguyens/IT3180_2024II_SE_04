package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import services.EmailService;
import services.ForgotPasswordService;
import services.UserService;

import java.util.Optional;

@Controller
public class LoginController { // Không kế thừa từ UserController nữa

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
            Optional<String> hashedPasswordOpt = userService.getPasswordByUsername(username);
            if (hashedPasswordOpt.isEmpty()) {
                loginStatusLabel.setStyle("-fx-text-fill: red;");
                loginStatusLabel.setText("Tên đăng nhập sai!");
            } else if (!BCrypt.checkpw(password, hashedPasswordOpt.get())) {
                loginStatusLabel.setStyle("-fx-text-fill: red;");
                loginStatusLabel.setText("Mật khẩu sai!");
            } else {
                loginStatusLabel.setStyle("-fx-text-fill: green;");
                loginStatusLabel.setText("Đăng nhập thành công!");
                // Thực hiện chuyển hướng hoặc các hành động tiếp theo sau khi đăng nhập thành công
            }
        } catch (Exception e) {
            loginStatusLabel.setStyle("-fx-text-fill: red;");
            loginStatusLabel.setText("Lỗi khi đăng nhập: " + e.getMessage());
        }
    }

    @FXML
    protected void handleRegisterClick() {
        // Gọi phương thức showRegisterBox() từ UserController
        userController.showRegisterBox();
    }

    @FXML
    protected void handleForgotPasswordClick() {
        // Gọi phương thức showForgotPasswordBox() từ UserController
        userController.showForgotPasswordBox();
    }
}