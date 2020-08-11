package syntaxfx.lexers;

import syntaxfx.Lexer;
import syntaxfx.Token;

import java.util.Stack;

/**
 * The JavaLexer class represents a lexer for the Java programming language.
 * 
 * @option functionality = all- setString+ getMatchStart+ getMatchEnd+ lexicalState+
 * @option visibility = all- stringMethods+ scanMethods+
 * @option internal = setString+
 * @option logo = disabled
 * @option statistics = disabled
 * @option headings = disabled
 * @option javadoc = all-
 * 
 * @macro DecimalInteger = 0 | [1-9][0-9]* 
 * @macro HexInteger     = 0 [xX] [0-9a-fA-F]* 
 * @macro OctalInteger   = 0 [0-7]+
 * @macro BinaryInteger  = 0 [bB] [01]* 
 * @macro FloatNumber    = (([0-9]+\.[0-9]*) | (\.?[0-9]+)) ([eE][+-]?[0-9]+)?
 */
public class JavaLexer extends Lexer {
    Stack<Integer> stateStack = new Stack<>();
    int stateStart = 0;

    public void setString(String string) {
        setStringInternal(string);
        stateStack.clear();
        stateStack.push(LEXICAL_STATE_INITIAL);
    }

    private void enterLexicalState(int state) {
        setLexicalState(state);
        stateStack.push(state);
    }

    private void leaveLexicalState() {
        stateStack.pop();
        setLexicalState(stateStack.peek());
    }

    //%%LEX-CONDITION-START%%COMMENT%%
    
    /** @expr \*\/ */
    Token endMultilineComment() {
        leaveLexicalState();
        return new Token(Token.COMMENT, stateStart, matchEnd);
    }

    /** @expr [^] */
    void commentText() {}

    //%%LEX-CONDITION-END%%
    
    //%%LEX-CONDITION-START%%DOCTAG%%
    
    /** @expr [^a-zA-Z] */
    Token endTag() {
        leaveLexicalState();
        int temp = stateStart;
        stateStart = matchStart;
        return new Token(Token.DOC_COMMENT_TAG, temp, matchStart);
    }

    /** @expr [^] */
    void doctagText() {}
    
    //%%LEX-CONDITION-END%%

    //%%LEX-CONDITION-START%%DOC%%
    
    /** @expr \*\/ */
    Token endDoc() {
        leaveLexicalState();
        return new Token(Token.DOC_COMMENT, stateStart, matchEnd);
    }

    /** @expr \@ */
    Token startTag() {
        enterLexicalState(LEXICAL_STATE_DOCTAG);
        int temp = stateStart;
        stateStart = matchStart;
        return new Token(Token.DOC_COMMENT, temp, matchStart);
    }

    /** @expr [^] */
    void docText() {}

    //%%LEX-CONDITION-END%%
   
    //%%LEX-CONDITION-START%%INITIAL%%

    /** @expr \/\/[^\r\n]* */
    Token createLineComment() { return new Token(Token.COMMENT, matchStart, matchEnd); }

    /** @expr \/\*\* */
    void startDoc() {
        stateStart = matchStart;
        enterLexicalState(LEXICAL_STATE_DOC);
    }

    /** @expr \/\* */
    void startMultilineComment() {
        stateStart = matchStart;
        enterLexicalState(LEXICAL_STATE_COMMENT);
    }

    /** @expr \"(\\[^\r\n]|[^\\\"])*\" */
    Token createString() { return new Token(Token.STRING, matchStart, matchEnd); }
    
     /** @expr \'(\\[^\r\n]|[^\\\'])*\' */
    Token createChar() { return new Token(Token.STRING, matchStart, matchEnd); }
    
    /** @expr package|void|public|protected|private|false|static|abstract|native|strictfp|synchronized|transient|volatile|const|extends|implements|throws|class */
    Token createKeyword() { return new Token(Token.KEYWORD, matchStart, matchEnd); }

    /** @expr import|if|else|switch|case|default|for|while|do|continue|break|return|goto|try|catch|finally|throw|assert|true|false|null|this*/
    Token createKeyword2() { return new Token(Token.KEYWORD2, matchStart, matchEnd); }

    /** @expr \(|\)|\{|\}|\[|\]|\;|\.|\=|\>|\<|\!|\~|\?|\:|\||\&|\^|\%|\+|\-|\*|\/ */
    Token createOperator() { return new Token(Token.OPERATOR, matchStart, matchEnd); }

