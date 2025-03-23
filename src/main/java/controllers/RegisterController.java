package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import services.EmailService;
import services.ForgotPasswordService;
import services.UserService;

import java.util.regex.Pattern;

@Controller
public class RegisterController { // Không kế thừa từ UserController nữa

    private String verificationCode;

    @FXML private TextField registerEmailField;
    @FXML private TextField registerUsernameField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField registerPasswordCheckField;
    @FXML private Label registerStatusLabel;

    @Autowired
    private ConfirmController confirmController; // Inject ConfirmController

    @Autowired
    private UserController userController; // Inject UserController

    private final UserService userService;
    private final EmailService emailService;
    private final ForgotPasswordService forgotPasswordService;

    @Autowired
    public RegisterController(UserService userService, EmailService emailService, ForgotPasswordService forgotPasswordService) {
        this.userService = userService;
        this.emailService = emailService;
        this.forgotPasswordService = forgotPasswordService;
    }

    @FXML
    public void handleSwitchToConfirmRegister() {
        String registerEmail = registerEmailField.getText().trim();
        String registerUsername = registerUsernameField.getText().trim();
        String registerPassword = registerPasswordField.getText().trim();
        String registerCfPassword = registerPasswordCheckField.getText().trim();

        if (registerEmail.isEmpty() || registerUsername.isEmpty() || registerPassword.isEmpty() || registerCfPassword.isEmpty()) {
            registerStatusLabel.setStyle("-fx-text-fill: red;");
            registerStatusLabel.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (!isValidEmail(registerEmail)) {
            registerStatusLabel.setStyle("-fx-text-fill: red;");
            registerStatusLabel.setText("Email không hợp lệ!");
            return;
        }

        if (!registerPassword.equals(registerCfPassword)) {
            registerStatusLabel.setStyle("-fx-text-fill: red;");
            registerStatusLabel.setText("Mật khẩu nhập lại không khớp!");
            return;
        }

        verificationCode = emailService.generateVerificationCode();
        try {
            emailService.sendVerificationCode(registerEmail, verificationCode);

            // Truyền verificationCode sang ConfirmController
            confirmController.setVerificationCode(verificationCode);

            // Gọi phương thức showConfirmBox() từ UserController
            userController.showConfirmBox();
        } catch (Exception e) {
            registerStatusLabel.setStyle("-fx-text-fill: red;");
            registerStatusLabel.setText("Lỗi gửi email: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    @FXML
    protected void handleLoginClick() {
        // Gọi phương thức handleLoginClick() từ UserController
        userController.showLoginBox();
    }
}