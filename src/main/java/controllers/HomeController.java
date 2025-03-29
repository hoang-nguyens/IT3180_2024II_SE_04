package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import models.User;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

import static app.MainApplication.springContext;

@Controller
public class HomeController {
    @FXML private Button homeButton;
    @FXML private Button residentButton;
    @FXML private Button feeButton;
    @FXML private Button billButton;
    @FXML private Button reportButton;
    @FXML private Button supportButton;

    private List<Button> buttons; // Danh sách các button

    @FXML
    public void initialize() {
        buttons = List.of(homeButton, residentButton, feeButton, billButton, reportButton, supportButton);
        User loggedInUser = LoginController.getLoggedInUser(); // Lấy thông tin user từ LoginController
        if (loggedInUser != null) {
            usernameItem.setText("Tài khoản: " + loggedInUser.getUsername());
        }
    }

    private void handleButtonClick(Button clickedButton) {
        // Đặt tất cả button thành "other_button"
        for (Button button : buttons) {
            button.getStyleClass().removeAll("clicked_button", "other_button");
            button.getStyleClass().add("other_button");
        }

        // Gán class "clicked_button" cho button vừa được nhấn
        clickedButton.getStyleClass().remove("other_button");
        clickedButton.getStyleClass().add("clicked_button");
    }
    @FXML
    private StackPane contentArea;

    // Sự kiện khi click vào "Quản lý Chi Phí"
    @FXML
    private void onManageFees() {
        handleButtonClick(feeButton);
        loadPage("/view/fee-management.fxml");
    }

    @FXML
    private void onManageHome(){
        handleButtonClick(homeButton);
        loadPage("/view/home-management.fxml");
    }

    @FXML
    private void onManageResident(){
        handleButtonClick(residentButton);
        loadPage("/view/home-management.fxml"); //THAY ĐỔI DÒNG NÀY ĐỂ LOAD ĐÚNG TRANG
    }

    @FXML
    private void onManageBill(){
        handleButtonClick(billButton);
        loadPage("/view/home-management.fxml"); //THAY ĐỔI DÒNG NÀY ĐỂ LOAD ĐÚNG TRANG
    }

    @FXML
    private void onManageReport(){
        handleButtonClick(reportButton);
        loadPage("/view/home-management.fxml"); //THAY ĐỔI DÒNG NÀY ĐỂ LOAD ĐÚNG TRANG
    }

    @FXML
    private void onManageSupport(){
        handleButtonClick(supportButton);
        loadPage("/view/home-management.fxml"); //THAY ĐỔI DÒNG NÀY ĐỂ LOAD ĐÚNG TRANG
    }

    // Sự kiện khi click vào "Danh sách Người Dùng"
    @FXML
    private void onUserList() {
    }

    // Sự kiện khi click vào "Cài đặt"
    @FXML
    private void onSettings() {
    }

    // Sự kiện khi click vào "Đăng xuất"
    @FXML
    private void onLogout() {
        showAlert("Bạn đã đăng xuất!");
    }

    // Hiển thị thông báo
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Tải và hiển thị trang mới trong contentArea
    private void loadPage(String fxmlPath) {
        try {
            System.out.println("Đang tải: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(springContext::getBean); // Inject Spring Beans
            Pane newPage = loader.load();
            if (contentArea != null) {
                contentArea.getChildren().setAll(newPage);
            } else {
                System.err.println("contentArea chưa được khởi tạo.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi khi tải trang: " + fxmlPath + "\n" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi không xác định: " + e.getMessage());
        }
    }

    /** Account **/
    @FXML private Button accountButton;
    @FXML private MenuItem usernameItem;
    @FXML private ContextMenu accountMenu;

    @FXML
    private void handleOpenMenu() {
        if (accountMenu != null) {
            accountMenu.show(accountButton, Side.BOTTOM, 0, 10);
        }
    }

    @FXML
    private void handleLogOut() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/login&register/main.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        handleQuit();
        Stage primaryStage = new Stage();
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 850, 650); // Chỉnh lại kích thước cho đúng với FXML
        primaryStage.setTitle("Đăng nhập");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false); // Ngăn người dùng chỉnh sửa kích thước
        primaryStage.show();
    }

    @FXML
    private void handleQuit(){
        Stage homeStage = (Stage) accountButton.getScene().getWindow();
        homeStage.close();
    }


}