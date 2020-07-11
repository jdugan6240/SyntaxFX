package syntaxfx;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import java.util.function.Consumer;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.PlainTextChange;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import org.reactfx.Subscription;

import javafx.application.Platform;

import syntaxfx.lexers.JavaLexer;

public class SyntaxArea extends CodeArea {

    protected Lexer lexer = new JavaLexer();
    Subscription highlighter;

    public SyntaxArea() {
        setParagraphGraphicFactory(LineNumberFactory.get(this));

        //Recompute the syntax highlighting repeatedly
        highlighter = this.multiPlainChanges()
            .successionEnds(Duration.ofMillis(20))
            .subscribe(ignore -> setStyleSpans(0, computeHighlighting(getText())));
    }

    public void setLexer(Lexer lexer) {
        this.lexer = lexer;
        //Recalculate the syntax highlighting
        setStyleSpans(0, computeHighlighting(getText()));
    }

    public Lexer getLexer() {
        return lexer;
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        //Initialize the lexer
        lexer.setString(text);
        int lastTokenEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<Collection<String>>();
        //While we continue to get tokens, add their style
        Token tok = lexer.getNextToken();
        while (tok != null) {
            //Grab the correct style class
            String styleClass = "";
            switch (tok.getTokenType()) {
                case Token.OPERATOR: styleClass = "operator"; break;
                case Token.KEYWORD: styleClass = "keyword"; break;
                case Token.KEYWORD2: styleClass = "keyword2"; break;
                case Token.IDENTIFIER: styleClass = "identifier"; break;
                case Token.STRING: styleClass = "string"; break;
                case Token.COMMENT: styleClass = "comment"; break;
                case Token.DOC_COMMENT: styleClass = "doc_comment"; break;
                case Token.DOC_COMMENT_TAG: styleClass = "doc_comment_tag"; break;
                case Token.TYPE: styleClass = "type"; break;
                case Token.NUMBER: styleClass = "number"; break;
                case Token.FUNCTION: styleClass = "function"; break;
                case Token.MEMBER_VAR: styleClass = "member_var"; break;
                default: styleClass = "other";
            }
            //First, make the style of any non-tokenized text before this token plain
            if (tok.getStart() - lastTokenEnd > 0)
              spansBuilder.add(Collections.emptyList(), tok.getStart() - lastTokenEnd);
            //Stylize the text in the current token
            spansBuilder.add(Collections.singleton(styleClass), tok.getLength());
            lastTokenEnd = tok.getEnd();

            tok = lexer.getNextToken();
        }
        //Make the style of any remaining text plain
        if (text.length() - lastTokenEnd > 0)
            spansBuilder.add(Collections.emptyList(), text.length() - lastTokenEnd);
        //If there's no spans in the spansBuilder when create() is called, an exception is thrown.
        //This happens when there's no text in the SyntaxArea.
        //To avoid this, we simply create a zero-length span.
        if (lastTokenEnd == 0)
            spansBuilder.add(Collections.emptyList(), 0);

        return spansBuilder.create();
    }

}

