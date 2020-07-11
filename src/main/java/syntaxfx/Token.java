package syntaxfx;

public class Token {

    public final static int OPERATOR = 0;
    public final static int KEYWORD = 1;
    public final static int KEYWORD2 = 2;
    public final static int IDENTIFIER = 3;
    public final static int STRING = 4;
    public final static int COMMENT = 5;
    public final static int DOC_COMMENT = 6;
    public final static int DOC_COMMENT_TAG = 7;
    public final static int TYPE = 8;
    public final static int NUMBER = 9;
    public final static int FUNCTION = 10;
    public final static int MEMBER_VAR = 11;
    public final static int OTHER = 12;

    private int tokenType, start, end;

    public Token(int tokenType, int start, int end) {
        this.tokenType = tokenType;
        this.start = start;
        this.end = end;
    }

    public int getTokenType() {
        return tokenType;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getLength() {
        return end - start;
    }

}