    /** @expr {DecimalInteger}[Ll]? | {HexInteger}[Ll]? | {OctalInteger}[Ll]? | {BinaryInteger}[Ll]? */
    Token createInteger() { return new Token(Token.NUMBER, matchStart, matchEnd); }

    /** @expr {FloatNumber}[fF]? | {FloatNumber}[dD] */
    Token createFloat() { return new Token(Token.NUMBER, matchStart, matchEnd); }

    /** @expr \@\p{JavaIdentifierPart}* */
    Token createAnnotation() { return new Token(Token.DOC_COMMENT_TAG, matchStart, matchEnd); }

    /** @expr \p{JavaIdentifierStart}\p{JavaIdentifierPart}* */
    Token createIdentifer() {
        //If the next character that isn't whitespace is a parentheses, it's a function
        for (int i = matchEnd; i < this.string.length(); ++i) {
            if (Character.isWhitespace(this.string.charAt(i)))
                continue;
            else if (this.string.charAt(i) == '(')
                return new Token(Token.FUNCTION, matchStart, matchEnd);
            else
                break;
        }
        return new Token(Token.IDENTIFIER, matchStart, matchEnd);
    }
    
    /** @expr [^] */
    Token other() { return new Token(Token.OTHER, matchStart, matchEnd); }

    //%%LEX-CONDITION-END%%

    //%%LEX-MAIN-START%%
    
