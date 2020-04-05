package syntaxfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.fxmisc.flowless.VirtualizedScrollPane;

public class Demo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new StackPane(new VirtualizedScrollPane<>(new SyntaxArea())), 600, 400);
        scene.getStylesheets().add(Demo.class.getResource("default.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("SyntaxFX Demo");
        primaryStage.show();
    }

}
