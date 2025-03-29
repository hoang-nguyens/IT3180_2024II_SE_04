package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import services.EmailService;
import services.ForgotPasswordService;
import services.UserService;

import java.io.IOException;
import java.util.Optional;

@Controller
public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginStatusLabel;

    private static User loggedInUser;

    @Autowired
    private UserController userController;

    private final UserService userService;
    private final EmailService emailService;
    private final ForgotPasswordService forgotPasswordService;

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    @Autowired
    public LoginController(
            UserService userService,
            EmailService emailService,
            ForgotPasswordService forgotPasswordService
    ) {
        this.userService = userService;
        this.emailService = emailService;
        this.forgotPasswordService = forgotPasswordService;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            setLoginError("Vui lòng nhập thông tin!");
            return;
        }

        try {
            Optional<User> userOpt = userService.loginUser(username, password);

            if (userOpt.isPresent()) {
                loggedInUser = userOpt.get();
                setLoginSuccess("Đăng nhập thành công!");
                // Thực hiện chuyển hướng hoặc các hành động tiếp theo
                Stage loginStage = (Stage) usernameField.getScene().getWindow();

                openHomePage(new Stage());
                loginStage.close();

            } else {
                setLoginError("Tên đăng nhập hoặc mật khẩu sai!");
            }
        } catch (Exception e) {
            handleLoginError(e);
        }
    }

    @FXML
    private void handleRegisterClick() {
        userController.showRegisterBox();
    }

    @FXML
    private void handleForgotPasswordClick() {
        userController.showForgotPasswordBox();
    }

    private void setLoginError(String message) {
        loginStatusLabel.setStyle("-fx-text-fill: red;");
        loginStatusLabel.setText(message);
    }

    private void setLoginSuccess(String message) {
        loginStatusLabel.setStyle("-fx-text-fill: green;");
        loginStatusLabel.setText(message);
    }

    private void handleLoginError(Exception e) {
        loginStatusLabel.setStyle("-fx-text-fill: red;");
        loginStatusLabel.setText("Lỗi khi đăng nhập: " + e.getMessage());
        System.out.println(e.getMessage());
    }

    private void openHomePage(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HomePage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1250, 800); // Chỉnh lại kích thước cho đúng với FXML

            primaryStage.setTitle("HomePage");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.setResizable(false); // Ngăn người dùng chỉnh sửa kích thước
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}