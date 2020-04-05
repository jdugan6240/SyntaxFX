package syntaxfx;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import syntaxfx.lexers.JavaLexer;

public class SyntaxArea extends CodeArea {

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

    protected Lexer lexer;

    public SyntaxArea() {
        setParagraphGraphicFactory(LineNumberFactory.get(this));

        //Recompute the syntax highlighting 2 ms after user stops editing area
        Subscription cleanupWhenNoLongerNeedIt = this
                .multiPlainChanges()
                .successionEnds(Duration.ofMillis(2))
                .subscribe(ignore -> setStyleSpans(0, computeHighlighting(getText())));

        //Use a JavaLexer by default
        lexer = new JavaLexer();

        //TODO: Move this to the demo.
        replaceText(0, 0, sampleCode);
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
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        //While we continue to get tokens, add their style
        Token tok = null;
        while ((tok = lexer.getNextToken()) != null) {
            //Grab the correct style class
            String styleClass = "";
            switch (tok.type) {
                case Token.OPERATOR:
                    styleClass = "operator";
                    break;
                case Token.KEYWORD:
                    styleClass = "keyword";
                    break;
                case Token.KEYWORD2:
                    styleClass = "keyword2";
                    break;
                case Token.IDENTIFIER:
                    styleClass = "identifier";
                    break;
                case Token.STRING:
                    styleClass = "string";
                    break;
                case Token.COMMENT:
                    styleClass = "comment";
                    break;
                case Token.DOC_COMMENT:
                    styleClass = "doc_comment";
                    break;
                case Token.TYPE:
                    styleClass = "type";
                    break;
                case Token.NUMBER:
                    styleClass = "number";
                    break;
                case Token.OTHER:
                    styleClass = "other";
                    break;
                default:
                    //Should never happen
                    styleClass = "other";
            }
            //First, make the style of any non-tokenized text before this token plain
            spansBuilder.add(Collections.emptyList(), tok.start - lastTokenEnd);
            //Stylize the text in the current token
            spansBuilder.add(Collections.singleton(styleClass), tok.length());
            lastTokenEnd = tok.end;
        }
        //Make the style of any remaining text plain
        spansBuilder.add(Collections.emptyList(), text.length() - lastTokenEnd);
        return spansBuilder.create();
    }
   
}
