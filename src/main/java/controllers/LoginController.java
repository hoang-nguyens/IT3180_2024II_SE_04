package controllers;

import jakarta.validation.constraints.Email;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import models.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Field;
import java.util.Optional;
import java.net.URL;
import java.util.ResourceBundle;

import services.EmailService;
import services.ForgotPasswordService;
import services.UserService;

@Controller
public class LoginController {
    private final UserService userService;
    private final EmailService emailService;
    private final ForgotPasswordService forgotPasswordService;
    private String curAct;
    private String curUser;
    private String curPass;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label loginStatusLabel;

    @FXML private VBox loginBox;
    @FXML private VBox registerBox;
    @FXML private VBox confirmBox;
    @FXML private VBox forgotPasswordBox;

    private String verificationCode;

    public LoginController(UserService userService, EmailService emailService, ForgotPasswordService forgotPasswordService) {
        this.userService = userService;
        this.emailService = emailService;
        this.forgotPasswordService = forgotPasswordService;
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
        confirmBox.setVisible(false);
        confirmBox.setManaged(false);
        forgotPasswordBox.setVisible(false);
        forgotPasswordBox.setManaged(false);
    }

    private void showRegisterBox() {
        registerBox.setVisible(true);
        registerBox.setManaged(true);
        loginBox.setVisible(false);
        loginBox.setManaged(false);
        confirmBox.setVisible(false);
        confirmBox.setManaged(false);
        forgotPasswordBox.setVisible(false);
        forgotPasswordBox.setManaged(false);
    }

    private void showConfirmBox(){
        confirmBox.setVisible(true);
        confirmBox.setManaged(true);
        registerBox.setVisible(false);
        registerBox.setManaged(false);
        loginBox.setVisible(false);
        loginBox.setManaged(false);
        forgotPasswordBox.setVisible(false);
        forgotPasswordBox.setManaged(false);
    }