    private static final byte[] CHARACTER_MAP = createCharacterMap(
    "\33\t\4\1\1\1\4\2\1\1\33\16\4\5\23\1\3\1\4\1\37\1\32\1\30\1" +
    "\2\1\b\1\t\1\5\1\35\1\4\1\36\1\17\1\6\1\101\1\102\1\100\6\73\2" +
    "\26\1\16\1\22\1\20\1\21\1\25\1\7\1\62\1\67\1\62\1\72\1\70\1" +
    "\71\1\34\5\60\1\34\13\61\1\34\2\f\1\0\1\r\1\31\1\37\1\4\1\65\1" +
    "\74\1\66\1\77\1\75\1\76\1\"\1\54\1\45\1\34\1\41\1\63\1\56\1" +
    "\52\1\44\1\40\1\34\1\'\1\51\1\50\1\46\1\43\1\57\1\64\1\53\1" +
    "\55\1\n\1\27\1\13\1\24\1\33\41\4\2\37\4\4\4\37\1\4\2\33\1\4\7" +
    "\37\1\4\4\37\1\4\5\37\27\4\1\37\37\4\1\37\u01ca\4\4\37\f\4\16" +
    "\37\5\4\7\37\1\4\1\37\1\4\21\33\160\37\5\4\1\37\2\4\2\37\4\4\b" +
    "\37\1\4\1\37\3\4\1\37\1\4\1\37\24\4\1\37\123\4\1\37\213\4\1" +
    "\33\5\4\2\37\236\4\t\37\46\4\2\37\1\4\7\37\'\4\7\37\1\4\1\33\55" +
    "\4\1\33\1\4\1\33\2\4\1\33\2\4\1\33\1\4\b\37\33\4\5\37\3\4\r" +
    "\33\5\4\6\37\1\4\4\33\13\4\5\37\53\33\37\4\4\37\2\33\1\37\143" +
    "\4\1\37\1\33\b\4\1\33\6\37\2\33\2\4\1\33\4\37\2\33\n\37\3\4\2" +
    "\37\1\4\17\33\1\37\1\33\1\37\36\33\33\4\2\37\131\33\13\37\1" +
    "\4\16\33\n\37\41\33\t\37\2\4\4\37\1\4\5\37\26\33\4\37\1\33\t" +
    "\37\1\33\3\37\1\33\5\4\22\37\31\33\3\4\104\37\1\4\1\37\13\4\67" +
    "\33\33\4\1\33\4\37\66\33\3\37\1\33\22\37\1\33\7\37\n\33\2\4\2" +
    "\33\n\4\1\37\7\4\1\37\7\4\1\33\3\4\1\37\b\4\2\37\2\4\2\37\26" +
    "\4\1\37\7\4\1\37\1\4\3\37\4\4\2\33\1\37\1\33\7\4\2\33\2\4\2" +
    "\33\3\37\1\4\b\33\1\4\4\37\2\4\1\37\3\33\2\4\2\33\n\37\4\4\7" +
    "\37\1\4\5\33\3\4\1\37\6\4\4\37\2\4\2\37\26\4\1\37\7\4\1\37\2" +
    "\4\1\37\2\4\1\37\2\4\2\33\1\4\1\33\5\4\4\33\2\4\2\33\3\4\3\33\1" +
    "\4\7\37\4\4\1\37\1\4\7\33\f\37\3\33\1\4\13\33\3\4\1\37\t\4\1" +
    "\37\3\4\1\37\26\4\1\37\7\4\1\37\2\4\1\37\5\4\2\33\1\37\1\33\b" +
    "\4\1\33\3\4\1\33\3\4\2\37\1\4\17\37\2\33\2\4\2\33\n\4\1\37\1" +
    "\4\17\33\3\4\1\37\b\4\2\37\2\4\2\37\26\4\1\37\7\4\1\37\2\4\1" +
    "\37\5\4\2\33\1\37\1\33\7\4\2\33\2\4\2\33\3\4\b\33\2\4\4\37\2" +
    "\4\1\37\3\33\2\4\2\33\n\4\1\37\1\4\20\33\1\37\1\4\1\37\6\4\3" +
    "\37\3\4\1\37\4\4\3\37\2\4\1\37\1\4\1\37\2\4\3\37\2\4\3\37\3" +
    "\4\3\37\f\4\4\33\5\4\3\33\3\4\1\33\4\4\2\37\1\4\6\33\1\4\16" +
    "\33\n\4\t\37\1\4\7\33\3\4\1\37\b\4\1\37\3\4\1\37\27\4\1\37\n" +
    "\4\1\37\5\4\3\37\1\33\7\4\1\33\3\4\1\33\4\4\7\33\2\4\1\37\2" +
    "\4\6\37\2\33\2\4\2\33\n\4\22\33\2\4\1\37\b\4\1\37\3\4\1\37\27" +
    "\4\1\37\n\4\1\37\5\4\2\33\1\37\1\33\7\4\1\33\3\4\1\33\4\4\7" +
    "\33\2\4\7\37\1\4\1\37\2\33\2\4\2\33\n\4\1\37\2\4\17\33\2\4\1" +
    "\37\b\4\1\37\3\4\1\37\51\4\2\37\1\33\7\4\1\33\3\4\1\33\4\37\1" +
    "\4\b\33\1\4\b\37\2\33\2\4\2\33\n\4\n\37\6\4\2\33\2\4\1\37\22" +
    "\4\3\37\30\4\1\37\t\4\1\37\1\4\2\37\7\4\3\33\1\4\4\33\6\4\1" +
    "\33\1\4\1\33\b\4\22\33\2\4\r\37\60\33\1\37\2\33\7\4\4\37\b\33\b" +
    "\4\1\33\n\4\'\37\2\4\1\37\1\4\2\37\2\4\1\37\1\4\2\37\1\4\6\37\4" +
    "\4\1\37\7\4\1\37\3\4\1\37\1\4\1\37\1\4\2\37\2\4\1\37\4\33\1" +
    "\37\2\33\6\4\1\33\2\37\1\4\2\37\5\4\1\37\1\4\1\33\6\4\2\33\n" +
    "\4\2\37\4\4\40\37\1\4\27\33\2\4\6\33\n\4\13\33\1\4\1\33\1\4\1" +
    "\33\1\4\4\33\2\37\b\4\1\37\44\4\4\33\24\4\1\33\2\37\5\33\13" +
    "\4\1\33\44\4\t\33\1\4\71\37\53\33\24\37\1\33\n\4\6\37\6\33\4" +
    "\37\4\33\3\37\1\33\3\37\2\33\7\37\3\33\4\37\r\33\f\37\1\33\17" +
    "\4\2\37\46\4\1\37\1\4\5\37\1\4\2\37\53\4\1\37\u014d\4\1\37\4" +
    "\4\2\37\7\4\1\37\1\4\1\37\4\4\2\37\51\4\1\37\4\4\2\37\41\4\1" +
    "\37\4\4\2\37\7\4\1\37\1\4\1\37\4\4\2\37\17\4\1\37\71\4\1\37\4" +
    "\4\2\37\103\4\2\33\3\4\40\37\20\4\20\37\125\4\f\37\u026c\4\2" +
    "\37\21\4\1\37\32\4\5\37\113\4\3\37\3\4\17\37\r\4\1\37\4\33\3" +
    "\4\13\37\22\33\3\4\13\37\22\33\2\4\f\37\r\4\1\37\3\4\1\33\2" +
    "\4\f\37\64\33\40\4\3\37\1\4\3\37\2\33\1\4\2\33\n\4\41\33\3\4\2" +
    "\33\n\4\6\37\130\4\b\37\51\33\1\37\1\4\5\37\106\4\n\37\35\4\3" +
    "\33\f\4\4\33\f\4\n\33\n\37\36\4\2\37\5\4\13\37\54\4\4\33\21" +
    "\37\7\33\2\4\6\33\n\4\46\37\27\33\5\4\4\37\65\33\n\4\1\33\35" +
    "\4\2\33\13\4\6\33\n\4\r\37\1\4\130\33\5\37\57\33\21\37\7\4\4" +
    "\33\n\4\21\33\t\4\f\33\3\37\36\33\r\37\2\33\n\37\54\33\16\4\f" +
    "\37\44\33\24\4\b\33\n\4\3\37\3\33\n\37\44\4\122\33\3\4\1\33\25" +
    "\37\4\33\1\37\4\33\3\37\2\4\t\37\300\33\'\4\25\33\4\37\u0116" +
    "\4\2\37\6\4\2\37\46\4\2\37\6\4\2\37\b\4\1\37\1\4\1\37\1\4\1" +
    "\37\1\4\1\37\37\4\2\37\65\4\1\37\7\4\1\37\1\4\3\37\3\4\1\37\7" +
    "\4\3\37\4\4\2\37\6\4\4\37\r\4\5\37\3\4\1\37\7\4\16\33\5\4\32" +
    "\33\5\4\20\37\2\4\23\37\1\4\13\33\5\4\5\33\6\4\1\37\1\4\r\37\1" +
    "\4\20\37\r\4\3\37\33\4\25\33\r\4\4\33\1\4\3\33\f\4\21\37\1\4\4" +
    "\37\1\4\2\37\n\4\1\37\1\4\3\37\5\4\6\37\1\4\1\37\1\4\1\37\1" +
    "\4\1\37\4\4\1\37\13\4\2\37\4\4\5\37\5\4\4\37\1\4\21\37\51\4\u0a77" +
    "\37\57\4\1\37\57\4\1\37\205\4\6\37\4\33\3\37\2\4\f\37\46\4\1" +
    "\37\1\4\5\37\1\4\2\37\70\4\7\37\1\4\17\33\1\37\27\4\t\37\7\4\1" +
    "\37\7\4\1\37\7\4\1\37\7\4\1\37\7\4\1\37\7\4\1\37\7\4\1\37\7" +
    "\4\1\33\40\4\57\37\1\4\u01d5\37\3\4\31\37\t\33\6\4\1\37\5\4\2" +
    "\37\5\4\4\37\126\4\2\33\2\4\2\37\3\4\1\37\132\4\1\37\4\4\5\37\51" +
    "\4\3\37\136\4\21\37\33\4\65\37\20\4\u0200\37\u19b6\4\112\37\u51cd" +
    "\4\63\37\u048d\4\103\37\56\4\2\37\u010d\4\3\37\20\33\n\37\2" +
    "\4\24\37\57\33\1\4\4\33\n\4\1\37\31\4\7\33\1\37\120\33\2\4\45" +
    "\37\t\4\2\37\147\4\2\37\4\4\1\37\4\4\f\37\13\4\115\37\n\33\1" +
    "\37\3\33\1\37\4\33\1\37\27\33\5\4\20\37\1\4\7\37\64\4\f\33\2" +
    "\37\62\33\21\4\13\33\n\4\6\33\22\37\6\4\3\37\1\4\4\33\n\37\34" +
    "\33\b\4\2\37\27\33\r\4\f\37\35\4\3\33\4\37\57\33\16\4\16\37\1" +
    "\33\n\4\46\37\51\33\16\4\t\37\3\33\1\37\b\33\2\4\2\33\n\4\6" +
    "\37\27\4\3\37\1\33\1\4\4\37\60\33\1\37\1\33\3\37\2\33\2\37\5" +
    "\33\2\37\1\33\1\37\1\4\30\37\3\4\2\37\13\33\5\4\2\37\3\33\2" +
    "\4\n\37\6\4\2\37\6\4\2\37\6\4\t\37\7\4\1\37\7\4\221\37\43\33\b" +
    "\4\1\33\2\4\2\33\n\4\6\37\u2ba4\4\f\37\27\4\4\37\61\4\u2104" +
    "\37\u016e\4\2\37\152\4\46\37\7\4\f\37\5\4\5\37\1\33\1\37\n\4\1" +
    "\37\r\4\1\37\5\4\1\37\1\4\1\37\2\4\1\37\2\4\1\37\154\4\41\37\u016b" +
    "\4\22\37\100\4\2\37\66\4\50\37\r\4\3\33\20\4\20\33\7\4\f\37\2" +
    "\4\30\37\3\4\31\37\1\4\6\37\5\4\1\37\207\4\2\33\1\4\4\37\1\4\13" +
    "\33\n\4\7\37\32\4\4\37\1\4\1\37\32\4\13\37\131\4\3\37\6\4\2" +
    "\37\6\4\2\37\6\4\2\37\3\4\3\37\2\4\3\37\2\4\22\33\3\4\4");
    
