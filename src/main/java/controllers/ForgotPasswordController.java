package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import services.EmailService;
import services.ForgotPasswordService;
import services.UserService;

import java.util.Optional;

@Controller
public class ForgotPasswordController { // Không kế thừa từ UserController nữa

    private String verificationCode;
    private User user;
    private int state = 0;
    @FXML private TextField newPasswordField;
    @FXML private TextField cfNewPasswordField;
    @FXML private TextField forgotPasswordUserNameField;
    @FXML private Label forgotPasswordStatusLabel;

    @Autowired
    private UserController userController; // Inject UserController

    @Autowired
    private ConfirmController confirmController; // Inject ConfirmController

    private final UserService userService;
    private final EmailService emailService;
    private final ForgotPasswordService forgotPasswordService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ForgotPasswordController(UserService userService, EmailService emailService, ForgotPasswordService forgotPasswordService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.emailService = emailService;
        this.forgotPasswordService = forgotPasswordService;
        this.passwordEncoder = passwordEncoder;
    }

    @FXML
    protected void handleSwitchToConfirmForgotPassword() {
        String userName = forgotPasswordUserNameField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String cfNewPassword = cfNewPasswordField.getText().trim();

        if (userName.isEmpty() || newPassword.isEmpty() || cfNewPassword.isEmpty()) {
            forgotPasswordStatusLabel.setStyle("-fx-text-fill: red;");
            forgotPasswordStatusLabel.setText("Vui lòng nhập đủ thông tin");
            return;
        }

        if(newPassword.length()<8){
            forgotPasswordStatusLabel.setStyle("-fx-text-fill: red;");
            forgotPasswordStatusLabel.setText("Mật khẩu phải có ít nhất 8 ký tự!");
            return;
        }

        if (!newPassword.equals(cfNewPassword)) {
            forgotPasswordStatusLabel.setStyle("-fx-text-fill: red;");
            forgotPasswordStatusLabel.setText("Mật khẩu chưa khớp");
            return;
        }

        Optional<String> emailOpt = userService.getEmailByUsername(userName);
        if (emailOpt.isEmpty()) {
            forgotPasswordStatusLabel.setStyle("-fx-text-fill: red;");
            forgotPasswordStatusLabel.setText("Tên tài khoản không tồn tại.");
            return;
        }
        if(state == 0) {
            String email = emailOpt.get();
            verificationCode = emailService.generateVerificationCode();

            try {
                System.out.println("Sending email to: " + email + " with code: " + verificationCode);
                emailService.sendVerificationCode(email, verificationCode);
                forgotPasswordStatusLabel.setStyle("-fx-text-fill: green;");
                forgotPasswordStatusLabel.setText("Mã xác nhận đã được gửi đi");
            } catch (Exception e) {
                forgotPasswordStatusLabel.setStyle("-fx-text-fill: red;");
                forgotPasswordStatusLabel.setText("Lỗi gửi email: " + e.getMessage());
            }
        }
        user = userService.setUserByUsername(userName);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        state++;
        //Setup data cho trang Confirm
        confirmController.setVerificationCode(verificationCode);
        confirmController.setCurAct(2);
        confirmController.setUser(user);
        userController.showConfirmBox();
    }

    public void clearForgotPassword(){
        newPasswordField.clear();
        cfNewPasswordField.clear();
        forgotPasswordUserNameField.clear();
        forgotPasswordStatusLabel.setText("");
        state = 0;
    }
    @FXML
    protected void handleLoginClick() {
        userController.showLoginBox();
        clearForgotPassword();
    }
}