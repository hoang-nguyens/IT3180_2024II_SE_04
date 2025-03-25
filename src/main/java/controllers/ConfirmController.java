package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import services.EmailService;
import services.ForgotPasswordService;
import services.UserService;

@Controller
public class ConfirmController {

    private static final int INITIAL_TIME = 300;

    private Timeline countdownTimeline;
    private int remainingTimeInSeconds = INITIAL_TIME;
    private int state = 1;

    @FXML private TextField mot;
    @FXML private TextField hai;
    @FXML private TextField ba;
    @FXML private TextField bon;
    @FXML private TextField nam;
    @FXML private TextField sau;
    @FXML private Label cfregisterStatusLabel;
    @FXML private Label countdownLabel;
    @FXML private Button backToLoginButton;
    @FXML private Button backButton;

    private String sendCode;
    private int curAct;
    private User user;

    @Autowired private UserController userController;
    @Autowired @Lazy private ForgotPasswordController forgotPasswordController;
    @Autowired @Lazy private RegisterController registerController;

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

    public void setCurAct(int act) {
        this.curAct = act;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    public void initialize() {
        startCountdown();
    }

    private void startCountdown() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
        updateCountdownLabel();
        countdownTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    remainingTimeInSeconds--;
                    updateCountdownLabel();
                    if (remainingTimeInSeconds <= 0) {
                        countdownTimeline.stop();
                        countdownLabel.setStyle("-fx-text-fill: red;");
                        countdownLabel.setText("Mã xác nhận đã hết hiệu lực!");
                        state = 0;
                    }
                })
        );
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
    }

    private void updateCountdownLabel() {
        int minutes = remainingTimeInSeconds / 60;
        int seconds = remainingTimeInSeconds % 60;
        countdownLabel.setStyle("-fx-text-fill: green;");
        countdownLabel.setText(String.format("Mã còn hiệu lực trong: %02d:%02d", minutes, seconds));
    }

    public void resetCountdown() {
        remainingTimeInSeconds = INITIAL_TIME;
        startCountdown();
    }

    public void stopCountdown() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
    }

    @FXML
    private void handleReSendCode() {
        if (state == 0) {
            String email = user.getEmail();
            sendCode = emailService.generateVerificationCode();
            state++;
            try {
                emailService.sendVerificationCode(email, sendCode);
                resetCountdown();
                cfregisterStatusLabel.setText("");
            } catch (Exception e) {
                cfregisterStatusLabel.setStyle("-fx-text-fill: red;");
                cfregisterStatusLabel.setText("Lỗi gửi email: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleKeyTyped(KeyEvent event) {
        TextField currentTextField = (TextField) event.getSource();
        String text = currentTextField.getText();
        if (text.length() > 1) {
            currentTextField.setText(text.substring(0, 1));
        }
        if (!text.isEmpty()) {
            switch (currentTextField.getId()) {
                case "mot": hai.requestFocus(); break;
                case "hai": ba.requestFocus(); break;
                case "ba": bon.requestFocus(); break;
                case "bon": nam.requestFocus(); break;
                case "nam": sau.requestFocus(); break;
                default: break;
            }
        }
    }

    @FXML
    private void handleConfirm() {
        String code = mot.getText() + hai.getText() + ba.getText() + bon.getText() + nam.getText() + sau.getText();
        if (remainingTimeInSeconds <= 0) return;
        if (code.equals(sendCode)) {
            if (curAct == 1) {
                userService.registerUser(user.getUsername(), user.getEmail(), user.getPasswordHash());
                registerController.clearRegister();
            }
            if (curAct == 2) {
                userService.updateUser(user);
                forgotPasswordController.clearForgotPassword();
            }
            cfregisterStatusLabel.setText("Xác nhận thành công!");
            cfregisterStatusLabel.setStyle("-fx-text-fill: green;");
            backToLoginButton.setVisible(true);
            backButton.setVisible(false);
            backButton.setManaged(false);
            stopCountdown();
            countdownLabel.setText("");
        } else {
            cfregisterStatusLabel.setText("Mã xác nhận không hợp lệ!");
            cfregisterStatusLabel.setStyle("-fx-text-fill: red;");
            clearAllFields();
        }
    }

    private void clearAllFields() {
        mot.clear(); hai.clear(); ba.clear(); bon.clear(); nam.clear(); sau.clear();
    }

    private void clearCf() {
        clearAllFields();
        backButton.setVisible(true);
        backButton.setManaged(true);
        state = 1;
        backToLoginButton.setVisible(false);
        backToLoginButton.setManaged(false);
        cfregisterStatusLabel.setText("");
    }

    @FXML
    private void handleBack() {
        if (curAct == 1) {
            userController.showRegisterBox();
        } else if (curAct == 2) {
            userController.showForgotPasswordBox();
        }
    }

    @FXML
    private void handleLoginClick(ActionEvent event) {
        userController.showLoginBox();
        clearCf();
    }
}
