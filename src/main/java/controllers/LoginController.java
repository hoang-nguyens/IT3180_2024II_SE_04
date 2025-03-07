package controllers;  // Đảm bảo package này khớp với khai báo trong FXML

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {  // Đảm bảo class phải là public
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        System.out.println("Đăng nhập với: " + username + " - " + password);
    }
}
