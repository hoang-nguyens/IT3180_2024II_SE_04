package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import services.EmailService;
import services.ForgotPasswordService;
import services.UserService;

import java.util.regex.Pattern;

@Controller
public class RegisterController {

    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private String verificationCode;
    private User user = new User();
    private int state = 0;

    @FXML
    private TextField registerEmailField;
    @FXML
    private TextField registerUsernameField;
    @FXML
    private PasswordField registerPasswordField;
    @FXML
    private PasswordField registerPasswordCheckField;
    @FXML
    private Label registerStatusLabel;

    @Autowired
    private ConfirmController confirmController;
    @Autowired
    private UserController userController;

    private final UserService userService;
    private final EmailService emailService;
    private final ForgotPasswordService forgotPasswordService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegisterController(
            UserService userService,
            EmailService emailService,
            ForgotPasswordService forgotPasswordService,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.emailService = emailService;
        this.forgotPasswordService = forgotPasswordService;
        this.passwordEncoder = passwordEncoder;
    }

    @FXML
    private void initialize() {
        if(state == 0){
            clearRegister();
        }
    }

    @FXML
    private void handleSwitchToConfirmRegister() {
        if (!validateInput()) return;

        setupUserCredentials();

        if (state == 0 && !sendVerificationCode()) return;

        prepareConfirmation();
        userController.showConfirmBox();
    }

    @FXML
    private void handleLoginClick() {
        userController.showLoginBox();
        clearRegister();
    }

    private boolean validateInput() {
        String email = registerEmailField.getText().trim();
        String username = registerUsernameField.getText().trim();
        String password = registerPasswordField.getText().trim();
        String confirmPassword = registerPasswordCheckField.getText().trim();

        if (hasEmptyFields(email, username, password, confirmPassword)) {
            setRegisterError("Vui lòng nhập đầy đủ thông tin!");
            return false;
        }

        if (!isValidEmail(email)) {
            setRegisterError("Email không hợp lệ!");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            setRegisterError("Mật khẩu nhập lại không khớp!");
            return false;
        }

        if (password.length() < 8) {
            setRegisterError("Mật khẩu phải có ít nhất 8 ký tự!");
            return false;
        }

        if (userService.checkUserByUsername(username)) {
            setRegisterError("Tên tài khoản đã tồn tại!");
            return false;
        }

        if (userService.checkUserByEmail(email)) {
            setRegisterError("Email đã được sử dụng!");
            return false;
        }

        return true;
    }

    private void setupUserCredentials() {
        user.setEmail(registerEmailField.getText().trim());
        user.setUsername(registerUsernameField.getText().trim());

        if (state == 0) {
            String rawPassword = registerPasswordField.getText().trim();
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
        }
    }

    private boolean sendVerificationCode() {
        try {
            verificationCode = emailService.generateVerificationCode();
            emailService.sendVerificationCode(user.getEmail(), verificationCode);
            setRegisterSuccess("Mã xác nhận đã được gửi đi");
            return true;
        } catch (Exception e) {
            setRegisterError("Lỗi gửi email: " + e.getMessage());
            return false;
        }
    }

    private void prepareConfirmation() {
        state++;
        confirmController.setVerificationCode(verificationCode);
        confirmController.setCurAct(1);
        confirmController.setUser(user);
        confirmController.startCountdown();
    }

    private boolean hasEmptyFields(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) return true;
        }
        return false;
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public void clearRegister() {
        registerEmailField.clear();
        registerUsernameField.clear();
        registerPasswordField.clear();
        registerPasswordCheckField.clear();
        registerStatusLabel.setText("");
        state = 0;
    }

    private void setRegisterError(String message) {
        registerStatusLabel.setStyle("-fx-text-fill: red;");
        registerStatusLabel.setText(message);
    }

    private void setRegisterSuccess(String message) {
        registerStatusLabel.setStyle("-fx-text-fill: green;");
        registerStatusLabel.setText(message);
    }
}