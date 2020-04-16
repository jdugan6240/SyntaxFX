package syntaxfx

import java.time.Duration
import java.util.Collection
import java.util.Collections
import java.util.List

import java.util.function.Consumer

import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.model.PlainTextChange
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder

import org.reactfx.Subscription

import javafx.application.Platform

import syntaxfx.lexers.JavaLexer

class SyntaxArea extends CodeArea {

    protected var lexer: Lexer = new JavaLexer()

    setParagraphGraphicFactory(LineNumberFactory.get(this))

    //Recompute the syntax highlighting repeatedly
    //new StyleDaemon(this, 20).run()
    var cleanup : Subscription = this.multiPlainChanges()
      .successionEnds(Duration.ofMillis(20))
      .subscribe(new Consumer[List[PlainTextChange]] {
          override def accept(value : List[PlainTextChange]): Unit = {
              setStyleSpans(0, computeHighlighting(getText))
          }
      })

    def setLexer(lexer: Lexer): Unit = {
        this.lexer = lexer
        //Recalculate the syntax highlighting
        setStyleSpans(0, computeHighlighting(getText))
    }

    def getLexer(): Lexer = lexer

    private def computeHighlighting(text: String): StyleSpans[Collection[String]] = {
        //Initialize the lexer
        lexer.setString(text)
        var lastTokenEnd: Int = 0
        val spansBuilder: StyleSpansBuilder[Collection[String]] = new StyleSpansBuilder[Collection[String]]()
        //While we continue to get tokens, add their style
        var tok: Token = lexer.getNextToken
        while (tok != null) {
            //Grab the correct style class
            var styleClass: String = ""
            tok.tokenType match {
                case Token.OPERATOR => styleClass = "operator"
                case Token.KEYWORD => styleClass = "keyword"
                case Token.KEYWORD2 => styleClass = "keyword2"
                case Token.IDENTIFIER => styleClass = "identifier"
                case Token.STRING => styleClass = "string"
                case Token.COMMENT => styleClass = "comment"
                case Token.DOC_COMMENT => styleClass = "doc_comment"
                case Token.DOC_COMMENT_TAG => styleClass = "doc_comment_tag"
                case Token.TYPE => styleClass = "type"
                case Token.NUMBER => styleClass = "number"
                case Token.OTHER => styleClass = "other"
                case _ => styleClass = "other" //Should never happen

            }
            //First, make the style of any non-tokenized text before this token plain
            if (tok.start - lastTokenEnd > 0)
              spansBuilder.add(Collections.emptyList(), tok.start - lastTokenEnd)
            //Stylize the text in the current token
            spansBuilder.add(Collections.singleton(styleClass), tok.length)
            lastTokenEnd = tok.end

            tok = lexer.getNextToken
        }
        //Make the style of any remaining text plain
        //if (text.length - lastTokenEnd > 0)
        //    spansBuilder.add(Collections.emptyList(), text.length - lastTokenEnd)
        spansBuilder.create()
    }

}


