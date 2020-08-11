package syntaxfx.lexers;

import syntaxfx.Lexer;
import syntaxfx.Token;

import java.util.Stack;

/**
 * The PythonLexer class represents a lexer for the Python programming language.
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
public class PythonLexer extends Lexer {
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

    //%%LEX-CONDITION-START%%DOC%%
    
    /** @expr \"\"\" */
    Token endDoc() {
        leaveLexicalState();
        return new Token(Token.DOC_COMMENT, stateStart, matchEnd);
    }

    /** @expr [^] */
    void docText() {}

    //%%LEX-CONDITION-END%%
   
    //%%LEX-CONDITION-START%%INITIAL%%

    /** @expr #[^\r\n]* */
    Token createLineComment() { return new Token(Token.COMMENT, matchStart, matchEnd); }

    /** @expr \"\"\" */
    void startDoc() {
        stateStart = matchStart;
        enterLexicalState(LEXICAL_STATE_DOC);
    }

    /** @expr \"(\\[^\r\n]|[^\\\"])*\" */
    Token createString() { return new Token(Token.STRING, matchStart, matchEnd); }

    /** @expr \'(\\[^\r\n]|[^\\\'])*\' */
    Token createString2() { return new Token(Token.STRING, matchStart, matchEnd); }
    
    /** @expr False|None|True|async|class|def|from|global|lambda|nonlocal */
    Token createKeyword() { return new Token(Token.KEYWORD, matchStart, matchEnd); }

    /** @expr and|as|assert|await|break|continue|del|if|else|except|finally|for|if|import|in|is|not|or|pass|raise|return|try|while|with|yield */
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
    "\34\t\4\1\1\1\4\2\1\1\34\16\4\5\21\1\3\1\5\1\37\1\30\1\26\1" +
    "\2\1\6\1\7\1\31\1\35\1\4\1\36\1\r\1\32\1\101\1\102\1\73\6\63\2" +
    "\24\1\f\1\20\1\16\1\17\1\23\1\33\1\62\1\70\1\62\1\72\1\71\1" +
    "\76\1\37\5\60\1\37\1\41\1\37\5\44\1\37\3\61\1\37\2\n\1\0\1\13\1" +
    "\27\1\37\1\4\1\66\1\74\1\67\1\100\1\75\1\77\1\51\1\57\1\54\1" +
    "\37\1\55\1\64\1\50\1\43\1\"\1\56\1\37\1\45\1\40\1\52\1\46\1" +
    "\37\1\53\1\65\1\'\1\37\1\b\1\25\1\t\1\22\1\34\41\4\2\37\4\4\4" +
    "\37\1\4\2\34\1\4\7\37\1\4\4\37\1\4\5\37\27\4\1\37\37\4\1\37\u01ca" +
    "\4\4\37\f\4\16\37\5\4\7\37\1\4\1\37\1\4\21\34\160\37\5\4\1\37\2" +
    "\4\2\37\4\4\b\37\1\4\1\37\3\4\1\37\1\4\1\37\24\4\1\37\123\4\1" +
    "\37\213\4\1\34\5\4\2\37\236\4\t\37\46\4\2\37\1\4\7\37\'\4\7" +
    "\37\1\4\1\34\55\4\1\34\1\4\1\34\2\4\1\34\2\4\1\34\1\4\b\37\33" +
    "\4\5\37\3\4\r\34\5\4\6\37\1\4\4\34\13\4\5\37\53\34\37\4\4\37\2" +
    "\34\1\37\143\4\1\37\1\34\b\4\1\34\6\37\2\34\2\4\1\34\4\37\2" +
    "\34\n\37\3\4\2\37\1\4\17\34\1\37\1\34\1\37\36\34\33\4\2\37\131" +
    "\34\13\37\1\4\16\34\n\37\41\34\t\37\2\4\4\37\1\4\5\37\26\34\4" +
    "\37\1\34\t\37\1\34\3\37\1\34\5\4\22\37\31\34\3\4\104\37\1\4\1" +
    "\37\13\4\67\34\33\4\1\34\4\37\66\34\3\37\1\34\22\37\1\34\7\37\n" +
    "\34\2\4\2\34\n\4\1\37\7\4\1\37\7\4\1\34\3\4\1\37\b\4\2\37\2" +
    "\4\2\37\26\4\1\37\7\4\1\37\1\4\3\37\4\4\2\34\1\37\1\34\7\4\2" +
    "\34\2\4\2\34\3\37\1\4\b\34\1\4\4\37\2\4\1\37\3\34\2\4\2\34\n" +
    "\37\4\4\7\37\1\4\5\34\3\4\1\37\6\4\4\37\2\4\2\37\26\4\1\37\7" +
    "\4\1\37\2\4\1\37\2\4\1\37\2\4\2\34\1\4\1\34\5\4\4\34\2\4\2\34\3" +
    "\4\3\34\1\4\7\37\4\4\1\37\1\4\7\34\f\37\3\34\1\4\13\34\3\4\1" +
    "\37\t\4\1\37\3\4\1\37\26\4\1\37\7\4\1\37\2\4\1\37\5\4\2\34\1" +
    "\37\1\34\b\4\1\34\3\4\1\34\3\4\2\37\1\4\17\37\2\34\2\4\2\34\n" +
    "\4\1\37\1\4\17\34\3\4\1\37\b\4\2\37\2\4\2\37\26\4\1\37\7\4\1" +
    "\37\2\4\1\37\5\4\2\34\1\37\1\34\7\4\2\34\2\4\2\34\3\4\b\34\2" +
    "\4\4\37\2\4\1\37\3\34\2\4\2\34\n\4\1\37\1\4\20\34\1\37\1\4\1" +
    "\37\6\4\3\37\3\4\1\37\4\4\3\37\2\4\1\37\1\4\1\37\2\4\3\37\2" +
    "\4\3\37\3\4\3\37\f\4\4\34\5\4\3\34\3\4\1\34\4\4\2\37\1\4\6\34\1" +
    "\4\16\34\n\4\t\37\1\4\7\34\3\4\1\37\b\4\1\37\3\4\1\37\27\4\1" +
    "\37\n\4\1\37\5\4\3\37\1\34\7\4\1\34\3\4\1\34\4\4\7\34\2\4\1" +
    "\37\2\4\6\37\2\34\2\4\2\34\n\4\22\34\2\4\1\37\b\4\1\37\3\4\1" +
    "\37\27\4\1\37\n\4\1\37\5\4\2\34\1\37\1\34\7\4\1\34\3\4\1\34\4" +
    "\4\7\34\2\4\7\37\1\4\1\37\2\34\2\4\2\34\n\4\1\37\2\4\17\34\2" +
    "\4\1\37\b\4\1\37\3\4\1\37\51\4\2\37\1\34\7\4\1\34\3\4\1\34\4" +
    "\37\1\4\b\34\1\4\b\37\2\34\2\4\2\34\n\4\n\37\6\4\2\34\2\4\1" +
    "\37\22\4\3\37\30\4\1\37\t\4\1\37\1\4\2\37\7\4\3\34\1\4\4\34\6" +
    "\4\1\34\1\4\1\34\b\4\22\34\2\4\r\37\60\34\1\37\2\34\7\4\4\37\b" +
    "\34\b\4\1\34\n\4\'\37\2\4\1\37\1\4\2\37\2\4\1\37\1\4\2\37\1" +
    "\4\6\37\4\4\1\37\7\4\1\37\3\4\1\37\1\4\1\37\1\4\2\37\2\4\1\37\4" +
    "\34\1\37\2\34\6\4\1\34\2\37\1\4\2\37\5\4\1\37\1\4\1\34\6\4\2" +
    "\34\n\4\2\37\4\4\40\37\1\4\27\34\2\4\6\34\n\4\13\34\1\4\1\34\1" +
    "\4\1\34\1\4\4\34\2\37\b\4\1\37\44\4\4\34\24\4\1\34\2\37\5\34\13" +
    "\4\1\34\44\4\t\34\1\4\71\37\53\34\24\37\1\34\n\4\6\37\6\34\4" +
    "\37\4\34\3\37\1\34\3\37\2\34\7\37\3\34\4\37\r\34\f\37\1\34\17" +
    "\4\2\37\46\4\1\37\1\4\5\37\1\4\2\37\53\4\1\37\u014d\4\1\37\4" +
    "\4\2\37\7\4\1\37\1\4\1\37\4\4\2\37\51\4\1\37\4\4\2\37\41\4\1" +
    "\37\4\4\2\37\7\4\1\37\1\4\1\37\4\4\2\37\17\4\1\37\71\4\1\37\4" +
    "\4\2\37\103\4\2\34\3\4\40\37\20\4\20\37\125\4\f\37\u026c\4\2" +
    "\37\21\4\1\37\32\4\5\37\113\4\3\37\3\4\17\37\r\4\1\37\4\34\3" +
    "\4\13\37\22\34\3\4\13\37\22\34\2\4\f\37\r\4\1\37\3\4\1\34\2" +
    "\4\f\37\64\34\40\4\3\37\1\4\3\37\2\34\1\4\2\34\n\4\41\34\3\4\2" +
    "\34\n\4\6\37\130\4\b\37\51\34\1\37\1\4\5\37\106\4\n\37\35\4\3" +
    "\34\f\4\4\34\f\4\n\34\n\37\36\4\2\37\5\4\13\37\54\4\4\34\21" +
    "\37\7\34\2\4\6\34\n\4\46\37\27\34\5\4\4\37\65\34\n\4\1\34\35" +
    "\4\2\34\13\4\6\34\n\4\r\37\1\4\130\34\5\37\57\34\21\37\7\4\4" +
    "\34\n\4\21\34\t\4\f\34\3\37\36\34\r\37\2\34\n\37\54\34\16\4\f" +
    "\37\44\34\24\4\b\34\n\4\3\37\3\34\n\37\44\4\122\34\3\4\1\34\25" +
    "\37\4\34\1\37\4\34\3\37\2\4\t\37\300\34\'\4\25\34\4\37\u0116" +
    "\4\2\37\6\4\2\37\46\4\2\37\6\4\2\37\b\4\1\37\1\4\1\37\1\4\1" +
    "\37\1\4\1\37\37\4\2\37\65\4\1\37\7\4\1\37\1\4\3\37\3\4\1\37\7" +
    "\4\3\37\4\4\2\37\6\4\4\37\r\4\5\37\3\4\1\37\7\4\16\34\5\4\32" +
    "\34\5\4\20\37\2\4\23\37\1\4\13\34\5\4\5\34\6\4\1\37\1\4\r\37\1" +
    "\4\20\37\r\4\3\37\33\4\25\34\r\4\4\34\1\4\3\34\f\4\21\37\1\4\4" +
    "\37\1\4\2\37\n\4\1\37\1\4\3\37\5\4\6\37\1\4\1\37\1\4\1\37\1" +
    "\4\1\37\4\4\1\37\13\4\2\37\4\4\5\37\5\4\4\37\1\4\21\37\51\4\u0a77" +
    "\37\57\4\1\37\57\4\1\37\205\4\6\37\4\34\3\37\2\4\f\37\46\4\1" +
    "\37\1\4\5\37\1\4\2\37\70\4\7\37\1\4\17\34\1\37\27\4\t\37\7\4\1" +
    "\37\7\4\1\37\7\4\1\37\7\4\1\37\7\4\1\37\7\4\1\37\7\4\1\37\7" +
    "\4\1\34\40\4\57\37\1\4\u01d5\37\3\4\31\37\t\34\6\4\1\37\5\4\2" +
    "\37\5\4\4\37\126\4\2\34\2\4\2\37\3\4\1\37\132\4\1\37\4\4\5\37\51" +
    "\4\3\37\136\4\21\37\33\4\65\37\20\4\u0200\37\u19b6\4\112\37\u51cd" +
    "\4\63\37\u048d\4\103\37\56\4\2\37\u010d\4\3\37\20\34\n\37\2" +
    "\4\24\37\57\34\1\4\4\34\n\4\1\37\31\4\7\34\1\37\120\34\2\4\45" +
    "\37\t\4\2\37\147\4\2\37\4\4\1\37\4\4\f\37\13\4\115\37\n\34\1" +
    "\37\3\34\1\37\4\34\1\37\27\34\5\4\20\37\1\4\7\37\64\4\f\34\2" +
    "\37\62\34\21\4\13\34\n\4\6\34\22\37\6\4\3\37\1\4\4\34\n\37\34" +
    "\34\b\4\2\37\27\34\r\4\f\37\35\4\3\34\4\37\57\34\16\4\16\37\1" +
    "\34\n\4\46\37\51\34\16\4\t\37\3\34\1\37\b\34\2\4\2\34\n\4\6" +
    "\37\27\4\3\37\1\34\1\4\4\37\60\34\1\37\1\34\3\37\2\34\2\37\5" +
    "\34\2\37\1\34\1\37\1\4\30\37\3\4\2\37\13\34\5\4\2\37\3\34\2" +
    "\4\n\37\6\4\2\37\6\4\2\37\6\4\t\37\7\4\1\37\7\4\221\37\43\34\b" +
    "\4\1\34\2\4\2\34\n\4\6\37\u2ba4\4\f\37\27\4\4\37\61\4\u2104" +
    "\37\u016e\4\2\37\152\4\46\37\7\4\f\37\5\4\5\37\1\34\1\37\n\4\1" +
    "\37\r\4\1\37\5\4\1\37\1\4\1\37\2\4\1\37\2\4\1\37\154\4\41\37\u016b" +
    "\4\22\37\100\4\2\37\66\4\50\37\r\4\3\34\20\4\20\34\7\4\f\37\2" +
    "\4\30\37\3\4\31\37\1\4\6\37\5\4\1\37\207\4\2\34\1\4\4\37\1\4\13" +
    "\34\n\4\7\37\32\4\4\37\1\4\1\37\32\4\13\37\131\4\3\37\6\4\2" +
    "\37\6\4\2\37\6\4\2\37\3\4\3\37\2\4\3\37\2\4\22\34\3\4\4");
    
    private static final byte[][] TRANSITION_TABLE = createTransitionTable(
    "\0\3\2\1\0\77\0\103\0\103\3\3\5\1\3\77\0\3\1\1\0\77\167\1\0\1\167\101" +
    "\7\1\0\1\7\101\0\103\0\103\0\103\0\34\21\1\0\2\21\44\0\34\21\1\0\2\21\44" +
    "\0\103\0\103\0\103\0\34\20\1\0\2\20\44\0\34\21\1\0\2\21\44\0\103\22\2" +
    "\163\1\166\1\22\1\7\1\r\7\24\1\r\r\20\1\22\1\r\2\21\2\132\1\116\1\106\1" +
    "\130\1\62\1\21\1\"\1\21\1\151\1\115\1\50\1\44\1\21\1\125\1\21\4\27\1" +
    "\144\1\21\1\43\1\56\1\21\3\27\1\72\1\60\1\135\1\114\1\64\1\36\1\27\1" +
    "\0\63\30\1\0\7\30\1\0\5\30\2\0\63\25\1\0\6\17\1\25\1\0\2\17\3\25\2\0\63" +
    "\25\1\0\7\25\1\0\5\25\2\0\r\30\1\0\"\16\1\0\2\27\1\16\1\0\4\33\1\17\1" +
    "\27\1\0\1\33\1\17\3\27\2\0\63\30\1\0\5\33\1\17\1\30\1\0\1\33\1\17\3" +
    "\30\2\0\r\30\1\0\"\16\1\0\2\32\1\16\1\0\4\33\1\17\1\31\1\0\1\33\1\17\3" +
    "\31\2\0\r\30\1\0\45\32\1\0\5\33\1\17\1\32\1\0\1\33\1\17\3\32\2\0\35" +
    "\26\2\0\24\25\1\0\7\25\1\0\5\25\2\0\60\16\1\0\3\16\1\0\f\34\2\0\60\16\1" +
    "\0\1\35\2\16\1\0\1\35\r\0\r\30\1\0\"\16\1\35\1\0\1\32\1\16\1\35\1\0\2" +
    "\34\1\33\1\17\1\31\1\34\1\33\1\17\3\31\2\0\34\21\1\0\2\21\41\f\1\21\2" +
    "\0\34\21\1\0\2\21\25\37\1\21\16\0\34\21\1\0\2\21\36\40\1\21\5\0\34\21\1" +
    "\0\2\21\r\41\1\21\26\0\34\21\1\0\2\21\1\103\1\21\2\37\1\21\7\100\1\21\27" +
    "\0\34\21\1\0\2\21\1\f\1\21\2\f\1\21\4\105\1\21\26\f\1\21\3\0\34\21\1" +
    "\0\2\21\36\f\1\21\5\0\34\21\1\0\2\21\25\45\1\21\16\0\34\21\1\0\2\21\r" +
    "\46\1\21\26\0\34\21\1\0\2\21\r\66\1\21\2\'\1\21\23\0\34\21\1\0\2\21\7" +
    "\45\1\21\34\0\34\21\1\0\2\21\4\51\1\21\37\0\34\21\1\0\2\21\r\52\1\21\26" +
    "\0\34\21\1\0\2\21\13\53\1\21\30\0\34\21\1\0\2\21\4\54\1\21\37\0\34\21\1" +
    "\0\2\21\3\55\1\21\21\162\1\21\16\0\34\21\1\0\2\21\1\45\1\21\"\0\34\21\1" +
    "\0\2\21\25\57\1\76\1\21\r\0\34\21\1\0\2\21\r\57\1\21\26\0\34\21\1\0\2" +
    "\21\27\61\1\21\6\122\1\21\5\0\34\21\1\0\2\21\25\f\1\21\n\13\1\21\3\0\34" +
    "\21\1\0\2\21\36\63\1\21\5\0\34\21\1\0\2\21\20\f\1\21\23\0\34\21\1\0\2" +
    "\21\13\65\1\21\30\0\34\21\1\0\2\21\16\f\1\21\25\0\34\21\1\0\2\21\27" +
    "\67\1\21\f\0\34\21\1\0\2\21\36\70\1\21\5\0\34\21\1\0\2\21\6\71\1\21\35" +
    "\0\34\21\1\0\2\21\13\f\1\21\30\0\34\21\1\0\2\21\17\73\1\21\24\0\34\21\1" +
    "\0\2\21\36\74\1\21\5\0\34\21\1\0\2\21\30\75\1\21\13\0\34\21\1\0\2\21\r" +
    "\73\1\21\26\0\34\21\1\0\2\21\27\77\1\21\f\0\34\21\1\0\2\21\6\73\1\21\35" +
    "\0\34\21\1\0\2\21\36\101\1\21\5\0\34\21\1\0\2\21\1\102\1\21\6\137\1" +
    "\21\33\0\34\21\1\0\2\21\3\101\1\21\40\0\34\21\1\0\2\21\17\104\1\21\24" +
    "\0\34\21\1\0\2\21\3\155\1\21\40\0\34\21\1\0\2\21\b\f\1\21\33\0\34\21\1" +
    "\0\2\21\25\107\1\21\16\0\34\21\1\0\2\21\25\110\1\21\16\0\34\21\1\0\2" +
    "\21\27\111\1\21\f\0\34\21\1\0\2\21\4\112\1\21\37\0\34\21\1\0\2\21\3" +
    "\116\1\21\2\157\1\21\6\113\1\21\26\0\34\21\1\0\2\21\6\107\1\21\35\0\34" +
    "\21\1\0\2\21\6\f\1\21\35\0\34\21\1\0\2\21\4\f\1\21\37\0\34\21\1\0\2" +
    "\21\6\117\1\21\35\0\34\21\1\0\2\21\7\120\1\21\34\0\34\21\1\0\2\21\13" +
    "\121\1\21\30\0\34\21\1\0\2\21\1\f\1\21\"\0\34\21\1\0\2\21\1\123\1\21\"" +
    "\0\34\21\1\0\2\21\27\124\1\21\f\0\34\21\1\0\2\21\36\13\1\21\5\0\34\21\1" +
    "\0\2\21\7\126\1\21\34\0\34\21\1\0\2\21\6\127\1\21\35\0\34\21\1\0\2\21\4" +
    "\126\1\21\37\0\34\21\1\0\2\21\3\131\1\21\40\0\34\21\1\0\2\21\1\126\1" +
    "\21\"\0\34\21\1\0\2\21\25\133\1\21\16\0\34\21\1\0\2\21\27\\\1\21\f\0\34" +
    "\21\1\0\2\21\30\13\1\21\13\0\34\21\1\0\2\21\4\136\1\21\37\0\34\21\1" +
    "\0\2\21\27\13\1\21\f\0\34\21\1\0\2\21\41\140\1\21\2\0\34\21\1\0\2\21\35" +
    "\141\1\21\6\0\34\21\1\0\2\21\t\142\1\21\32\0\34\21\1\0\2\21\27\143\1" +
    "\21\f\0\34\21\1\0\2\21\25\13\1\21\16\0\34\21\1\0\2\21\27\145\1\21\f" +
    "\0\34\21\1\0\2\21\35\146\1\21\6\0\34\21\1\0\2\21\3\147\1\21\40\0\34" +
    "\21\1\0\2\21\25\150\1\21\16\0\34\21\1\0\2\21\30\146\1\21\13\0\34\21\1" +
    "\0\2\21\3\152\1\21\40\0\34\21\1\0\2\21\25\153\1\21\16\0\34\21\1\0\2" +
    "\21\4\154\1\21\6\f\1\21\30\0\34\21\1\0\2\21\t\13\1\21\32\0\34\21\1\0\2" +
    "\21\3\156\1\21\40\0\34\21\1\0\2\21\1\13\1\21\"\0\34\21\1\0\2\21\1\160\1" +
    "\21\"\0\34\21\1\0\2\21\27\161\1\21\f\165\1\164\1\n\1\164\100\165\1\164\1" +
    "\n\1\164\100\164\1\0\1\164\101\6\1\167\2\170\1\167\77\6\1\167\2\t\1" +
    "\167\77\0\3\b\1\0\77");
    
    private static final byte[] ACTION_MAP = createActionMap(
    "\0\1\1\1\2\1\0\1\2\1\0\1\3\1\4\1\5\1\6\1\7\1\b\1\t\1\n\1" +
    "\13\1\f\1\r\1\16\1\0\1\t\1\13\1\0\1\n\1\13\1\n\1\13\1" +
    "\0\1\n\3\r\44\b\1\r\57\16\1\0\2\16\1\0\1\5\1");
    
    private static final int LEXICAL_STATE_DOC = 0;
    private static final int LEXICAL_STATE_INITIAL = 1;
    
    private String string = "";
    private int regionEnd;
    private int dot;
    private int lexicalState = LEXICAL_STATE_INITIAL;
    private int matchStart;
    private int matchEnd;
    private int startState = 18;
    
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
    
    private static byte[][] createTransitionTable(String transitionTableData) {
        byte[][] transitionTable = new byte[120][67];
        int length = transitionTableData.length();
        int i = 0;
        int j = 0;
        int k = 0;
        
        while (i < length) {
            byte curValue = (byte)((short)transitionTableData.charAt(i++) - 1);
            
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
        byte[] actionMap = new byte[120];
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
        
        startState = 18;
    }
    
    private void setLexicalState(int lexicalState) {
        switch(lexicalState) {
        case LEXICAL_STATE_DOC: startState = 3; break;
        case LEXICAL_STATE_INITIAL: startState = 18; break;
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
                case 0: return endDoc();
                case 1: docText(); continue;
                case 2: return createLineComment();
                case 3: startDoc(); continue;
                case 4: return createString();
                case 5: return createString2();
                case 6: return createKeyword();
                case 7: return createKeyword2();
                case 8: return createOperator();
                case 9: return createInteger();
                case 10: return createFloat();
                case 11: return createAnnotation();
                case 12: return createIdentifer();
                case 13: return other();
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
