package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MainApplication extends Application {

    private static ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        // Khởi động Spring Boot trước khi JavaFX chạy
        springContext = SpringApplication.run(MainApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        primaryStage.setTitle("Đăng nhập");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Đóng Spring Boot khi ứng dụng JavaFX tắt
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
