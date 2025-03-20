package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

public class HomeController {
    @FXML
    private StackPane contentArea;

    // Sự kiện khi click vào "Quản lý Chi Phí"
    @FXML
    private void onManageFees() {
        showAlert("Quản lý Chi Phí");
    }

    // Sự kiện khi click vào "Danh sách Người Dùng"
    @FXML
    private void onUserList() {
        showAlert("Danh sách Người Dùng");
    }

    // Sự kiện khi click vào "Cài đặt"
    @FXML
    private void onSettings() {
        showAlert("Cài đặt");
    }

    // Sự kiện khi click vào "Đăng xuất"
    @FXML
    private void onLogout() {
        showAlert("Bạn đã đăng xuất!");
    }

    // Hiển thị thông báo tạm thời
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
