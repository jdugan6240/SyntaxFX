package syntaxfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.fxmisc.flowless.VirtualizedScrollPane;

public class Demo extends Application {

    private static final String sampleCode = String.join("\n", new String[] {
            "package com.example;",
            "",
            "import java.util.*;",
            "",
            "public class Foo extends Bar implements Baz {",
            "",
            "    /*",
            "     * multi-line comment",
            "     */",
            "    public static void main(String[] args) {",
            "        // single-line comment",
            "        for(String arg: args) {",
            "            if(arg.length() != 0)",
            "                System.out.println(arg);",
            "            else",
            "                System.err.println(\"Warning: empty string as argument\");",
            "        }",
            "    }",
            "",
            "}"
    });


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        SyntaxArea area = new SyntaxArea();
        area.replaceText(sampleCode);
        Scene scene = new Scene(new StackPane(new VirtualizedScrollPane<>(area)), 600, 400);
        scene.getStylesheets().add(Demo.class.getResource("default.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("SyntaxFX Demo");
        primaryStage.show();
    }

}
