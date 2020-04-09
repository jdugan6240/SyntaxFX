package syntaxfx

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.fxmisc.flowless.VirtualizedScrollPane

import Demo._

object Demo {

    private val sampleCode: String =
        "package com.example;" + "\n" +
        "" + "\n" +     
        "import java.util.*;" + "\n" +
        "" + "\n" +
        "public class Foo extends Bar implements Baz {" + "\n" +
        "" + "\n" +
        "    /*" + "\n" +
        "     * Multi-line comment" + "\n" +
        "     */" + "\n" +
        "    public static void main(String[] args) {" + "\n" +
        "        //Single-line comment" + "\n" +
        "        for (String arg : args) {" + "\n" +
        "            if (arg.length() != 0)" + "\n" +
        "                System.out.println(arg);" + "\n" +
        "            else" + "\n" +
        "                System.err.println(\"Warning: empty string as argument\");" + "\n" +
        "        }" + "\n" +
        "    }" + "\n" +
        "" + "\n" +
        "}"

    def main(args: Array[String]): Unit = {
        Application.launch(classOf[Demo], args: _*)
    }

}

class Demo extends Application {

    override def start(primaryStage: Stage): Unit = {
        val area: SyntaxArea = new SyntaxArea()
        area.replaceText(sampleCode)
        val scene: Scene = new Scene(new StackPane(new VirtualizedScrollPane(area)), 600, 400)
        scene.getStylesheets.add(classOf[Demo].getResource("default.css").toExternalForm())
        primaryStage.setScene(scene)
        primaryStage.setTitle("SyntaxFX Demo")
        primaryStage.show()
    }

}
