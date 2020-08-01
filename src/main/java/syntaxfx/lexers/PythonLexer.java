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
    
    //%%LEX-CONDITION-START%%STRING%%
    
    /** @expr ((\\\")|(\\\\))*\" */
    Token endString() {
        leaveLexicalState();
        return new Token(Token.STRING, stateStart, matchEnd);
    }

    /** @expr [^] */
    void stringText() {}

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
    
    //================================================
    //     _                      _____ _             
    //    / \   _ __  _ __   ___ |  ___| | ___ _  __  
    //   / _ \ |  _ \|  _ \ / _ \| |_  | |/ _ \ \/ /  
    //  / ___ \| | | | | | | (_) |  _| | |  __/>  <   
    // /_/   \_\_| |_|_| |_|\___/|_|   |_|\___/_/\_\  
    //                                                
    //================================================
    
    /*************************************************
     *             Generation Statistics             *
     * * * * * * * * * * * * * * * * * * * * * * * * *
     *                                               *
     * Rules:           15                           *
     * Lookaheads:      0                            *
     * Alphabet length: 66                           *
     * NFA states:      281                          *
     * DFA states:      122                          *
     * Static size:     74 KB                        *
     * Instance size:   28 Bytes                     *
     *                                               *
     ************************************************/
    
    //=================
    // Table Constants
    //=================
    
    /**
     * Maps Unicode characters to DFA input symbols.
     */
    private static final byte[] CHARACTER_MAP = createCharacterMap(
    "\33\t\3\1\0\1\3\2\0\1\33\16\3\5\20\1\1\1\4\1\36\1\27\1\25\1" +
    "\3\1\5\1\6\1\30\1\34\1\3\1\35\1\f\1\31\1\100\1\101\1\72\6\62\2" +
    "\23\1\13\1\17\1\r\1\16\1\22\1\32\1\61\1\67\1\61\1\71\1\70\1" +
    "\75\1\36\5\57\1\36\1\40\1\36\5\43\1\36\3\60\1\36\2\t\1\2\1\n\1" +
    "\26\1\36\1\3\1\65\1\73\1\66\1\77\1\74\1\76\1\50\1\56\1\53\1" +
    "\36\1\54\1\63\1\'\1\"\1\41\1\55\1\36\1\44\1\37\1\51\1\45\1\36\1" +
    "\52\1\64\1\46\1\36\1\7\1\24\1\b\1\21\1\33\41\3\2\36\4\3\4\36\1" +
    "\3\2\33\1\3\7\36\1\3\4\36\1\3\5\36\27\3\1\36\37\3\1\36\u01ca" +
    "\3\4\36\f\3\16\36\5\3\7\36\1\3\1\36\1\3\21\33\160\36\5\3\1\36\2" +
    "\3\2\36\4\3\b\36\1\3\1\36\3\3\1\36\1\3\1\36\24\3\1\36\123\3\1" +
    "\36\213\3\1\33\5\3\2\36\236\3\t\36\46\3\2\36\1\3\7\36\'\3\7" +
    "\36\1\3\1\33\55\3\1\33\1\3\1\33\2\3\1\33\2\3\1\33\1\3\b\36\33" +
    "\3\5\36\3\3\r\33\5\3\6\36\1\3\4\33\13\3\5\36\53\33\37\3\4\36\2" +
    "\33\1\36\143\3\1\36\1\33\b\3\1\33\6\36\2\33\2\3\1\33\4\36\2" +
    "\33\n\36\3\3\2\36\1\3\17\33\1\36\1\33\1\36\36\33\33\3\2\36\131" +
    "\33\13\36\1\3\16\33\n\36\41\33\t\36\2\3\4\36\1\3\5\36\26\33\4" +
    "\36\1\33\t\36\1\33\3\36\1\33\5\3\22\36\31\33\3\3\104\36\1\3\1" +
    "\36\13\3\67\33\33\3\1\33\4\36\66\33\3\36\1\33\22\36\1\33\7\36\n" +
    "\33\2\3\2\33\n\3\1\36\7\3\1\36\7\3\1\33\3\3\1\36\b\3\2\36\2" +
    "\3\2\36\26\3\1\36\7\3\1\36\1\3\3\36\4\3\2\33\1\36\1\33\7\3\2" +
    "\33\2\3\2\33\3\36\1\3\b\33\1\3\4\36\2\3\1\36\3\33\2\3\2\33\n" +
    "\36\4\3\7\36\1\3\5\33\3\3\1\36\6\3\4\36\2\3\2\36\26\3\1\36\7" +
    "\3\1\36\2\3\1\36\2\3\1\36\2\3\2\33\1\3\1\33\5\3\4\33\2\3\2\33\3" +
    "\3\3\33\1\3\7\36\4\3\1\36\1\3\7\33\f\36\3\33\1\3\13\33\3\3\1" +
    "\36\t\3\1\36\3\3\1\36\26\3\1\36\7\3\1\36\2\3\1\36\5\3\2\33\1" +
    "\36\1\33\b\3\1\33\3\3\1\33\3\3\2\36\1\3\17\36\2\33\2\3\2\33\n" +
    "\3\1\36\1\3\17\33\3\3\1\36\b\3\2\36\2\3\2\36\26\3\1\36\7\3\1" +
    "\36\2\3\1\36\5\3\2\33\1\36\1\33\7\3\2\33\2\3\2\33\3\3\b\33\2" +
    "\3\4\36\2\3\1\36\3\33\2\3\2\33\n\3\1\36\1\3\20\33\1\36\1\3\1" +
    "\36\6\3\3\36\3\3\1\36\4\3\3\36\2\3\1\36\1\3\1\36\2\3\3\36\2" +
    "\3\3\36\3\3\3\36\f\3\4\33\5\3\3\33\3\3\1\33\4\3\2\36\1\3\6\33\1" +
    "\3\16\33\n\3\t\36\1\3\7\33\3\3\1\36\b\3\1\36\3\3\1\36\27\3\1" +
    "\36\n\3\1\36\5\3\3\36\1\33\7\3\1\33\3\3\1\33\4\3\7\33\2\3\1" +
    "\36\2\3\6\36\2\33\2\3\2\33\n\3\22\33\2\3\1\36\b\3\1\36\3\3\1" +
    "\36\27\3\1\36\n\3\1\36\5\3\2\33\1\36\1\33\7\3\1\33\3\3\1\33\4" +
    "\3\7\33\2\3\7\36\1\3\1\36\2\33\2\3\2\33\n\3\1\36\2\3\17\33\2" +
    "\3\1\36\b\3\1\36\3\3\1\36\51\3\2\36\1\33\7\3\1\33\3\3\1\33\4" +
    "\36\1\3\b\33\1\3\b\36\2\33\2\3\2\33\n\3\n\36\6\3\2\33\2\3\1" +
    "\36\22\3\3\36\30\3\1\36\t\3\1\36\1\3\2\36\7\3\3\33\1\3\4\33\6" +
    "\3\1\33\1\3\1\33\b\3\22\33\2\3\r\36\60\33\1\36\2\33\7\3\4\36\b" +
    "\33\b\3\1\33\n\3\'\36\2\3\1\36\1\3\2\36\2\3\1\36\1\3\2\36\1" +
    "\3\6\36\4\3\1\36\7\3\1\36\3\3\1\36\1\3\1\36\1\3\2\36\2\3\1\36\4" +
    "\33\1\36\2\33\6\3\1\33\2\36\1\3\2\36\5\3\1\36\1\3\1\33\6\3\2" +
    "\33\n\3\2\36\4\3\40\36\1\3\27\33\2\3\6\33\n\3\13\33\1\3\1\33\1" +
    "\3\1\33\1\3\4\33\2\36\b\3\1\36\44\3\4\33\24\3\1\33\2\36\5\33\13" +
    "\3\1\33\44\3\t\33\1\3\71\36\53\33\24\36\1\33\n\3\6\36\6\33\4" +
    "\36\4\33\3\36\1\33\3\36\2\33\7\36\3\33\4\36\r\33\f\36\1\33\17" +
    "\3\2\36\46\3\1\36\1\3\5\36\1\3\2\36\53\3\1\36\u014d\3\1\36\4" +
    "\3\2\36\7\3\1\36\1\3\1\36\4\3\2\36\51\3\1\36\4\3\2\36\41\3\1" +
    "\36\4\3\2\36\7\3\1\36\1\3\1\36\4\3\2\36\17\3\1\36\71\3\1\36\4" +
    "\3\2\36\103\3\2\33\3\3\40\36\20\3\20\36\125\3\f\36\u026c\3\2" +
    "\36\21\3\1\36\32\3\5\36\113\3\3\36\3\3\17\36\r\3\1\36\4\33\3" +
    "\3\13\36\22\33\3\3\13\36\22\33\2\3\f\36\r\3\1\36\3\3\1\33\2" +
    "\3\f\36\64\33\40\3\3\36\1\3\3\36\2\33\1\3\2\33\n\3\41\33\3\3\2" +
    "\33\n\3\6\36\130\3\b\36\51\33\1\36\1\3\5\36\106\3\n\36\35\3\3" +
    "\33\f\3\4\33\f\3\n\33\n\36\36\3\2\36\5\3\13\36\54\3\4\33\21" +
    "\36\7\33\2\3\6\33\n\3\46\36\27\33\5\3\4\36\65\33\n\3\1\33\35" +
    "\3\2\33\13\3\6\33\n\3\r\36\1\3\130\33\5\36\57\33\21\36\7\3\4" +
    "\33\n\3\21\33\t\3\f\33\3\36\36\33\r\36\2\33\n\36\54\33\16\3\f" +
    "\36\44\33\24\3\b\33\n\3\3\36\3\33\n\36\44\3\122\33\3\3\1\33\25" +
    "\36\4\33\1\36\4\33\3\36\2\3\t\36\300\33\'\3\25\33\4\36\u0116" +
    "\3\2\36\6\3\2\36\46\3\2\36\6\3\2\36\b\3\1\36\1\3\1\36\1\3\1" +
    "\36\1\3\1\36\37\3\2\36\65\3\1\36\7\3\1\36\1\3\3\36\3\3\1\36\7" +
    "\3\3\36\4\3\2\36\6\3\4\36\r\3\5\36\3\3\1\36\7\3\16\33\5\3\32" +
    "\33\5\3\20\36\2\3\23\36\1\3\13\33\5\3\5\33\6\3\1\36\1\3\r\36\1" +
    "\3\20\36\r\3\3\36\33\3\25\33\r\3\4\33\1\3\3\33\f\3\21\36\1\3\4" +
    "\36\1\3\2\36\n\3\1\36\1\3\3\36\5\3\6\36\1\3\1\36\1\3\1\36\1" +
    "\3\1\36\4\3\1\36\13\3\2\36\4\3\5\36\5\3\4\36\1\3\21\36\51\3\u0a77" +
    "\36\57\3\1\36\57\3\1\36\205\3\6\36\4\33\3\36\2\3\f\36\46\3\1" +
    "\36\1\3\5\36\1\3\2\36\70\3\7\36\1\3\17\33\1\36\27\3\t\36\7\3\1" +
    "\36\7\3\1\36\7\3\1\36\7\3\1\36\7\3\1\36\7\3\1\36\7\3\1\36\7" +
    "\3\1\33\40\3\57\36\1\3\u01d5\36\3\3\31\36\t\33\6\3\1\36\5\3\2" +
    "\36\5\3\4\36\126\3\2\33\2\3\2\36\3\3\1\36\132\3\1\36\4\3\5\36\51" +
    "\3\3\36\136\3\21\36\33\3\65\36\20\3\u0200\36\u19b6\3\112\36\u51cd" +
    "\3\63\36\u048d\3\103\36\56\3\2\36\u010d\3\3\36\20\33\n\36\2" +
    "\3\24\36\57\33\1\3\4\33\n\3\1\36\31\3\7\33\1\36\120\33\2\3\45" +
    "\36\t\3\2\36\147\3\2\36\4\3\1\36\4\3\f\36\13\3\115\36\n\33\1" +
    "\36\3\33\1\36\4\33\1\36\27\33\5\3\20\36\1\3\7\36\64\3\f\33\2" +
    "\36\62\33\21\3\13\33\n\3\6\33\22\36\6\3\3\36\1\3\4\33\n\36\34" +
    "\33\b\3\2\36\27\33\r\3\f\36\35\3\3\33\4\36\57\33\16\3\16\36\1" +
    "\33\n\3\46\36\51\33\16\3\t\36\3\33\1\36\b\33\2\3\2\33\n\3\6" +
    "\36\27\3\3\36\1\33\1\3\4\36\60\33\1\36\1\33\3\36\2\33\2\36\5" +
    "\33\2\36\1\33\1\36\1\3\30\36\3\3\2\36\13\33\5\3\2\36\3\33\2" +
    "\3\n\36\6\3\2\36\6\3\2\36\6\3\t\36\7\3\1\36\7\3\221\36\43\33\b" +
    "\3\1\33\2\3\2\33\n\3\6\36\u2ba4\3\f\36\27\3\4\36\61\3\u2104" +
    "\36\u016e\3\2\36\152\3\46\36\7\3\f\36\5\3\5\36\1\33\1\36\n\3\1" +
    "\36\r\3\1\36\5\3\1\36\1\3\1\36\2\3\1\36\2\3\1\36\154\3\41\36\u016b" +
    "\3\22\36\100\3\2\36\66\3\50\36\r\3\3\33\20\3\20\33\7\3\f\36\2" +
    "\3\30\36\3\3\31\36\1\3\6\36\5\3\1\36\207\3\2\33\1\3\4\36\1\3\13" +
    "\33\n\3\7\36\32\3\4\36\1\3\1\36\32\3\13\36\131\3\3\36\6\3\2" +
    "\36\6\3\2\36\6\3\2\36\3\3\3\36\2\3\3\36\2\3\22\33\3\3\4");
    
    /**
     * The transition table of the DFA.
     */
    private static final byte[][] TRANSITION_TABLE = createTransitionTable(
    "\0\1\2\1\0\100\0\102\0\102\3\1\5\1\3\100\0\1\1\1\0\100\0\1\n\2\0\77" +
    "\0\102\0\1\n\2\0\77\13\1\7\1\b\1\13\77\0\1\7\1\6\1\0\77\0\102\0\1\171\101" +
    "\0\1\r\101\0\102\0\102\0\33\26\1\0\2\26\44\0\33\26\1\0\2\26\44\0\102" +
    "\0\102\0\102\0\33\25\1\0\2\25\44\0\33\26\1\0\2\26\44\0\102\27\1\170\1" +
    "\27\2\r\1\22\7\31\1\22\r\25\1\27\1\22\2\26\2\137\1\123\1\113\1\135\1" +
    "\67\1\26\1\'\1\26\1\156\1\122\1\55\1\51\1\26\1\132\1\26\4\34\1\151\1" +
    "\26\1\50\1\63\1\26\3\34\1\77\1\65\1\142\1\121\1\71\1\43\1\34\1\0\62" +
    "\35\1\0\7\35\1\0\5\35\2\0\62\32\1\0\6\24\1\32\1\0\2\24\3\32\2\0\62\32\1" +
    "\0\7\32\1\0\5\32\2\0\f\35\1\0\"\23\1\0\2\34\1\23\1\0\4\40\1\24\1\34\1" +
    "\0\1\40\1\24\3\34\2\0\62\35\1\0\5\40\1\24\1\35\1\0\1\40\1\24\3\35\2" +
    "\0\f\35\1\0\"\23\1\0\2\37\1\23\1\0\4\40\1\24\1\36\1\0\1\40\1\24\3\36\2" +
    "\0\f\35\1\0\45\37\1\0\5\40\1\24\1\37\1\0\1\40\1\24\3\37\2\0\34\33\2" +
    "\0\24\32\1\0\7\32\1\0\5\32\2\0\57\23\1\0\3\23\1\0\f\41\2\0\57\23\1\0\1" +
    "\"\2\23\1\0\1\"\r\0\f\35\1\0\"\23\1\"\1\0\1\37\1\23\1\"\1\0\2\41\1\40\1" +
    "\24\1\36\1\41\1\40\1\24\3\36\2\0\33\26\1\0\2\26\41\21\1\26\2\0\33\26\1" +
    "\0\2\26\25\44\1\26\16\0\33\26\1\0\2\26\36\45\1\26\5\0\33\26\1\0\2\26\r" +
    "\46\1\26\26\0\33\26\1\0\2\26\1\110\1\26\2\44\1\26\7\105\1\26\27\0\33" +
    "\26\1\0\2\26\1\21\1\26\2\21\1\26\4\112\1\26\26\21\1\26\3\0\33\26\1\0\2" +
    "\26\36\21\1\26\5\0\33\26\1\0\2\26\25\52\1\26\16\0\33\26\1\0\2\26\r\53\1" +
    "\26\26\0\33\26\1\0\2\26\r\73\1\26\2\54\1\26\23\0\33\26\1\0\2\26\7\52\1" +
    "\26\34\0\33\26\1\0\2\26\4\56\1\26\37\0\33\26\1\0\2\26\r\57\1\26\26\0\33" +
    "\26\1\0\2\26\13\60\1\26\30\0\33\26\1\0\2\26\4\61\1\26\37\0\33\26\1\0\2" +
    "\26\3\62\1\26\21\167\1\26\16\0\33\26\1\0\2\26\1\52\1\26\"\0\33\26\1" +
    "\0\2\26\25\64\1\103\1\26\r\0\33\26\1\0\2\26\r\64\1\26\26\0\33\26\1\0\2" +
    "\26\27\66\1\26\6\127\1\26\5\0\33\26\1\0\2\26\25\21\1\26\n\20\1\26\3" +
    "\0\33\26\1\0\2\26\36\70\1\26\5\0\33\26\1\0\2\26\20\21\1\26\23\0\33\26\1" +
    "\0\2\26\13\72\1\26\30\0\33\26\1\0\2\26\16\21\1\26\25\0\33\26\1\0\2\26\27" +
    "\74\1\26\f\0\33\26\1\0\2\26\36\75\1\26\5\0\33\26\1\0\2\26\6\76\1\26\35" +
    "\0\33\26\1\0\2\26\13\21\1\26\30\0\33\26\1\0\2\26\17\100\1\26\24\0\33" +
    "\26\1\0\2\26\36\101\1\26\5\0\33\26\1\0\2\26\30\102\1\26\13\0\33\26\1" +
    "\0\2\26\r\100\1\26\26\0\33\26\1\0\2\26\27\104\1\26\f\0\33\26\1\0\2\26\6" +
    "\100\1\26\35\0\33\26\1\0\2\26\36\106\1\26\5\0\33\26\1\0\2\26\1\107\1" +
    "\26\6\144\1\26\33\0\33\26\1\0\2\26\3\106\1\26\40\0\33\26\1\0\2\26\17" +
    "\111\1\26\24\0\33\26\1\0\2\26\3\162\1\26\40\0\33\26\1\0\2\26\b\21\1" +
    "\26\33\0\33\26\1\0\2\26\25\114\1\26\16\0\33\26\1\0\2\26\25\115\1\26\16" +
    "\0\33\26\1\0\2\26\27\116\1\26\f\0\33\26\1\0\2\26\4\117\1\26\37\0\33" +
    "\26\1\0\2\26\3\123\1\26\2\164\1\26\6\120\1\26\26\0\33\26\1\0\2\26\6" +
    "\114\1\26\35\0\33\26\1\0\2\26\6\21\1\26\35\0\33\26\1\0\2\26\4\21\1\26\37" +
    "\0\33\26\1\0\2\26\6\124\1\26\35\0\33\26\1\0\2\26\7\125\1\26\34\0\33" +
    "\26\1\0\2\26\13\126\1\26\30\0\33\26\1\0\2\26\1\21\1\26\"\0\33\26\1\0\2" +
    "\26\1\130\1\26\"\0\33\26\1\0\2\26\27\131\1\26\f\0\33\26\1\0\2\26\36" +
    "\20\1\26\5\0\33\26\1\0\2\26\7\133\1\26\34\0\33\26\1\0\2\26\6\\\1\26\35" +
    "\0\33\26\1\0\2\26\4\133\1\26\37\0\33\26\1\0\2\26\3\136\1\26\40\0\33" +
    "\26\1\0\2\26\1\133\1\26\"\0\33\26\1\0\2\26\25\140\1\26\16\0\33\26\1" +
    "\0\2\26\27\141\1\26\f\0\33\26\1\0\2\26\30\20\1\26\13\0\33\26\1\0\2\26\4" +
    "\143\1\26\37\0\33\26\1\0\2\26\27\20\1\26\f\0\33\26\1\0\2\26\41\145\1" +
    "\26\2\0\33\26\1\0\2\26\35\146\1\26\6\0\33\26\1\0\2\26\t\147\1\26\32" +
    "\0\33\26\1\0\2\26\27\150\1\26\f\0\33\26\1\0\2\26\25\20\1\26\16\0\33" +
    "\26\1\0\2\26\27\152\1\26\f\0\33\26\1\0\2\26\35\153\1\26\6\0\33\26\1" +
    "\0\2\26\3\154\1\26\40\0\33\26\1\0\2\26\25\155\1\26\16\0\33\26\1\0\2" +
    "\26\30\153\1\26\13\0\33\26\1\0\2\26\3\157\1\26\40\0\33\26\1\0\2\26\25" +
    "\160\1\26\16\0\33\26\1\0\2\26\4\161\1\26\6\21\1\26\30\0\33\26\1\0\2" +
    "\26\t\20\1\26\32\0\33\26\1\0\2\26\3\163\1\26\40\0\33\26\1\0\2\26\1\20\1" +
    "\26\"\0\33\26\1\0\2\26\1\165\1\26\"\0\33\26\1\0\2\26\27\166\1\26\f\171\1" +
    "\172\1\f\1\171\77\171\1\17\1\f\1\171\77\0\1\16\1\0\100");
    
    /**
     * Maps state numbers to action numbers.
     */
    private static final byte[] ACTION_MAP = createActionMap(
    "\0\1\1\1\2\1\0\1\2\1\0\1\3\1\4\1\0\2\4\1\0\1\5\1\6\1\7\1" +
    "\b\1\t\1\n\1\13\1\f\1\r\1\16\1\17\1\0\1\n\1\f\1\0\1\13\1" +
    "\f\1\13\1\f\1\0\1\13\3\16\44\t\1\16\57\17\1\0\1\7\1");
    
    //=========================
    // Lexical State Constants
    //=========================
    
    /**
     * The ordinal number of the lexical state "DOC".
     */
    private static final int LEXICAL_STATE_DOC = 0;
    
    /**
     * The ordinal number of the lexical state "STRING".
     */
    private static final int LEXICAL_STATE_STRING = 1;
    
    /**
     * The ordinal number of the lexical state "INITIAL".
     */
    private static final int LEXICAL_STATE_INITIAL = 2;
    
    //===============
    // String Fields
    //===============
    
    /**
     * The current string to be scanned.
     */
    private String string = "";
    
    //===============
    // Region Fields
    //===============
    
    /**
     * The end of the scan region.
     */
    private int regionEnd;
    
    //============
    // Dot Fields
    //============
    
    /**
     * The start position of the next scan.
     */
    private int dot;
    
    //======================
    // Lexical State Fields
    //======================
    
    /**
     * The current lexical state.
     */
    private int lexicalState = LEXICAL_STATE_INITIAL;
    
    //==============
    // Match Fields
    //==============
    
    /**
     * The start of the last match.
     */
    private int matchStart;
    
    /**
     * The end of the last match.
     */
    private int matchEnd;
    
    //===============
    // Helper Fields
    //===============
    
    /**
     * The start state of the DFA.
     */
    private int startState = 23;
    
    //===============
    // Table Methods
    //===============
    
    /**
     * Creates the character map of the scanner.
     * 
     * @param characterMapData The compressed data of the character map.
     * @return The character map of the scanner.
     */
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
    
    /**
     * Creates the transition table of the scanner.
     * 
     * @param transitionTableData The compressed data of the transition table.
     * @return The transition table of the scanner.
     */
    private static byte[][] createTransitionTable(String transitionTableData) {
        byte[][] transitionTable = new byte[122][66];
        int length = transitionTableData.length();
        int i = 0;
        int j = 0;
        int k = 0;
        
        while (i < length) {
            byte curValue = (byte)((short)transitionTableData.charAt(i++) - 1);
            
            for (int x=transitionTableData.charAt(i++);x>0;x--) {
                transitionTable[j][k++] = curValue;
            }
            
            if (k == 66) {
                k = 0;
                j++;
            }
        }
        
        return transitionTable;
    }
    
    /**
     * Creates the action map of the scanner.
     * 
     * @param actionMapData The compressed data of the action map.
     * @return The action map of the scanner.
     */
    private static byte[] createActionMap(String actionMapData) {
        byte[] actionMap = new byte[122];
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
    
    //================
    // String Methods
    //================
    
    /**
     * Sets the string to be scanned. The scan region is set to the entire
     * string.
     * 
     * @param string The new string to be scanned.
     */
    public void setStringInternal(String string) {
        this.string = string != null ? string : "";
        
        regionEnd = this.string.length();
        
        dot = 0;
        lexicalState = LEXICAL_STATE_INITIAL;
        
        matchStart = 0;
        matchEnd = 0;
        
        startState = 23;
    }
    
    //=======================
    // Lexical State Methods
    //=======================
    
    /**
     * Sets the current lexical state.
     * 
     * @param lexicalState The new lexical state.
     * @throws IllegalArgumentException If the specified state is invalid
     */
    private void setLexicalState(int lexicalState) {
        switch(lexicalState) {
        case LEXICAL_STATE_DOC: startState = 3; break;
        case LEXICAL_STATE_STRING: startState = 8; break;
        case LEXICAL_STATE_INITIAL: startState = 23; break;
        default:
            throw new IllegalArgumentException("invalid lexical state");
        }
        
        this.lexicalState = lexicalState;
    }
    
    //===============
    // Match Methods
    //===============
    
    /**
     * Returns the start (inclusive) of the last match.
     * 
     * @return The start (inclusive) of the last match.
     */
    private int getMatchStart() {
        return matchStart;
    }
    
    /**
     * Returns the end (exclusive) of the last match.
     * 
     * @return The end (exclusive) of the last match.
     */
    private int getMatchEnd() {
        return matchEnd;
    }
    
    //==============
    // Scan Methods
    //==============
    
    /**
     * Performs at the current position the next step of the lexical analysis
     * and returns the result.
     * 
     * @return The result of the next step of the lexical analysis.
     * @throws IllegalStateException If a lexical error occurs
     */
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
                case 2: return endString();
                case 3: stringText(); continue;
                case 4: return createLineComment();
                case 5: startDoc(); continue;
                case 6: return createString();
                case 7: return createKeyword();
                case 8: return createKeyword2();
                case 9: return createOperator();
                case 10: return createInteger();
                case 11: return createFloat();
                case 12: return createAnnotation();
                case 13: return createIdentifer();
                case 14: return other();
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