    private void showForgotPasswordBox(){
        forgotPasswordBox.setVisible(true);
        forgotPasswordBox.setManaged(true);
        registerBox.setVisible(false);
        registerBox.setManaged(false);
        loginBox.setVisible(false);
        loginBox.setManaged(false);
        confirmBox.setVisible(false);
        confirmBox.setManaged(false);
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
    @FXML
    private void handleForgotPasswordClick(){
        showForgotPasswordBox();
    }
    //@FXML private TextField registerIdField;
    @FXML private TextField registerEmailField;
    @FXML private TextField registerUsernameField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField registerPasswordCheckField;
    @FXML private Label registerStatusLabel;
    @FXML private Label cfregisterStatusLabel;
    private String registerEmail;
    private String registerUsername;
    private String registerPassword;
    private String registerCfPassword;
    @FXML
    private void handleSwitchToConfirmRegister() {
        // Lấy thông tin từ giao diện
        //String idText = registerIdField.getText();
        registerEmail = registerEmailField.getText();
        registerUsername = registerUsernameField.getText();
        registerPassword = registerPasswordField.getText();
        registerCfPassword = registerPasswordCheckField.getText();
        // Kiểm tra nếu có trường nào bị bỏ trống
        if (registerEmail.isBlank() || registerUsername.isBlank() || registerPassword.isBlank() || registerCfPassword.isBlank()) {
            registerStatusLabel.setStyle("-fx-text-fill: red;");
            registerStatusLabel.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // Kiểm tra mật khẩu nhập lại
        if (!registerPassword.equals(registerCfPassword)) {
            registerStatusLabel.setStyle("-fx-text-fill: red;");
            registerStatusLabel.setText("Mật khẩu nhập lại không khớp!");
            return;
        }

        verificationCode = emailService.generateVerificationCode();  // Sinh mã xác nhận
        try {
            System.out.println("Sending email to: " + registerEmail + " with code: " + verificationCode);
            emailService.sendVerificationCode(registerEmail, verificationCode);
            curAct = "Register";
            curPass = registerPassword;
            curPass = registerPassword;
            showConfirmBox();
        } catch (Exception e) {
            registerStatusLabel.setStyle("-fx-text-fill: red;");
            registerStatusLabel.setText("Lỗi gửi email: " + e.getMessage());
        }
        showConfirmBox();
    }

    // CF Register Box
    @FXML private TextField mot;
    @FXML private TextField hai;
    @FXML private TextField ba;
    @FXML private TextField bon;
    @FXML private TextField nam;
    @FXML private TextField sau;
    @FXML private Button backToLoginButton;
    @FXML
    private void handleKeyTyped(KeyEvent event) {
        TextField current = (TextField) event.getSource();
        String text = current.getText();

        if (text.length() > 1) {
            current.setText(text.substring(0, 1)); // Chỉ giữ lại 1 ký tự đầu tiên
            current.positionCaret(1); // Đưa con trỏ về cuối
        }

        // Tự động chuyển sang ô tiếp theo nếu có ký tự
        TextField[] fields = {mot, hai, ba, bon, nam, sau};
        for (int i = 0; i < fields.length - 1; i++) {
            if (current == fields[i] && !text.isEmpty()) {
                fields[i + 1].requestFocus();
                break;
            }
        }
    }

    /*confirm đăng kí*/
    @FXML
    private void handleConfirmRegister(){
        String enteredCode = mot.getText() + hai.getText() + ba.getText() + bon.getText() + nam.getText() + sau.getText();

        if (enteredCode.equals(verificationCode)) {
            String hashedPassword = BCrypt.hashpw(registerPassword, BCrypt.gensalt(12));

            // Gọi UserService để thêm user với mật khẩu đã băm
            try {
                if(curAct.equals("Register")){
                    userService.addUser(registerUsername,registerEmail,hashedPassword);
                }
                if(curAct.equals("ForgotPassword")){
                    userService.updatePasswordByUsername(curUser,hashedPassword);
                }
                cfregisterStatusLabel.setStyle("-fx-text-fill: green;");
                cfregisterStatusLabel.setText("Thực hiện thành công");
                backToLoginButton.setVisible(true);
                backToLoginButton.setManaged(true);
            } catch (Exception e) {
                registerStatusLabel.setStyle("-fx-text-fill: red;");
                registerStatusLabel.setText("Lỗi khi đăng ký: " + e.getMessage());
            }
        } else {
            cfregisterStatusLabel.setText("Mã xác nhận không đúng, vui lòng thử lại.");
            cfregisterStatusLabel.setStyle("-fx-text-fill: red;"); // Màu đỏ cho lỗi

            // Xóa hết nội dung các ô nhập
            mot.clear();
            hai.clear();
            ba.clear();
            bon.clear();
            nam.clear();
            sau.clear();

            // Đưa con trỏ về ô đầu tiên
            mot.requestFocus();
        }
    }

    //ForgotPassword
    @FXML private TextField newPasswordField;
    @FXML private TextField cfNewPasswordField;
    @FXML private TextField forgotPasswordUserNameField;
    @FXML private Label forgotPasswordStatusLabel;

    @FXML
    private void handleSwitchToConfirmForgotPassword() {
        String userName = forgotPasswordUserNameField.getText();
        String newPassword = newPasswordField.getText();
        String cfNewPassword = cfNewPasswordField.getText();

        if (userName.isBlank() || newPassword.isBlank() || cfNewPassword.isBlank()) {
            forgotPasswordStatusLabel.setStyle("-fx-text-fill: red;");
            forgotPasswordStatusLabel.setText("Vui lòng nhập đủ thông tin");
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
            forgotPasswordStatusLabel.setText("Tên tài khoản không tồn tại hoặc chưa đăng ký email.");
            return;
        }

        String email = emailOpt.get();
        verificationCode = emailService.generateVerificationCode();

        try {
            System.out.println("Sending email to: " + email + " with code: " + verificationCode);
            emailService.sendVerificationCode(email, verificationCode);
            curAct = "ForgotPassword";
            curPass = newPassword;
            curUser = userName;
            System.out.println("Current Pass: " + curPass + " with Name: " + curUser);
            showConfirmBox();  // Đảm bảo chạy trên JavaFX Thread
        } catch (Exception e) {
            //e.printStackTrace();
            forgotPasswordStatusLabel.setStyle("-fx-text-fill: red;");
            forgotPasswordStatusLabel.setText("Lỗi gửi email: " + e.getMessage());
        }
    }



}
