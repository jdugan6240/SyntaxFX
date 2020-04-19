package syntaxfx.demo

import java.io.InputStream

import scala.io._

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.text.Text
import javafx.scene.layout.BorderPane
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.stage.Stage
import org.fxmisc.flowless.VirtualizedScrollPane

import syntaxfx.SyntaxArea
import syntaxfx.Lexer

import syntaxfx.lexers.JavaLexer

import Demo._

object Demo {

    def main(args: Array[String]): Unit = {
        Application.launch(classOf[Demo], args: _*)
    }
}

class Demo extends Application {
    var area : SyntaxArea = null

    override def start(primaryStage: Stage): Unit = {
        area = new SyntaxArea()

        val menuBar : MenuBar = new MenuBar()
        val menuLang : Menu = new Menu("Language")
        menuBar.getMenus().addAll(menuLang)

        val langGroup : ToggleGroup = new ToggleGroup()

        val java : RadioMenuItem = new RadioMenuItem("Java")
        java.setSelected(true)
        java.setToggleGroup(langGroup)
        java.setOnAction(new EventHandler[ActionEvent]() {
            override def handle(e : ActionEvent) {
                setLanguage("JAVA")
            }
        });

        menuLang.getItems().addAll(java)

        val scene: Scene = new Scene(new BorderPane())
        scene.getRoot().asInstanceOf[BorderPane].setTop(menuBar)
        scene.getRoot().asInstanceOf[BorderPane].setCenter(new VirtualizedScrollPane(area))
        scene.getStylesheets.add(classOf[Demo].getResource("/syntaxfx/default.css").toExternalForm())
        primaryStage.setScene(scene)
        primaryStage.setTitle("SyntaxFX Demo")
        setLanguage("JAVA")
        primaryStage.show()
    }

    def setLanguage(language : String) : Unit = {
        var lexer : Lexer = null;
        var lines : Iterator[String] = null
        language match {
            case "JAVA" =>
                lexer = new JavaLexer
                val stream : InputStream = getClass.getResourceAsStream("/syntaxfx/demo/Demo.java")
                lines = Source.fromInputStream(stream).getLines
        }
        if (lexer != null && lines != null) {
            val sb : StringBuilder = new StringBuilder()
            while (lines.hasNext) {
                sb.append(lines.next + "\n")
            }
            if (area != null) {
                area.replaceText(sb.toString)
                area.setLexer(lexer)
            }
        }
    }

}
