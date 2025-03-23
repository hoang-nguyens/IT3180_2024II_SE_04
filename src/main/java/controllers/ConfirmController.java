package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import services.EmailService;
import services.ForgotPasswordService;
import services.UserService;

@Controller
public class ConfirmController { // Không kế thừa từ UserController nữa

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
    private Button confirmregisterButton;

    @FXML
    private Label cfregisterStatusLabel;

    @FXML
    private Button backToLoginButton;

    @FXML
    private Hyperlink loginLink2;

    private String sendCode;

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

    // Phương thức xử lý sự kiện khi người dùng nhập mã xác nhận
    @FXML
    private void handleKeyTyped(javafx.scene.input.KeyEvent event) {
        TextField currentTextField = (TextField) event.getSource();

        // Giới hạn mỗi ô chỉ chứa 1 ký tự
        String text = currentTextField.getText();
        if (text.length() > 1) {
            currentTextField.setText(text.substring(0, 1)); // Chỉ giữ lại ký tự đầu tiên
        }

        // Tự động chuyển sang ô tiếp theo nếu có 1 ký tự
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


    // Phương thức xử lý sự kiện khi người dùng nhấn nút "Xác nhận"
    @FXML
    private void handleConfirm() {
        // Lấy mã xác nhận từ các ô nhập liệu
        String code = mot.getText() + hai.getText() + ba.getText() + bon.getText() + nam.getText() + sau.getText();

        // Xử lý logic xác nhận mã
        if (code.equals(sendCode)) {
            cfregisterStatusLabel.setText("Xác nhận thành công!");
            cfregisterStatusLabel.setStyle("-fx-text-fill: green;");
            backToLoginButton.setVisible(true);
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
    private void handleLoginClick(ActionEvent event) {
        // Gọi phương thức showLoginBox() từ UserController
        userController.showLoginBox();
    }
}