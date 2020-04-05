package syntaxfx;

/**
 * This class represents a Lexer. All lexers used by SyntaxFX must subclass this class.
 */
public abstract class Lexer {
    
    /**
     * Sets the string to be scanned. All lexers must override this method.
     */
    public abstract void setString(String string);

    /**
     * Gets the next token found by the lexer. Returns null when no tokens remain. All lexers must override this method.
     */
    public abstract Token getNextToken();
}
