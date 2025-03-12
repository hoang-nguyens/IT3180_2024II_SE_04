package app;

import config.AppConfig;
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
@Import({AppConfig.class})
//@SpringBootApplication
public class MainApplication extends Application {

    private static ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        // Start Spring Boot
        springContext = SpringApplication.run(MainApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean); // Inject Spring Beans

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("Đăng nhập");
        primaryStage.setScene(scene);
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
