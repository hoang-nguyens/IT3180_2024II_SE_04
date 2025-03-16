package controllers;  // Đảm bảo package này khớp với khai báo trong FXML

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.swing.text.html.Option;
import java.util.Optional;
import services.UserService;
@Controller
public class LoginController {  // Đảm bảo class phải là public
    private final UserService userService;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label loginStatusLabel;
    public LoginController(UserService userService) {
        this.userService = userService;
    }
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if(username.isBlank()||password.isBlank()){
            loginStatusLabel.setText("Vui lòng nhập thông tin");
        }
        else {
            System.out.println("Đăng nhập với: " + username + " - " + password);
            Optional<String> hashedPasswordOpt = userService.getPasswordByUsername(username);

            if (hashedPasswordOpt.isEmpty()) {
                loginStatusLabel.setText("Tên đăng nhập sai!");
            } else if (!BCrypt.checkpw(password, hashedPasswordOpt.get())) {
                loginStatusLabel.setText("Mật khẩu sai!");
            } else {
                loginStatusLabel.setText("Đăng nhập thành công!");
            }
        }
    }
}
