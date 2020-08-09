package syntaxfx;

public abstract class Lexer {
    public abstract void setString(String str);
    public abstract Token getNextToken();
}
