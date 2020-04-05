package syntaxfx.lexers;

import syntaxfx.Lexer;
import syntaxfx.Token;


/**
 * The JavaLexer class is a lexer that supports the Java programming language.
 *
 * @option functionality = all- setString+ getMatchTextRange+
 *
 * @option visibility = all- stringMethods+ scanMethods+ 
 *
 * @macro LineTerminator = \r | \n | \r\n
 * @macro DecimalInteger = 0 | [1-9][0-9]* 
 * @macro HexInteger     = 0 [xX] [0-9a-fA-F]* 
 * @macro OctalInteger   = 0 [0-7]+
 * @macro BinaryInteger  = 0 [bB] [01]* 
 * @macro FloatNumber    = (([0-9]+\.[0-9]*) | (\.?[0-9]+)) ([eE][+-]?[0-9]+)?
 * @macro CommentClosure = !([^]* [*][/] [^]*) ([*][/])?
 *
 * @author James Dugan
 */
public class JavaLexer extends Lexer {
    
    public JavaLexer() {}

    public JavaLexer(String code) {
        setString(code);
    }

    //"Declarative" keywords
    /** @expr package|void|public|protected|private|static|final|abstract|native|strictfp|synchronized|transient|volatile|const|extends|implements|throws */
    Token createDeclarativeKeyword() {
        return new Token(Token.KEYWORD, matchStart, matchEnd);
    }

    //"Operative" keywords
    /** @expr import|if|else|switch|case|default|for|while|do|break|continue|return|goto|try|catch|finally|throw|assert */
    Token createOperativeKeyword() {
        return new Token(Token.KEYWORD2, matchStart, matchEnd);
    }

    //Anything that isn't already caught by the lexer
    /** @expr [^] */
    Token createOtherToken() {
        return new Token(Token.OTHER, matchStart, matchEnd);
    }

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
     * Rules:           3                            *
     * Lookaheads:      0                            *
     * Alphabet length: 25                           *
     * NFA states:      216                          *
     * DFA states:      126                          *
     * Static size:     6 KB                         *
     * Instance size:   24 Bytes                     *
     *                                               *
     ************************************************/
    
    //=================
    // Table Constants
    //=================
    
    /**
     * Maps Unicode characters to DFA input symbols.
     */
    private static final byte[] CHARACTER_MAP = createCharacterMap(
    "\0\141\2\1\f\1\3\1\n\1\6\1\21\1\5\1\24\1\t\1\0\1\4\1\r\1\27\1" +
    "\22\1\b\1\1\1\0\1\16\1\20\1\17\1\13\1\7\1\30\1\26\1\23\1\25\1");
    
    /**
     * The transition table of the DFA.
     */
    private static final byte[][] TRANSITION_TABLE = createTransitionTable(
    "\0\t\176\1\0\17\0\31\0\31\0\31\4\1\144\1\"\1\17\1\4\1\53\1\57\1\125\1" +
    "\4\1\33\1\'\1\4\1\73\1\4\1\32\1\t\1\16\1\25\1\160\1\4\5\62\1\0\30\74\1" +
    "\0\b\6\1\0\20\0\16\7\1\0\n\0\16\115\1\0\5\b\1\0\4\0\24\3\1\0\4\0\3\n\1" +
    "\0\25\0\17\13\1\0\t\0\t\f\1\0\17\0\17\170\1\0\3\136\1\0\4\r\1\0\2\55\1" +
    "\0\5\67\1\0\20\0\23\3\1\0\5\0\r\20\1\0\13\0\r\21\1\0\13\0\2\22\1\0\26" +
    "\0\22\23\1\0\6\0\b\50\1\24\1\0\17\0\22\3\1\0\6\0\16\26\1\0\n\0\13\27\1" +
    "\0\r\0\17\30\1\0\t\0\6\31\1\0\22\0\21\3\1\0\5\37\1\0\1\0\17\3\1\0\t" +
    "\0\16\34\1\0\n\0\b\35\1\0\4\102\1\0\13\0\1\36\1\0\27\0\6\35\1\0\22\0\20" +
    "\40\1\0\b\0\f\122\1\0\3\41\1\0\b\0\r\34\1\0\13\0\13\43\1\0\r\0\2\44\1" +
    "\0\26\0\21\45\1\0\7\0\6\46\1\0\1\3\1\0\20\0\16\3\1\0\n\0\b\3\1\0\20" +
    "\0\17\51\1\0\t\0\b\52\1\0\20\0\6\3\1\0\22\0\17\13\1\54\1\0\b\0\20\54\1" +
    "\0\b\0\r\56\1\0\b\106\1\0\2\0\r\54\1\0\13\0\t\60\1\0\17\0\24\61\1\0\4" +
    "\0\13\54\1\0\r\0\22\63\1\0\6\0\t\64\1\0\17\0\17\65\1\107\1\0\b\0\22" +
    "\66\1\0\6\0\4\3\1\0\24\0\2\70\1\0\26\0\6\71\1\0\22\0\16\72\1\0\n\0\20" +
    "\2\1\0\b\0\20\2\1\0\b\0\17\75\1\0\t\0\22\76\1\0\6\0\6\77\1\0\22\0\27" +
    "\100\1\0\1\0\6\101\1\0\22\0\n\75\1\0\16\0\22\103\1\0\6\0\6\104\1\0\22" +
    "\0\17\105\1\0\t\0\17\2\1\0\t\0\22\107\1\0\6\0\6\110\1\0\22\0\t\111\1" +
    "\0\17\0\20\112\1\0\b\0\22\113\1\0\6\0\2\114\1\0\20\3\1\0\5\0\3\107\1" +
    "\0\25\0\2\116\1\0\26\0\16\117\1\0\n\0\17\120\1\0\t\0\20\121\1\0\b\0\n" +
    "\2\1\0\16\0\t\123\1\0\3\154\1\0\13\0\b\124\1\0\20\0\6\123\1\0\22\0\25" +
    "\126\1\0\3\0\t\127\1\0\17\0\22\130\1\0\6\0\b\131\1\0\20\0\16\132\1\0\n" +
    "\0\24\133\1\0\4\0\3\\\1\0\25\0\22\135\1\0\6\0\17\126\1\0\t\0\3\137\1" +
    "\0\25\0\6\140\1\0\22\0\17\141\1\0\t\0\b\142\1\150\1\0\17\0\2\164\1\0\b" +
    "\172\1\0\2\143\1\0\n\0\6\2\1\0\22\0\17\145\1\0\t\0\2\146\1\0\26\0\7" +
    "\147\1\0\21\0\r\145\1\0\13\0\t\151\1\0\17\0\17\152\1\0\t\0\2\153\1\0\26" +
    "\0\7\145\1\0\21\0\t\155\1\0\17\0\17\156\1\0\t\0\2\157\1\0\26\0\5\145\1" +
    "\0\23\0\2\161\1\0\26\0\4\162\1\0\24\0\3\163\1\0\25\0\3\2\1\0\25\0\t" +
    "\165\1\0\17\0\17\166\1\0\t\0\2\167\1\0\13\1\1\0\n\0\r\166\1\0\13\0\f" +
    "\171\1\0\f\0\1\2\1\0\27\0\21\173\1\0\7\0\17\174\1\0\t\0\3\175\1\0\25");
    