    private static final short[][] TRANSITION_TABLE = createTransitionTable(
    "\3\5\4\1\3\75\0\103\0\103\0\6\2\1\0\74\6\34\7\1\6\3\7\33\6\1\7\4\6\3" +
    "\0\103\0\103\13\5\f\1\13\1\n\1\13\73\0\103\0\103\0\103\0\6\t\1\0\74\254\1" +
    "\0\1\254\101\16\1\0\1\16\101\0\103\0\5\17\1\0\75\0\103\0\103\0\33\31\2" +
    "\0\2\31\44\0\33\31\2\0\2\31\44\0\103\0\103\0\103\0\33\30\2\0\2\30\44" +
    "\0\33\31\2\0\2\31\44\0\103\32\2\250\1\253\1\32\1\25\1\255\1\30\1\25\7" +
    "\34\1\25\13\32\1\31\1\25\2\31\1\161\1\31\1\135\1\163\1\31\1\'\1\31\1" +
    "\114\1\64\1\100\1\67\1\31\4\53\1\31\5\127\1\55\1\31\4\37\1\141\1\56\1" +
    "\107\1\123\1\37\1\46\1\37\1\0\73\40\1\0\4\40\3\0\71\27\2\35\1\0\2\27\2" +
    "\35\3\0\73\35\1\0\4\35\3\0\17\40\1\0\40\26\1\0\2\26\1\0\4\43\1\27\2\37\1" +
    "\0\1\43\1\27\2\37\3\0\70\43\1\27\2\40\1\0\1\43\1\27\2\40\3\0\17\40\1" +
    "\0\40\26\1\0\2\26\1\0\4\43\1\27\2\"\1\0\1\43\1\27\2\41\3\0\17\40\1\0\50" +
    "\43\1\27\2\"\1\0\1\43\1\27\2\"\3\0\35\36\2\0\34\35\1\0\4\35\3\0\60\26\1" +
    "\0\2\26\1\0\r\44\2\0\60\26\1\0\1\45\1\26\1\0\1\45\16\0\17\40\1\0\40\26\1" +
    "\45\1\0\1\26\1\45\1\0\2\44\1\43\1\27\2\"\1\44\1\43\1\27\2\41\3\0\33\31\2" +
    "\0\2\31\17\131\1\31\17\24\1\31\4\0\33\31\2\0\2\31\36\24\1\31\5\0\33\31\2" +
    "\0\2\31\24\50\1\31\17\0\33\31\2\0\2\31\6\51\1\31\35\0\33\31\2\0\2\31\r" +
    "\52\1\31\26\0\33\31\2\0\2\31\n\50\1\31\31\0\33\31\2\0\2\31\5\63\1\31\16" +
    "\222\1\31\1\75\1\31\r\0\33\31\2\0\2\31\24\54\1\220\1\31\16\0\33\31\2" +
    "\0\2\31\7\50\1\31\34\0\33\31\2\0\2\31\13\57\1\31\30\0\33\31\2\0\2\31\6" +
    "\60\1\31\35\0\33\31\2\0\2\31\t\61\1\230\1\31\31\0\33\31\2\0\2\31\13\62\1" +
    "\31\30\0\33\31\2\0\2\31\b\101\1\31\4\72\1\31\26\0\33\31\2\0\2\31\24\24\1" +
    "\31\17\0\33\31\2\0\2\31\24\65\1\31\17\0\33\31\2\0\2\31\7\66\1\31\16\200\1" +
    "\31\r\0\33\31\2\0\2\31\20\214\1\31\23\0\33\31\2\0\2\31\5\70\1\31\36\0\33" +
    "\31\2\0\2\31\6\115\1\31\1\71\1\31\33\0\33\31\2\0\2\31\r\24\1\31\26\0\33" +
    "\31\2\0\2\31\27\73\1\31\f\0\33\31\2\0\2\31\t\74\1\50\1\31\31\0\33\31\2" +
    "\0\2\31\t\74\1\31\32\0\33\31\2\0\2\31\6\76\1\31\35\0\33\31\2\0\2\31\t" +
    "\212\1\31\2\153\1\31\3\77\1\31\23\0\33\31\2\0\2\31\7\50\1\31\4\24\1\31\t" +
    "\242\1\31\r\0\33\31\2\0\2\31\f\24\1\31\27\0\33\31\2\0\2\31\24\102\1\31\17" +
    "\0\33\31\2\0\2\31\24\103\1\31\17\0\33\31\2\0\2\31\26\104\1\31\r\0\33" +
    "\31\2\0\2\31\13\105\1\31\30\0\33\31\2\0\2\31\5\132\1\106\1\31\17\172\1" +
    "\31\r\0\33\31\2\0\2\31\13\24\1\31\30\0\33\31\2\0\2\31\b\110\1\31\33\0\33" +
    "\31\2\0\2\31\7\111\1\31\34\0\33\31\2\0\2\31\t\112\1\31\32\0\33\31\2\0\2" +
    "\31\36\113\1\31\5\0\33\31\2\0\2\31\n\24\1\31\31\0\33\31\2\0\2\31\t\24\1" +
    "\31\32\0\33\31\2\0\2\31\24\116\1\31\17\0\33\31\2\0\2\31\7\117\1\31\34" +
    "\0\33\31\2\0\2\31\26\120\1\31\r\0\33\31\2\0\2\31\37\121\1\31\4\0\33\31\2" +
    "\0\2\31\5\24\1\31\30\122\1\31\5\0\33\31\2\0\2\31\b\116\1\31\33\0\33\31\2" +
    "\0\2\31\36\124\1\31\5\0\33\31\2\0\2\31\n\125\1\31\31\0\33\31\2\0\2\31\n" +
    "\126\1\31\22\235\1\31\6\0\33\31\2\0\2\31\5\124\1\31\16\227\1\31\17\0\33" +
    "\31\2\0\2\31\1\130\1\31\"\0\33\31\2\0\2\31\b\24\1\31\33\0\33\31\2\0\2" +
    "\31\5\24\1\31\36\0\33\31\2\0\2\31\t\133\1\31\32\0\33\31\2\0\2\31\5\\\1" +
    "\31\36\0\33\31\2\0\2\31\2\24\1\31\41\0\33\31\2\0\2\31\26\136\1\31\r\0\33" +
    "\31\2\0\2\31\36\137\1\31\5\0\33\31\2\0\2\31\b\140\1\31\33\0\33\31\2\0\2" +
    "\31\40\23\1\31\3\0\33\31\2\0\2\31\36\142\1\31\5\0\33\31\2\0\2\31\16\143\1" +
    "\31\25\0\33\31\2\0\2\31\6\144\1\31\35\0\33\31\2\0\2\31\13\145\1\31\30" +
    "\0\33\31\2\0\2\31\5\146\1\31\36\0\33\31\2\0\2\31\b\147\1\31\33\0\33\31\2" +
    "\0\2\31\r\150\1\31\26\0\33\31\2\0\2\31\27\151\1\31\f\0\33\31\2\0\2\31\13" +
    "\152\1\31\30\0\33\31\2\0\2\31\t\143\1\31\32\0\33\31\2\0\2\31\27\154\1" +
    "\31\f\0\33\31\2\0\2\31\36\155\1\31\5\0\33\31\2\0\2\31\t\156\1\31\32\0\33" +
    "\31\2\0\2\31\5\157\1\175\1\31\35\0\33\31\2\0\2\31\7\210\1\160\1\31\r" +
    "\204\1\31\r\0\33\31\2\0\2\31\6\142\1\31\r\170\1\31\17\0\33\31\2\0\2\31\5" +
    "\162\1\31\36\0\33\31\2\0\2\31\36\23\1\31\5\0\33\31\2\0\2\31\24\164\1" +
    "\31\17\0\33\31\2\0\2\31\6\165\1\31\35\0\33\31\2\0\2\31\t\166\1\31\32" +
    "\0\33\31\2\0\2\31\26\167\1\31\r\0\33\31\2\0\2\31\n\164\1\31\31\0\33\31\2" +
    "\0\2\31\24\171\1\31\17\0\33\31\2\0\2\31\t\164\1\31\32\0\33\31\2\0\2\31\26" +
    "\173\1\31\r\0\33\31\2\0\2\31\4\174\1\31\37\0\33\31\2\0\2\31\4\164\1\31\37" +
    "\0\33\31\2\0\2\31\6\176\1\31\35\0\33\31\2\0\2\31\t\177\1\31\32\0\33\31\2" +
    "\0\2\31\3\164\1\31\40\0\33\31\2\0\2\31\26\201\1\31\r\0\33\31\2\0\2\31\2" +
    "\202\1\31\41\0\33\31\2\0\2\31\27\203\1\31\f\0\33\31\2\0\2\31\27\23\1" +
    "\31\f\0\33\31\2\0\2\31\6\205\1\31\35\0\33\31\2\0\2\31\24\206\1\31\17" +
    "\0\33\31\2\0\2\31\35\207\1\31\6\0\33\31\2\0\2\31\t\206\1\31\32\0\33\31\2" +
    "\0\2\31\b\247\1\31\r\211\1\31\r\0\33\31\2\0\2\31\n\23\1\31\31\0\33\31\2" +
    "\0\2\31\n\23\1\31\31\0\33\31\2\0\2\31\40\213\1\31\3\0\33\31\2\0\2\31\13" +
    "\215\1\31\30\0\33\31\2\0\2\31\36\216\1\31\5\0\33\31\2\0\2\31\t\217\1" +
    "\31\32\0\33\31\2\0\2\31\n\213\1\31\31\0\33\31\2\0\2\31\26\221\1\31\r" +
    "\0\33\31\2\0\2\31\t\213\1\31\32\0\33\31\2\0\2\31\13\223\1\31\30\0\33" +
    "\31\2\0\2\31\36\224\1\31\5\0\33\31\2\0\2\31\17\225\1\31\24\0\33\31\2" +
    "\0\2\31\36\226\1\31\5\0\33\31\2\0\2\31\t\23\1\31\32\0\33\31\2\0\2\31\27" +
    "\230\1\31\f\0\33\31\2\0\2\31\26\231\1\31\r\0\33\31\2\0\2\31\b\232\1\31\33" +
    "\0\33\31\2\0\2\31\t\233\1\31\32\0\33\31\2\0\2\31\n\234\1\31\31\0\33\31\2" +
    "\0\2\31\13\230\1\31\30\0\33\31\2\0\2\31\36\236\1\31\5\0\33\31\2\0\2\31\6" +
    "\237\1\31\35\0\33\31\2\0\2\31\n\240\1\31\31\0\33\31\2\0\2\31\13\241\1" +
    "\31\30\0\33\31\2\0\2\31\1\23\1\31\"\0\33\31\2\0\2\31\37\243\1\31\4\0\33" +
    "\31\2\0\2\31\t\244\1\31\32\0\33\31\2\0\2\31\27\245\1\31\f\0\33\31\2\0\2" +
    "\31\6\246\1\31\35\252\1\251\1\22\1\251\100\252\1\251\1\22\1\251\100\251\1" +
    "\0\1\251\101\r\1\254\2\21\1\254\77\r\1\254\2\21\1\254\77\0\5\20\1\16\1" +
    "\0\74");
    
