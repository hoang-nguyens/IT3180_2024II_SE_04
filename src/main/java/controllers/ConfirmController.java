package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import services.EmailService;
import services.ForgotPasswordService;
import services.UserService;

@Controller
public class ConfirmController {

    @FXML
    private TextField mot;

    @FXML
    private TextField hai;

    @FXML
    private TextField ba;

    @FXML
    private TextField bon;

    @FXML
    private TextField nam;

    @FXML
    private TextField sau;

    @FXML
    private Label cfregisterStatusLabel;

    @FXML
    private Button backToLoginButton;

    @FXML
    private Button backButton;
    private String sendCode;
    private int curAct;
    private User user;

    @Autowired
    private UserController userController; // Inject UserController

    private final UserService userService;
    private final EmailService emailService;
    private final ForgotPasswordService forgotPasswordService;

    @Autowired
    public ConfirmController(UserService userService, EmailService emailService, ForgotPasswordService forgotPasswordService) {
        this.userService = userService;
        this.emailService = emailService;
        this.forgotPasswordService = forgotPasswordService;
    }

    public void setVerificationCode(String verificationCode) {
        this.sendCode = verificationCode;
    }

    public void setCurAct(int act){
        this.curAct = act;
    }

    public void setUser(User user){
        this.user = user;
    }
    @FXML
    private void handleKeyTyped(javafx.scene.input.KeyEvent event) {
        TextField currentTextField = (TextField) event.getSource();
        String text = currentTextField.getText();
        if (text.length() > 1) {
            currentTextField.setText(text.substring(0, 1)); // Chỉ giữ lại ký tự đầu tiên
        }
        if (!text.isEmpty()) {
            if (currentTextField == mot) {
                hai.requestFocus();
            } else if (currentTextField == hai) {
                ba.requestFocus();
            } else if (currentTextField == ba) {
                bon.requestFocus();
            } else if (currentTextField == bon) {
                nam.requestFocus();
            } else if (currentTextField == nam) {
                sau.requestFocus();
            }
        }
    }

    @FXML
    private void handleConfirm() {
        String code = mot.getText() + hai.getText() + ba.getText() + bon.getText() + nam.getText() + sau.getText();
        if (code.equals(sendCode)) {
            // Xác nhận đăng kí
            if(curAct == 1){
                userService.registerUser(user.getUsername(),user.getEmail(),user.getPasswordHash());
            }
            // Xác nhận đổi mật khẩu
            if(curAct == 2){
                userService.updateUser(user);
            }
            cfregisterStatusLabel.setText("Xác nhận thành công!");
            cfregisterStatusLabel.setStyle("-fx-text-fill: green;");
            backToLoginButton.setVisible(true);
            backButton.setVisible(false);
            backButton.setManaged(false);
        } else {
            cfregisterStatusLabel.setText("Mã xác nhận không hợp lệ!");
            cfregisterStatusLabel.setStyle("-fx-text-fill: red;");
            clearAllFields();
        }
    }

    private void clearAllFields() {
        mot.clear();
        hai.clear();
        ba.clear();
        bon.clear();
        nam.clear();
        sau.clear();
    }

    @FXML
    private void handleBack(){
        if(curAct == 1){
            userController.showRegisterBox();
        }
        if(curAct == 2){
            userController.showForgotPasswordBox();
        }
    }
    @FXML
    private void handleLoginClick(ActionEvent event) {
        userController.showLoginBox();
    }
}