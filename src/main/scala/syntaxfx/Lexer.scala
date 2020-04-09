package syntaxfx

/**
  * This class represents a Lexer. All lexers used by SyntaxFX must subclass this class.
  */
abstract class Lexer {

    /**
      * Sets the string to be scanned. All lexers must override this method.
      */
    def setString(string: String): Unit

    /**
      * Gets the next token found by the lexer. Returns null when no tokens remain. All lexers must override this method.
      */
    def getNextToken(): Token

}