    private static final byte[] ACTION_MAP = createActionMap(
    "\0\1\1\1\2\2\0\1\3\1\4\1\0\1\5\1\6\1\7\2\0\1\b\1\t\1\n\1" +
    "\13\1\f\1\r\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\0\1\17\1" +
    "\21\1\0\1\20\1\21\1\20\1\21\1\0\1\20\3\23\145\16\1\23\33" +
    "\24\1\0\2\24\1\0\1\17\1");
    
    private static final int LEXICAL_STATE_COMMENT = 0;
    private static final int LEXICAL_STATE_DOCTAG = 1;
    private static final int LEXICAL_STATE_DOC = 2;
    private static final int LEXICAL_STATE_INITIAL = 3;
    
    private String string = "";
    private int regionEnd;
    private int dot;
    private int lexicalState = LEXICAL_STATE_INITIAL;
    private int matchStart;
    private int matchEnd;
    private int startState = 26;
    
    private static byte[] createCharacterMap(String characterMapData) {
        byte[] characterMap = new byte[65536];
        int length = characterMapData.length();
        int i = 0;
        int j = 0;
        
        while (i < length) {
            byte curValue = (byte)characterMapData.charAt(i++);
            
            for (int x=characterMapData.charAt(i++);x>0;x--) {
                characterMap[j++] = curValue;
            }
        }
        
        return characterMap;
    }
    
