package controllers;

import models.User;
import services.EmailService;
import services.ForgotPasswordService;
import jakarta.mail.MessagingException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.util.Optional;

public class ForgotPasswordController {
    @FXML private TextField usernameField;

    private final ForgotPasswordService forgotPasswordService;
    private final EmailService emailService;

    public ForgotPasswordController(ForgotPasswordService forgotPasswordService, EmailService emailService) {
        this.forgotPasswordService = forgotPasswordService;
        this.emailService = emailService;
    }

    @FXML
    private void handleSendCode() {
        String username = usernameField.getText().trim();

        Optional<User> userOptional = forgotPasswordService.getUserByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String verificationCode = emailService.generateVerificationCode();

            try {
                emailService.sendVerificationCode(user.getEmail(), verificationCode);
                showAlert("Thành công", "Mã xác nhận đã được gửi đến email!");
            } catch (MessagingException e) {
                showAlert("Lỗi", "Không thể gửi email. Vui lòng thử lại.");
            }
        } else {
            showAlert("Lỗi", "Không tìm thấy người dùng!");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
