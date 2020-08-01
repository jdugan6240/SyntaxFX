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
     * Rules:           19                           *
     * Lookaheads:      0                            *
     * Alphabet length: 66                           *
     * NFA states:      367                          *
     * DFA states:      169                          *
     * Static size:     89 KB                        *
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
    "\32\t\3\1\0\1\3\2\0\1\32\16\3\5\22\1\1\1\3\1\36\1\31\1\27\1" +
    "\3\1\7\1\b\1\4\1\34\1\3\1\35\1\16\1\5\1\100\1\101\1\77\6\72\2" +
    "\25\1\r\1\21\1\17\1\20\1\24\1\6\1\61\1\66\1\61\1\71\1\67\1\70\1" +
    "\33\5\57\1\33\13\60\1\33\2\13\1\2\1\f\1\30\1\36\1\3\1\64\1\73\1" +
    "\65\1\76\1\74\1\75\1\41\1\53\1\44\1\33\1\40\1\62\1\55\1\51\1" +
    "\43\1\37\1\33\1\46\1\50\1\'\1\45\1\"\1\56\1\63\1\52\1\54\1\t\1" +
    "\26\1\n\1\23\1\32\41\3\2\36\4\3\4\36\1\3\2\32\1\3\7\36\1\3\4" +
    "\36\1\3\5\36\27\3\1\36\37\3\1\36\u01ca\3\4\36\f\3\16\36\5\3\7" +
    "\36\1\3\1\36\1\3\21\32\160\36\5\3\1\36\2\3\2\36\4\3\b\36\1\3\1" +
    "\36\3\3\1\36\1\3\1\36\24\3\1\36\123\3\1\36\213\3\1\32\5\3\2" +
    "\36\236\3\t\36\46\3\2\36\1\3\7\36\'\3\7\36\1\3\1\32\55\3\1\32\1" +
    "\3\1\32\2\3\1\32\2\3\1\32\1\3\b\36\33\3\5\36\3\3\r\32\5\3\6" +
    "\36\1\3\4\32\13\3\5\36\53\32\37\3\4\36\2\32\1\36\143\3\1\36\1" +
    "\32\b\3\1\32\6\36\2\32\2\3\1\32\4\36\2\32\n\36\3\3\2\36\1\3\17" +
    "\32\1\36\1\32\1\36\36\32\33\3\2\36\131\32\13\36\1\3\16\32\n" +
    "\36\41\32\t\36\2\3\4\36\1\3\5\36\26\32\4\36\1\32\t\36\1\32\3" +
    "\36\1\32\5\3\22\36\31\32\3\3\104\36\1\3\1\36\13\3\67\32\33\3\1" +
    "\32\4\36\66\32\3\36\1\32\22\36\1\32\7\36\n\32\2\3\2\32\n\3\1" +
    "\36\7\3\1\36\7\3\1\32\3\3\1\36\b\3\2\36\2\3\2\36\26\3\1\36\7" +
    "\3\1\36\1\3\3\36\4\3\2\32\1\36\1\32\7\3\2\32\2\3\2\32\3\36\1" +
    "\3\b\32\1\3\4\36\2\3\1\36\3\32\2\3\2\32\n\36\4\3\7\36\1\3\5" +
    "\32\3\3\1\36\6\3\4\36\2\3\2\36\26\3\1\36\7\3\1\36\2\3\1\36\2" +
    "\3\1\36\2\3\2\32\1\3\1\32\5\3\4\32\2\3\2\32\3\3\3\32\1\3\7\36\4" +
    "\3\1\36\1\3\7\32\f\36\3\32\1\3\13\32\3\3\1\36\t\3\1\36\3\3\1" +
    "\36\26\3\1\36\7\3\1\36\2\3\1\36\5\3\2\32\1\36\1\32\b\3\1\32\3" +
    "\3\1\32\3\3\2\36\1\3\17\36\2\32\2\3\2\32\n\3\1\36\1\3\17\32\3" +
    "\3\1\36\b\3\2\36\2\3\2\36\26\3\1\36\7\3\1\36\2\3\1\36\5\3\2" +
    "\32\1\36\1\32\7\3\2\32\2\3\2\32\3\3\b\32\2\3\4\36\2\3\1\36\3" +
    "\32\2\3\2\32\n\3\1\36\1\3\20\32\1\36\1\3\1\36\6\3\3\36\3\3\1" +
    "\36\4\3\3\36\2\3\1\36\1\3\1\36\2\3\3\36\2\3\3\36\3\3\3\36\f" +
    "\3\4\32\5\3\3\32\3\3\1\32\4\3\2\36\1\3\6\32\1\3\16\32\n\3\t" +
    "\36\1\3\7\32\3\3\1\36\b\3\1\36\3\3\1\36\27\3\1\36\n\3\1\36\5" +
    "\3\3\36\1\32\7\3\1\32\3\3\1\32\4\3\7\32\2\3\1\36\2\3\6\36\2" +
    "\32\2\3\2\32\n\3\22\32\2\3\1\36\b\3\1\36\3\3\1\36\27\3\1\36\n" +
    "\3\1\36\5\3\2\32\1\36\1\32\7\3\1\32\3\3\1\32\4\3\7\32\2\3\7" +
    "\36\1\3\1\36\2\32\2\3\2\32\n\3\1\36\2\3\17\32\2\3\1\36\b\3\1" +
    "\36\3\3\1\36\51\3\2\36\1\32\7\3\1\32\3\3\1\32\4\36\1\3\b\32\1" +
    "\3\b\36\2\32\2\3\2\32\n\3\n\36\6\3\2\32\2\3\1\36\22\3\3\36\30" +
    "\3\1\36\t\3\1\36\1\3\2\36\7\3\3\32\1\3\4\32\6\3\1\32\1\3\1\32\b" +
    "\3\22\32\2\3\r\36\60\32\1\36\2\32\7\3\4\36\b\32\b\3\1\32\n\3\'" +
    "\36\2\3\1\36\1\3\2\36\2\3\1\36\1\3\2\36\1\3\6\36\4\3\1\36\7" +
    "\3\1\36\3\3\1\36\1\3\1\36\1\3\2\36\2\3\1\36\4\32\1\36\2\32\6" +
    "\3\1\32\2\36\1\3\2\36\5\3\1\36\1\3\1\32\6\3\2\32\n\3\2\36\4" +
    "\3\40\36\1\3\27\32\2\3\6\32\n\3\13\32\1\3\1\32\1\3\1\32\1\3\4" +
    "\32\2\36\b\3\1\36\44\3\4\32\24\3\1\32\2\36\5\32\13\3\1\32\44" +
    "\3\t\32\1\3\71\36\53\32\24\36\1\32\n\3\6\36\6\32\4\36\4\32\3" +
    "\36\1\32\3\36\2\32\7\36\3\32\4\36\r\32\f\36\1\32\17\3\2\36\46" +
    "\3\1\36\1\3\5\36\1\3\2\36\53\3\1\36\u014d\3\1\36\4\3\2\36\7" +
    "\3\1\36\1\3\1\36\4\3\2\36\51\3\1\36\4\3\2\36\41\3\1\36\4\3\2" +
    "\36\7\3\1\36\1\3\1\36\4\3\2\36\17\3\1\36\71\3\1\36\4\3\2\36\103" +
    "\3\2\32\3\3\40\36\20\3\20\36\125\3\f\36\u026c\3\2\36\21\3\1" +
    "\36\32\3\5\36\113\3\3\36\3\3\17\36\r\3\1\36\4\32\3\3\13\36\22" +
    "\32\3\3\13\36\22\32\2\3\f\36\r\3\1\36\3\3\1\32\2\3\f\36\64\32\40" +
    "\3\3\36\1\3\3\36\2\32\1\3\2\32\n\3\41\32\3\3\2\32\n\3\6\36\130" +
    "\3\b\36\51\32\1\36\1\3\5\36\106\3\n\36\35\3\3\32\f\3\4\32\f" +
    "\3\n\32\n\36\36\3\2\36\5\3\13\36\54\3\4\32\21\36\7\32\2\3\6" +
    "\32\n\3\46\36\27\32\5\3\4\36\65\32\n\3\1\32\35\3\2\32\13\3\6" +
    "\32\n\3\r\36\1\3\130\32\5\36\57\32\21\36\7\3\4\32\n\3\21\32\t" +
    "\3\f\32\3\36\36\32\r\36\2\32\n\36\54\32\16\3\f\36\44\32\24\3\b" +
    "\32\n\3\3\36\3\32\n\36\44\3\122\32\3\3\1\32\25\36\4\32\1\36\4" +
    "\32\3\36\2\3\t\36\300\32\'\3\25\32\4\36\u0116\3\2\36\6\3\2\36\46" +
    "\3\2\36\6\3\2\36\b\3\1\36\1\3\1\36\1\3\1\36\1\3\1\36\37\3\2" +
    "\36\65\3\1\36\7\3\1\36\1\3\3\36\3\3\1\36\7\3\3\36\4\3\2\36\6" +
    "\3\4\36\r\3\5\36\3\3\1\36\7\3\16\32\5\3\32\32\5\3\20\36\2\3\23" +
    "\36\1\3\13\32\5\3\5\32\6\3\1\36\1\3\r\36\1\3\20\36\r\3\3\36\33" +
    "\3\25\32\r\3\4\32\1\3\3\32\f\3\21\36\1\3\4\36\1\3\2\36\n\3\1" +
    "\36\1\3\3\36\5\3\6\36\1\3\1\36\1\3\1\36\1\3\1\36\4\3\1\36\13" +
    "\3\2\36\4\3\5\36\5\3\4\36\1\3\21\36\51\3\u0a77\36\57\3\1\36\57" +
    "\3\1\36\205\3\6\36\4\32\3\36\2\3\f\36\46\3\1\36\1\3\5\36\1\3\2" +
    "\36\70\3\7\36\1\3\17\32\1\36\27\3\t\36\7\3\1\36\7\3\1\36\7\3\1" +
    "\36\7\3\1\36\7\3\1\36\7\3\1\36\7\3\1\36\7\3\1\32\40\3\57\36\1" +
    "\3\u01d5\36\3\3\31\36\t\32\6\3\1\36\5\3\2\36\5\3\4\36\126\3\2" +
    "\32\2\3\2\36\3\3\1\36\132\3\1\36\4\3\5\36\51\3\3\36\136\3\21" +
    "\36\33\3\65\36\20\3\u0200\36\u19b6\3\112\36\u51cd\3\63\36\u048d" +
    "\3\103\36\56\3\2\36\u010d\3\3\36\20\32\n\36\2\3\24\36\57\32\1" +
    "\3\4\32\n\3\1\36\31\3\7\32\1\36\120\32\2\3\45\36\t\3\2\36\147" +
    "\3\2\36\4\3\1\36\4\3\f\36\13\3\115\36\n\32\1\36\3\32\1\36\4" +
    "\32\1\36\27\32\5\3\20\36\1\3\7\36\64\3\f\32\2\36\62\32\21\3\13" +
    "\32\n\3\6\32\22\36\6\3\3\36\1\3\4\32\n\36\34\32\b\3\2\36\27" +
    "\32\r\3\f\36\35\3\3\32\4\36\57\32\16\3\16\36\1\32\n\3\46\36\51" +
    "\32\16\3\t\36\3\32\1\36\b\32\2\3\2\32\n\3\6\36\27\3\3\36\1\32\1" +
    "\3\4\36\60\32\1\36\1\32\3\36\2\32\2\36\5\32\2\36\1\32\1\36\1" +
    "\3\30\36\3\3\2\36\13\32\5\3\2\36\3\32\2\3\n\36\6\3\2\36\6\3\2" +
    "\36\6\3\t\36\7\3\1\36\7\3\221\36\43\32\b\3\1\32\2\3\2\32\n\3\6" +
    "\36\u2ba4\3\f\36\27\3\4\36\61\3\u2104\36\u016e\3\2\36\152\3\46" +
    "\36\7\3\f\36\5\3\5\36\1\32\1\36\n\3\1\36\r\3\1\36\5\3\1\36\1" +
    "\3\1\36\2\3\1\36\2\3\1\36\154\3\41\36\u016b\3\22\36\100\3\2" +
    "\36\66\3\50\36\r\3\3\32\20\3\20\32\7\3\f\36\2\3\30\36\3\3\31" +
    "\36\1\3\6\36\5\3\1\36\207\3\2\32\1\3\4\36\1\3\13\32\n\3\7\36\32" +
    "\3\4\36\1\3\1\36\32\3\13\36\131\3\3\36\6\3\2\36\6\3\2\36\6\3\2" +
    "\36\3\3\3\36\2\3\3\36\2\3\22\32\3\3\4");
    
    /**
     * The transition table of the DFA.
     */
    private static final short[][] TRANSITION_TABLE = createTransitionTable(
    "\3\4\4\1\3\75\0\102\0\102\0\5\2\1\0\74\6\33\7\1\6\3\7\33\6\1\7\4\6\3" +
    "\0\102\0\102\13\4\f\1\13\1\n\1\13\73\0\102\0\102\0\102\0\5\t\1\0\74\0\1" +
    "\250\101\0\1\16\101\0\102\0\4\17\1\0\75\0\102\0\32\30\2\0\2\30\44\0\32" +
    "\30\2\0\2\30\44\0\102\0\102\0\102\0\32\27\2\0\2\27\44\0\32\30\2\0\2\30\44" +
    "\0\102\31\1\247\1\31\2\24\1\251\1\27\1\24\7\33\1\24\13\31\1\30\1\24\2" +
    "\30\1\160\1\30\1\\\1\162\1\30\1\46\1\30\1\113\1\63\1\77\1\66\1\30\4\52\1" +
    "\30\5\126\1\54\1\30\4\36\1\140\1\55\1\106\1\122\1\36\1\45\1\36\1\0\72" +
    "\37\1\0\4\37\3\0\70\26\2\34\1\0\2\26\2\34\3\0\72\34\1\0\4\34\3\0\16\37\1" +
    "\0\40\25\1\0\2\25\1\0\4\"\1\26\2\36\1\0\1\"\1\26\2\36\3\0\67\"\1\26\2" +
    "\37\1\0\1\"\1\26\2\37\3\0\16\37\1\0\40\25\1\0\2\25\1\0\4\"\1\26\2\41\1" +
    "\0\1\"\1\26\2\40\3\0\16\37\1\0\50\"\1\26\2\41\1\0\1\"\1\26\2\41\3\0\34" +
    "\35\2\0\34\34\1\0\4\34\3\0\57\25\1\0\2\25\1\0\r\43\2\0\57\25\1\0\1\44\1" +
    "\25\1\0\1\44\16\0\16\37\1\0\40\25\1\44\1\0\1\25\1\44\1\0\2\43\1\"\1\26\2" +
    "\41\1\43\1\"\1\26\2\40\3\0\32\30\2\0\2\30\17\130\1\30\17\23\1\30\4\0\32" +
    "\30\2\0\2\30\36\23\1\30\5\0\32\30\2\0\2\30\24\'\1\30\17\0\32\30\2\0\2" +
    "\30\6\50\1\30\35\0\32\30\2\0\2\30\r\51\1\30\26\0\32\30\2\0\2\30\n\'\1" +
    "\30\31\0\32\30\2\0\2\30\5\62\1\30\16\221\1\30\1\74\1\30\r\0\32\30\2\0\2" +
    "\30\24\53\1\217\1\30\16\0\32\30\2\0\2\30\7\'\1\30\34\0\32\30\2\0\2\30\13" +
    "\56\1\30\30\0\32\30\2\0\2\30\6\57\1\30\35\0\32\30\2\0\2\30\t\60\1\227\1" +
    "\30\31\0\32\30\2\0\2\30\13\61\1\30\30\0\32\30\2\0\2\30\b\100\1\30\4\71\1" +
    "\30\26\0\32\30\2\0\2\30\24\23\1\30\17\0\32\30\2\0\2\30\24\64\1\30\17" +
    "\0\32\30\2\0\2\30\7\65\1\30\16\177\1\30\r\0\32\30\2\0\2\30\20\213\1\30\23" +
    "\0\32\30\2\0\2\30\5\67\1\30\36\0\32\30\2\0\2\30\6\114\1\30\1\70\1\30\33" +
    "\0\32\30\2\0\2\30\r\23\1\30\26\0\32\30\2\0\2\30\27\72\1\30\f\0\32\30\2" +
    "\0\2\30\t\73\1\'\1\30\31\0\32\30\2\0\2\30\t\73\1\30\32\0\32\30\2\0\2" +
    "\30\6\75\1\30\35\0\32\30\2\0\2\30\t\211\1\30\2\152\1\30\3\76\1\30\23" +
    "\0\32\30\2\0\2\30\7\'\1\30\4\23\1\30\t\241\1\30\r\0\32\30\2\0\2\30\f" +
    "\23\1\30\27\0\32\30\2\0\2\30\24\101\1\30\17\0\32\30\2\0\2\30\24\102\1" +
    "\30\17\0\32\30\2\0\2\30\26\103\1\30\r\0\32\30\2\0\2\30\13\104\1\30\30" +
    "\0\32\30\2\0\2\30\5\131\1\105\1\30\17\171\1\30\r\0\32\30\2\0\2\30\13" +
    "\23\1\30\30\0\32\30\2\0\2\30\b\107\1\30\33\0\32\30\2\0\2\30\7\110\1\30\34" +
    "\0\32\30\2\0\2\30\t\111\1\30\32\0\32\30\2\0\2\30\36\112\1\30\5\0\32\30\2" +
    "\0\2\30\n\23\1\30\31\0\32\30\2\0\2\30\t\23\1\30\32\0\32\30\2\0\2\30\24" +
    "\115\1\30\17\0\32\30\2\0\2\30\7\116\1\30\34\0\32\30\2\0\2\30\26\117\1" +
    "\30\r\0\32\30\2\0\2\30\37\120\1\30\4\0\32\30\2\0\2\30\5\23\1\30\30\121\1" +
    "\30\5\0\32\30\2\0\2\30\b\115\1\30\33\0\32\30\2\0\2\30\36\123\1\30\5\0\32" +
    "\30\2\0\2\30\n\124\1\30\31\0\32\30\2\0\2\30\n\125\1\30\22\234\1\30\6" +
    "\0\32\30\2\0\2\30\5\123\1\30\16\226\1\30\17\0\32\30\2\0\2\30\1\127\1" +
    "\30\"\0\32\30\2\0\2\30\b\23\1\30\33\0\32\30\2\0\2\30\5\23\1\30\36\0\32" +
    "\30\2\0\2\30\t\132\1\30\32\0\32\30\2\0\2\30\5\133\1\30\36\0\32\30\2\0\2" +
    "\30\2\23\1\30\41\0\32\30\2\0\2\30\26\135\1\30\r\0\32\30\2\0\2\30\36\136\1" +
    "\30\5\0\32\30\2\0\2\30\b\137\1\30\33\0\32\30\2\0\2\30\40\22\1\30\3\0\32" +
    "\30\2\0\2\30\36\141\1\30\5\0\32\30\2\0\2\30\16\142\1\30\25\0\32\30\2" +
    "\0\2\30\6\143\1\30\35\0\32\30\2\0\2\30\13\144\1\30\30\0\32\30\2\0\2\30\5" +
    "\145\1\30\36\0\32\30\2\0\2\30\b\146\1\30\33\0\32\30\2\0\2\30\r\147\1" +
    "\30\26\0\32\30\2\0\2\30\27\150\1\30\f\0\32\30\2\0\2\30\13\151\1\30\30" +
    "\0\32\30\2\0\2\30\t\142\1\30\32\0\32\30\2\0\2\30\27\153\1\30\f\0\32\30\2" +
    "\0\2\30\36\154\1\30\5\0\32\30\2\0\2\30\t\155\1\30\32\0\32\30\2\0\2\30\5" +
    "\156\1\174\1\30\35\0\32\30\2\0\2\30\7\207\1\157\1\30\r\203\1\30\r\0\32" +
    "\30\2\0\2\30\6\141\1\30\r\167\1\30\17\0\32\30\2\0\2\30\5\161\1\30\36" +
    "\0\32\30\2\0\2\30\36\22\1\30\5\0\32\30\2\0\2\30\24\163\1\30\17\0\32\30\2" +
    "\0\2\30\6\164\1\30\35\0\32\30\2\0\2\30\t\165\1\30\32\0\32\30\2\0\2\30\26" +
    "\166\1\30\r\0\32\30\2\0\2\30\n\163\1\30\31\0\32\30\2\0\2\30\24\170\1" +
    "\30\17\0\32\30\2\0\2\30\t\163\1\30\32\0\32\30\2\0\2\30\26\172\1\30\r" +
    "\0\32\30\2\0\2\30\4\173\1\30\37\0\32\30\2\0\2\30\4\163\1\30\37\0\32\30\2" +
    "\0\2\30\6\175\1\30\35\0\32\30\2\0\2\30\t\176\1\30\32\0\32\30\2\0\2\30\3" +
    "\163\1\30\40\0\32\30\2\0\2\30\26\200\1\30\r\0\32\30\2\0\2\30\2\201\1" +
    "\30\41\0\32\30\2\0\2\30\27\202\1\30\f\0\32\30\2\0\2\30\27\22\1\30\f\0\32" +
    "\30\2\0\2\30\6\204\1\30\35\0\32\30\2\0\2\30\24\205\1\30\17\0\32\30\2" +
    "\0\2\30\35\206\1\30\6\0\32\30\2\0\2\30\t\205\1\30\32\0\32\30\2\0\2\30\b" +
    "\246\1\30\r\210\1\30\r\0\32\30\2\0\2\30\n\22\1\30\31\0\32\30\2\0\2\30\n" +
    "\22\1\30\31\0\32\30\2\0\2\30\40\212\1\30\3\0\32\30\2\0\2\30\13\214\1" +
    "\30\30\0\32\30\2\0\2\30\36\215\1\30\5\0\32\30\2\0\2\30\t\216\1\30\32" +
    "\0\32\30\2\0\2\30\n\212\1\30\31\0\32\30\2\0\2\30\26\220\1\30\r\0\32\30\2" +
    "\0\2\30\t\212\1\30\32\0\32\30\2\0\2\30\13\222\1\30\30\0\32\30\2\0\2\30\36" +
    "\223\1\30\5\0\32\30\2\0\2\30\17\224\1\30\24\0\32\30\2\0\2\30\36\225\1" +
    "\30\5\0\32\30\2\0\2\30\t\22\1\30\32\0\32\30\2\0\2\30\27\227\1\30\f\0\32" +
    "\30\2\0\2\30\26\230\1\30\r\0\32\30\2\0\2\30\b\231\1\30\33\0\32\30\2\0\2" +
    "\30\t\232\1\30\32\0\32\30\2\0\2\30\n\233\1\30\31\0\32\30\2\0\2\30\13" +
    "\227\1\30\30\0\32\30\2\0\2\30\36\235\1\30\5\0\32\30\2\0\2\30\6\236\1" +
    "\30\35\0\32\30\2\0\2\30\n\237\1\30\31\0\32\30\2\0\2\30\13\240\1\30\30" +
    "\0\32\30\2\0\2\30\1\22\1\30\"\0\32\30\2\0\2\30\37\242\1\30\4\0\32\30\2" +
    "\0\2\30\t\243\1\30\32\0\32\30\2\0\2\30\27\244\1\30\f\0\32\30\2\0\2\30\6" +
    "\245\1\30\35\250\1\21\1\r\1\250\77\250\1\21\1\r\1\250\77\0\4\20\1\16\1" +
    "\0\74");
    
    /**
     * Maps state numbers to action numbers.
     */
    private static final byte[] ACTION_MAP = createActionMap(
    "\0\1\1\1\2\2\0\1\3\1\4\1\0\1\5\1\6\1\7\2\0\1\b\1\t\1\n\1" +
    "\13\1\f\1\r\1\16\1\17\1\20\1\21\1\22\1\23\1\0\1\16\1\20\1" +
    "\0\1\17\1\20\1\17\1\20\1\0\1\17\3\22\145\r\1\22\33\23\1" +
    "\0\1\16\1");
    
    //=========================
    // Lexical State Constants
    //=========================
    
    /**
     * The ordinal number of the lexical state "COMMENT".
     */
    private static final int LEXICAL_STATE_COMMENT = 0;
    
    /**
     * The ordinal number of the lexical state "DOCTAG".
     */
    private static final int LEXICAL_STATE_DOCTAG = 1;
    
    /**
     * The ordinal number of the lexical state "DOC".
     */
    private static final int LEXICAL_STATE_DOC = 2;
    
    /**
     * The ordinal number of the lexical state "INITIAL".
     */
    private static final int LEXICAL_STATE_INITIAL = 3;
    
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
    private int startState = 25;
    
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
    private static short[][] createTransitionTable(String transitionTableData) {
        short[][] transitionTable = new short[169][66];
        int length = transitionTableData.length();
        int i = 0;
        int j = 0;
        int k = 0;
        
        while (i < length) {
            short curValue = (short)((short)transitionTableData.charAt(i++) - 1);
            
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
        byte[] actionMap = new byte[169];
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
        
        startState = 25;
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
        case LEXICAL_STATE_COMMENT: startState = 0; break;
        case LEXICAL_STATE_DOCTAG: startState = 4; break;
        case LEXICAL_STATE_DOC: startState = 7; break;
        case LEXICAL_STATE_INITIAL: startState = 25; break;
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
                case 11: return createKeyword();
                case 12: return createKeyword2();
                case 13: return createOperator();
                case 14: return createInteger();
                case 15: return createFloat();
                case 16: return createAnnotation();
                case 17: return createIdentifer();
                case 18: return other();
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