    /**
     * Maps state numbers to action numbers.
     */
    private static final byte[] ACTION_MAP = createActionMap(
    "\0\1\1\1\2\1\3\1\0\4\3\1\0\4\3\2\0\1\1\1\0\3\3\1\0\4\3\2" +
    "\0\6\3\1\0\4\3\1\0\3\3\1\0\3\3\1\0\2\3\1\0\b\3\1\2\1\0\30" +
    "\3\1\0\16\3\1\0\13\3\1\0\16");
    
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
     * The start of the scan region.
     */
    private int regionStart;
    
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
    // Table Methods
    //===============
    
    /**
     * Creates the character map of the scanner.
     * 
     * @param characterMapData The compressed data of the character map.
     * @return The character map of the scanner.
     */
    private static byte[] createCharacterMap(String characterMapData) {
        byte[] characterMap = new byte[123];
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
        byte[][] transitionTable = new byte[126][25];
        int length = transitionTableData.length();
        int i = 0;
        int j = 0;
        int k = 0;
        
        while (i < length) {
            byte curValue = (byte)((short)transitionTableData.charAt(i++) - 1);
            
            for (int x=transitionTableData.charAt(i++);x>0;x--) {
                transitionTable[j][k++] = curValue;
            }
            
            if (k == 25) {
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
        byte[] actionMap = new byte[126];
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
    public void setString(String string) {
        this.string = string != null ? string : "";
        
        regionStart = 0;
        regionEnd = this.string.length();
        
        dot = 0;
        
        matchStart = 0;
        matchEnd = 0;
    }
    
    //===============
    // Match Methods
    //===============
    
    /**
     * Returns a substring relative to the last match.
     * 
     * @param startOffset The forward-oriented start offset of the substring
     * relative to the start of the last match.
     * @param endOffset The backward-oriented end offset of the substring
     * relative to the end of the last match.
     * @return The substring at the specified indices.
     * @throws IndexOutOfBoundsException If the specified indices are invalid
     */
    private String getMatchText(int startOffset, int endOffset) {
        int startIndex = matchStart + startOffset;
        int endIndex = matchEnd - endOffset;
        
        if ((startIndex < regionStart) || (endIndex > regionEnd) ||
            (startIndex > endIndex)) {
            
            throw new IndexOutOfBoundsException("match text not available");
        }
        
        return string.substring(startIndex,endIndex);
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
        if (dot < regionEnd) {
            
            // find longest match
            int curState = 4;
            int iterator = dot;
            int matchState = -1;
            int matchPosition = 0;
            
            do {
                char curChar = string.charAt(iterator);
                
                curState = TRANSITION_TABLE[curState][curChar >= 123 ?
                        0 : CHARACTER_MAP[curChar]];
                
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
                case 0: return createDeclarativeKeyword();
                case 1: return createOperativeKeyword();
                case 2: return createOtherToken();
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
