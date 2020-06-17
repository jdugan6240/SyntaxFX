package syntaxfx.demo;

import java.io.*;

import java.util.stream.Stream;

import java.net.URISyntaxException;

import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.layout.BorderPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;

import syntaxfx.SyntaxArea;
import syntaxfx.Lexer;

import syntaxfx.lexers.JavaLexer;

public class Demo extends Application {
    SyntaxArea area = null;

    public void start(Stage primaryStage) {
        area = new SyntaxArea();

        MenuBar menuBar = new MenuBar();
        Menu menuLang = new Menu("Language");
        menuBar.getMenus().addAll(menuLang);

        ToggleGroup langGroup = new ToggleGroup();

        RadioMenuItem java = new RadioMenuItem("Java");
        java.setSelected(true);
        java.setToggleGroup(langGroup);
        java.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                setLanguage("JAVA");
            }
        });

        menuLang.getItems().addAll(java);

        Scene scene = new Scene(new BorderPane());
        ((BorderPane)scene.getRoot()).setTop(menuBar);
        ((BorderPane)scene.getRoot()).setCenter(new VirtualizedScrollPane(area));
        scene.getStylesheets().add(Demo.class.getResource("/syntaxfx/default.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("SyntaxFX Demo");
        setLanguage("JAVA");
        primaryStage.show();

    }

    public void setLanguage(String language) {
        Lexer lexer = null;
        //Iterator<String> lines = null;
        Stream<String> lines = null;
        switch (language) {
            case "JAVA":
                lexer = new JavaLexer();
                try {
                    lines = Files.lines(Paths.get(Demo.class.getResource("/syntaxfx/demo/Demo.java").toURI()));
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
                //InputStream stream = getClass().getResourceAsStream("/syntaxfx/demo/Demo.java")
                //lines = Source.fromInputStream(stream).getLines
                break;
        }
        if (lexer != null && lines != null) {
            StringBuilder sb = new StringBuilder();
            /*while (lines.hasNext()) {
                sb.append(lines.next() + "\n");
            }*/
            lines.forEachOrdered(line -> sb.append(line + "\n"));
            if (area != null) {
                area.replaceText(sb.toString());
                area.setLexer(lexer);
            }
        }

    }
}

