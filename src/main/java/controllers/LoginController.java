package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;

import java.util.Optional;
import java.net.URL;
import java.util.ResourceBundle;

import services.UserService;

@Controller
public class LoginController {
    private final UserService userService;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label loginStatusLabel;

    @FXML private VBox loginBox;
    @FXML private VBox registerBox;
    @FXML private Hyperlink registerLink;
    @FXML private Hyperlink loginLink;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            loginStatusLabel.setText("Vui lòng nhập thông tin");
            return;
        }

        Optional<String> hashedPasswordOpt = userService.getPasswordByUsername(username);
        if (hashedPasswordOpt.isEmpty()) {
            loginStatusLabel.setText("Tên đăng nhập sai!");
        } else if (!BCrypt.checkpw(password, hashedPasswordOpt.get())) {
            loginStatusLabel.setText("Mật khẩu sai!");
        } else {
            loginStatusLabel.setText("Đăng nhập thành công!");
        }
    }

    private void showLoginBox() {
        loginBox.setVisible(true);
        loginBox.setManaged(true);
        registerBox.setVisible(false);
        registerBox.setManaged(false);
    }

    private void showRegisterBox() {
        registerBox.setVisible(true);
        registerBox.setManaged(true);
        loginBox.setVisible(false);
        loginBox.setManaged(false);
    }

    @FXML
    private void handleRegisterClick() {
        showRegisterBox();
        System.out.println("Hyperlink clicked - Chuyển sang đăng ký");
    }

    @FXML
    private void handleLoginClick(){
        showLoginBox();
    }

}