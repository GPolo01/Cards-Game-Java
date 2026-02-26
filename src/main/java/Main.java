import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Create Principal Screen - our OBserver
        MainScreen screen = new MainScreen();
        
        primaryStage.setTitle("Collection of Card Games in Java");
        screen.displayMainMenu(primaryStage);
    }

    public static void main(String[] args) {
        // JavaFX method to iniciate
        launch(args);
    }
}