package syntaxfx.lexers;

import syntaxfx.Lexer;
import syntaxfx.Token;

import java.util.Stack;

/**
 * @option functionality = all- setString+ getMatchStart+ getMatchEnd+ lexicalState+
 * @option visibility = all- stringMethods+ scanMethods+
 * @option internal = setString+
 *
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
        return new Token(Token.DOC_COMMENT, temp, matchStart - 1);
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
    
    /** @expr package|void|public|protected|private|false|abstract|native|strictfp|synchronized|transient|volatile|const|extends|implements|throws|class */
    Token createKeyword() { return new Token(Token.KEYWORD, matchStart, matchEnd); }

    /** @expr import|if|else|switch|case|default|for|while|do|continue|break|return|goto|try|catch|finally|throw|assert|true|false|null|this*/
    Token createKeyword2() { return new Token(Token.KEYWORD2, matchStart, matchEnd); }

    /** @expr \(|\)|\{|\}|\[|\]|\;|\.|\=|\>|\<|\!|\~|\?|\:|\||\&|\^|\%|\+|\-|\*|\/ */
    Token createOperator() { return new Token(Token.OPERATOR, matchStart, matchEnd); }

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
     * Rules:           17                           *
     * Lookaheads:      0                            *
     * Alphabet length: 53                           *
     * NFA states:      305                          *
     * DFA states:      159                          *
     * Static size:     20 KB                        *
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
    "\4\n\0\1\4\2\0\1\4\23\53\1\3\1\4\2\62\1\60\1\4\1\40\1\41\1\35\1" +
    "\63\1\4\1\64\1\'\1\36\1\4\n\56\1\46\1\52\1\50\1\51\1\55\1\37\1" +
    "\1\32\44\1\2\1\45\1\61\1\4\2\6\1\20\1\7\1\16\1\n\1\24\1\t\1" +
    "\30\1\r\1\1\1\b\1\21\1\33\1\26\1\f\1\5\1\1\1\22\1\25\1\23\1" +
    "\17\1\13\1\34\1\32\1\27\1\31\1\"\1\57\1\43\1\54\1");
    
    /**
     * The transition table of the DFA.
     */
    private static final short[][] TRANSITION_TABLE = createTransitionTable(
    "\3\35\4\1\3\27\0\65\0\65\0\36\2\1\0\26\6\1\7\1\6\3\7\30\6\30\0\65\0\65" +
    "\13\35\f\1\13\1\n\1\13\25\0\65\0\65\0\65\0\36\t\1\0\26\0\2\21\2\0\61" +
    "\0\65\0\2\21\2\0\61\22\2\17\1\16\1\22\61\0\2\r\1\16\1\0\61\0\65\0\1\236\64" +
    "\0\1\24\64\0\65\0\35\25\1\0\27\0\65\0\65\0\65\0\65\0\65\33\3\235\1\33\1" +
    "\201\1\72\1\46\1\33\1\106\1\112\1\162\1\33\1\63\1\77\1\33\1\127\1\33\1" +
    "\61\1\40\1\54\1\45\1\103\1\33\5\115\1\32\1\237\1\33\1\32\25\0\34\131\1" +
    "\0\30\0\f\35\1\0\50\0\r\62\1\0\4\36\1\0\"\0\22\116\1\0\5\37\1\0\34\0\30" +
    "\31\1\0\34\0\7\41\1\0\55\0\23\"\1\0\41\0\r\43\1\0\'\0\23\234\1\0\3\173\1" +
    "\0\4\44\1\0\30\0\6\110\1\0\5\123\1\0\4\133\1\0\43\0\27\31\1\0\35\0\21" +
    "\'\1\0\43\0\21\50\1\0\43\0\6\51\1\0\56\0\26\52\1\0\36\0\6\204\1\0\5\100\1" +
    "\53\1\0\'\0\26\31\1\0\36\0\22\55\1\0\"\0\17\56\1\0\45\0\23\57\1\0\41" +
    "\0\n\60\1\0\52\0\25\31\1\0\37\0\24\31\1\0\6\67\1\0\31\0\23\31\1\0\41" +
    "\0\22\64\1\0\"\0\f\65\1\0\4\140\1\0\43\0\5\66\1\0\57\0\n\65\1\0\52\0\25" +
    "\70\1\0\37\0\20\157\1\0\4\71\1\0\37\0\21\64\1\0\43\0\17\73\1\0\45\0\6" +
    "\74\1\0\56\0\24\75\1\0\40\0\n\76\1\0\1\31\1\0\50\0\22\31\1\0\"\0\21\31\1" +
    "\0\43\0\21\101\1\0\43\0\6\216\1\0\b\102\1\0\45\0\f\31\1\0\50\0\23\104\1" +
    "\0\41\0\f\105\1\0\50\0\n\31\1\0\52\0\23\"\1\0\1\107\1\0\37\0\25\107\1" +
    "\0\37\0\21\111\1\0\b\144\1\0\32\0\21\107\1\0\43\0\r\113\1\0\'\0\30\114\1" +
    "\0\34\0\6\152\1\0\b\107\1\0\7\31\1\0\35\0\17\107\1\0\45\0\26\117\1\0\36" +
    "\0\r\120\1\0\'\0\23\121\1\0\1\145\1\0\37\0\26\122\1\0\36\0\b\31\1\0\54" +
    "\0\6\124\1\0\56\0\n\125\1\0\52\0\22\126\1\0\"\0\25\30\1\0\37\0\25\30\1" +
    "\0\37\0\25\130\1\0\37\0\6\132\1\0\56\0\23\130\1\0\41\0\26\\\1\0\36\0\n" +
    "\135\1\0\52\0\33\136\1\0\31\0\n\137\1\0\52\0\16\130\1\0\46\0\26\141\1" +
    "\0\36\0\n\142\1\0\52\0\23\143\1\0\41\0\23\30\1\0\41\0\26\145\1\0\36\0\n" +
    "\146\1\0\52\0\r\147\1\0\'\0\25\150\1\0\37\0\26\151\1\0\36\0\7\145\1\0\55" +
    "\0\6\153\1\0\56\0\22\154\1\0\"\0\23\155\1\0\41\0\25\156\1\0\37\0\16\30\1" +
    "\0\46\0\r\160\1\0\3\213\1\0\43\0\f\161\1\0\50\0\n\160\1\0\52\0\31\163\1" +
    "\0\33\0\r\164\1\0\'\0\26\165\1\0\36\0\f\166\1\0\50\0\22\167\1\0\"\0\30" +
    "\170\1\0\34\0\7\171\1\0\55\0\26\172\1\0\36\0\23\163\1\0\41\0\7\174\1" +
    "\0\55\0\n\175\1\0\52\0\23\176\1\0\41\0\f\177\1\207\1\0\'\0\6\222\1\0\b" +
    "\226\1\0\2\200\1\0\"\0\n\30\1\0\52\0\25\202\1\0\37\0\21\203\1\0\43\0\23" +
    "\202\1\0\41\0\6\205\1\0\56\0\13\206\1\0\51\0\21\202\1\0\43\0\r\210\1" +
    "\0\'\0\23\211\1\0\41\0\6\212\1\0\56\0\13\202\1\0\51\0\r\214\1\0\'\0\23" +
    "\215\1\0\41\0\t\202\1\0\53\0\6\217\1\0\56\0\b\220\1\0\54\0\7\221\1\0\55" +
    "\0\7\30\1\0\55\0\r\223\1\0\'\0\21\224\1\0\43\0\20\225\1\0\44\0\5\30\1" +
    "\0\57\0\24\227\1\0\40\0\23\230\1\0\41\0\7\231\1\0\55\0\r\232\1\0\'\0\22" +
    "\233\1\0\"\236\2\23\1\27\1\236\61\236\2\23\1\27\1\236\61\0\35\26\1\24\1" +
    "\0\26");
    
    /**
     * Maps state numbers to action numbers.
     */
    private static final byte[] ACTION_MAP = createActionMap(
    "\0\1\1\1\2\2\0\1\3\1\4\1\0\1\5\1\6\1\7\2\0\1\b\1\t\1\0\2" +
    "\t\1\0\1\n\1\13\1\f\1\r\1\16\1\17\1\20\1\21\1\0\4\21\1" +
    "\0\4\21\2\0\5\21\1\0\4\21\1\0\1\21\1\0\6\21\1\0\4\21\1" +
    "\0\3\21\1\0\2\21\1\0\3\21\1\0\2\21\1\0\t\21\1\0\1\17\1" +
    "\0\30\21\1\0\16\21\1\0\33\21\1\0\1\20\1");
    
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
     * The ordinal number of the lexical state "STRING".
     */
    private static final int LEXICAL_STATE_STRING = 3;
    
    /**
     * The ordinal number of the lexical state "INITIAL".
     */
    private static final int LEXICAL_STATE_INITIAL = 4;
    
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
    private int startState = 27;
    
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
        byte[] characterMap = new byte[127];
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
        short[][] transitionTable = new short[159][53];
        int length = transitionTableData.length();
        int i = 0;
        int j = 0;
        int k = 0;
        
        while (i < length) {
            short curValue = (short)((short)transitionTableData.charAt(i++) - 1);
            
            for (int x=transitionTableData.charAt(i++);x>0;x--) {
                transitionTable[j][k++] = curValue;
            }
            
            if (k == 53) {
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
        byte[] actionMap = new byte[159];
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
        
        startState = 27;
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
        case LEXICAL_STATE_STRING: startState = 15; break;
        case LEXICAL_STATE_INITIAL: startState = 27; break;
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
                char curChar = string.charAt(iterator);
                
                curState = TRANSITION_TABLE[curState][curChar >= 127 ?
                        4 : CHARACTER_MAP[curChar]];
                
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
                case 7: return endString();
                case 8: stringText(); continue;
                case 9: return createLineComment();
                case 10: startDoc(); continue;
                case 11: startMultilineComment(); continue;
                case 12: return createString();
                case 13: return createKeyword();
                case 14: return createKeyword2();
                case 15: return createOperator();
                case 16: return other();
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