    private static short[][] createTransitionTable(String transitionTableData) {
        short[][] transitionTable = new short[173][67];
        int length = transitionTableData.length();
        int i = 0;
        int j = 0;
        int k = 0;
        
        while (i < length) {
            short curValue = (short)((short)transitionTableData.charAt(i++) - 1);
            
            for (int x=transitionTableData.charAt(i++);x>0;x--) {
                transitionTable[j][k++] = curValue;
            }
            
            if (k == 67) {
                k = 0;
                j++;
            }
        }
        
        return transitionTable;
    }
    
    private static byte[] createActionMap(String actionMapData) {
        byte[] actionMap = new byte[173];
        int length = actionMapData.length();
        int i = 0;
        int j = 0;
        
        while (i < length) {
            byte curValue = (byte)((short)actionMapData.charAt(i++) - 1);
            
            for (int x=actionMapData.charAt(i++);x>0;x--) {
                actionMap[j++] = curValue;
            }
        }
        
        return actionMap;
    }
    
    public void setStringInternal(String string) {
        this.string = string != null ? string : "";
        
        regionEnd = this.string.length();
        
        dot = 0;
        lexicalState = LEXICAL_STATE_INITIAL;
        
        matchStart = 0;
        matchEnd = 0;
        
        startState = 26;
    }
    
