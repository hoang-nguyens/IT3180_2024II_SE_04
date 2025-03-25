package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Controller;

import java.io.IOException;

import static app.MainApplication.springContext;

@Controller
public class HomeController {

    @FXML
    private StackPane contentArea;

    // Sự kiện khi click vào "Quản lý Chi Phí"
    @FXML
    private void onManageFees() {
        loadPage("/view/fee-management.fxml");
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

            // Kiểm tra contentArea trước khi thay đổi
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
}
