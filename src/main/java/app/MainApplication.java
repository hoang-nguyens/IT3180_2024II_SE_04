package app;

import config.AppConfig;
import config.SecurityConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

//@SpringBootApplication(scanBasePackages = {"app", "repositories", "controllers", "services", "repositories"})
@SpringBootApplication
@Import({AppConfig.class, SecurityConfig.class})
//@SpringBootApplication
public class MainApplication extends Application {

    public static ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        // Start Spring Boot
        springContext = SpringApplication.run(MainApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/login&register/main.fxml"));
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/HomePage.fxml"));
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/fee-management.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean); // Inject Spring Beans

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 850, 650); // Chỉnh lại kích thước cho đúng với FXML

        primaryStage.setTitle("Đăng nhập");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false); // Ngăn người dùng chỉnh sửa kích thước
        primaryStage.show();
    }



    @Override
    public void stop() {
        // Close Spring Boot when JavaFX stops
        if (springContext != null) {
            springContext.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