    private void setLexicalState(int lexicalState) {
        switch(lexicalState) {
        case LEXICAL_STATE_COMMENT: startState = 0; break;
        case LEXICAL_STATE_DOCTAG: startState = 4; break;
        case LEXICAL_STATE_DOC: startState = 7; break;
        case LEXICAL_STATE_INITIAL: startState = 26; break;
        default:
            throw new IllegalArgumentException("invalid lexical state");
        }
        
        this.lexicalState = lexicalState;
    }
    
    private int getMatchStart() {
        return matchStart;
    }
    
    private int getMatchEnd() {
        return matchEnd;
    }
    
    public Token getNextToken() {
        while (dot < regionEnd) {
            
            // find longest match
            int curState = startState;
            int iterator = dot;
            int matchState = -1;
            int matchPosition = 0;
            
            do {
                curState = TRANSITION_TABLE[curState][CHARACTER_MAP[string
                        .charAt(iterator)]];
                
                if (curState == -1) {
                    break;
                }
                
                if (ACTION_MAP[curState] != -1) {
                    matchState = curState;
                    matchPosition = iterator;
                }
            } while (++iterator < regionEnd);
            
            // match found, perform action
            if (matchState != -1) {
                int endPosition = matchPosition + 1;
                
                matchStart = dot;
                matchEnd = endPosition;
                dot = endPosition;
                
                switch(ACTION_MAP[matchState]) {
                case 0: return endMultilineComment();
                case 1: commentText(); continue;
                case 2: return endTag();
                case 3: doctagText(); continue;
                case 4: return endDoc();
                case 5: return startTag();
                case 6: docText(); continue;
                case 7: return createLineComment();
                case 8: startDoc(); continue;
                case 9: startMultilineComment(); continue;
                case 10: return createString();
                case 11: return createChar();
                case 12: return createKeyword();
                case 13: return createKeyword2();
                case 14: return createOperator();
                case 15: return createInteger();
                case 16: return createFloat();
                case 17: return createAnnotation();
                case 18: return createIdentifer();
                case 19: return other();
                }
            }
            
            // no match found, set match values and report as error
            matchStart = dot;
            matchEnd = dot;
            
            throw new IllegalStateException("invalid input");
        }
        
        // no match found, set match values and return to caller
        matchStart = dot;
        matchEnd = dot;
        
        return null;
    }
    
    //%%LEX-MAIN-END%%

}
