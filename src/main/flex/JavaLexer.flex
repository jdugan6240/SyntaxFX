package syntaxfx.lexers

import syntaxfx.Lexer
import syntaxfx.Token

%%

%public
%class JavaLexer
%extends Lexer
%final
%unicode
%char
%type Token

/**
 * 
 */
%{
    var tokenStart : Int = 0
    var tokenLength : Int = 0

    //This constructor is needed because JFlex doesn't define a no-arg constructor.
    def this() {
        this(new java.io.StringReader(""))
    }

    def token(tokenType : Int) : Token = {
        return new Token(tokenType, yychar, yychar + yylength)
    }

    def setString(str : String) : Unit = {
        yyreset(new java.io.StringReader(str))
    }

    def getNextToken() : Token = {
        yylex()
    }
%}

//Main character classes
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

WhiteSpace = {LineTerminator} | [ \t\f]+

//Comments
Comment = {TraditionalComment} | {EndOfLineComment}

TraditionalComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}?

//Identifiers
Identifier = [:jletter:][:jletterdigit:]*

//Integer literals
DecIntegerLiteral = 0 | [1-9][0-9]*
DecLongLiteral    = {DecIntegerLiteral} [lL]

HexIntegerLiteral = 0 [xX] 0* {HexDigit} {1,8}
HexLongLiteral    = 0 [xX] 0* {HexDigit} {1,16} [lL]
HexDigit          = [0-9a-fA-F]

OctIntegerLiteral = 0+ [1-3]? {OctDigit} {1,15}
OctLongLiteral    = 0+ 1? {OctDigit} {1,21} [lL]
OctDigit          = [0-7]
    
//Floating point literals      
FloatLiteral  = ({FLit1}|{FLit2}|{FLit3}) {Exponent}? [fF]
DoubleLiteral = ({FLit1}|{FLit2}|{FLit3}) {Exponent}?

FLit1    = [0-9]+ \. [0-9]* 
FLit2    = \. [0-9]+ 
FLit3    = [0-9]+ 
Exponent = [eE] [+-]? [0-9]+

//String and character literals
StringCharacter = [^\r\n\"\\]
SingleCharacter = [^\r\n\'\\]

%state STRING, CHARLITERAL, JDOC, JDOC_TAG

%%

<YYINITIAL> {

//Keywords
"package"|"void"|"public"|"protected"|"private"|"static"|"final"|"abstract"|"native"|"strictfp"|"synchronized"
    |"transient"|"volatile"|"const"|"extends"|"implements"|"throws"|"class" 
    { return token(Token.KEYWORD) }

"import"|"if"|"else"|"switch"|"case"|"default"|"for"|"while"|"do"|"continue"|"break"|"return"|"goto"|"try"
    |"catch"|"finally"|"throw"|"assert"|"true"|"false"|"null" 
    { return token(Token.KEYWORD2) }

//Operators
"("|")"|"{"|"}"|"["|"]"|";"|","|"."|"="|">"|"<"|"!"|"~"|"?"|":"|"=="|"<="|">="|"!="|"&&"|"||"|"++"|"--"|"+"|"-"|"*"|"/"
    |"&"|"|"|"^"|"%"|"<<"|">>"|">>>"|"+="|"-="|"*="|"/="|"&="|"|="|"^="|"%="|"<<="|">>="|">>>="|"="
    { return token(Token.OPERATOR) }

//String literal
\"  {
        yybegin(STRING)
        tokenStart = yychar
        tokenLength = 1
        null //We need this whenever we don't immediately return a token
    }

//Character literal
\'  {
        yybegin(CHARLITERAL)
        tokenStart = yychar
        tokenLength = 1
        null //We need this whenever we don't immediately return a token
    }

//Numeric literals
{DecIntegerLiteral} | {DecLongLiteral} | {HexIntegerLiteral} | {HexLongLiteral} | {OctIntegerLiteral} | {OctLongLiteral}
    | {FloatLiteral} | {DoubleLiteral} | {DoubleLiteral}[dD] 
    { return token(Token.NUMBER) }

//Javadoc comments need a state so we can highlight javadoc tags
"/**" {
          yybegin(JDOC)
          tokenStart = yychar
          tokenLength = 3
          null //We need this whenever we don't immediately return a token
      }

//Comments
{Comment} { return token(Token.COMMENT) }

//Identifiers
{Identifier} { return token(Token.IDENTIFIER) }

}

<STRING> {

\"  {
        yybegin(YYINITIAL)
        return new Token(Token.STRING, tokenStart, tokenStart + tokenLength + 1)
    }

{StringCharacter}+ { 
                        tokenLength += yylength 
                        null //We need this whenever we don't immediately return a token
                   }

\\[0-3]?{OctDigit}?{OctDigit} { 
                                   tokenLength += yylength
                                   null //We need this whenever we don't immediately return a token
                              }

//Escape sequences
\\. { 
        tokenLength += yylength 
        null //We need this whenever we don't immediately return a token

    }

{LineTerminator}   { 
                        yybegin(YYINITIAL)
                        null //We need this whenever we don't immediately return a token
                   }

}

<CHARLITERAL> {

\'  {
        yybegin(YYINITIAL)
        return new Token(Token.STRING, tokenStart, tokenStart + tokenLength + 1)
    }

{SingleCharacter}+ { 
                        tokenLength += yylength
                        null //We need this whenever we don't immediately return a token
                   }

//Escape sequences
\\. { 
        tokenLength += yylength
        null //We need this whenever we don't immediately return a token

    }

{LineTerminator}   { 
                        yybegin(YYINITIAL) 
                        null //We need this whenever we don't immediately return a token
                   }

}

<JDOC> {

"*/"  {
          yybegin(YYINITIAL)
          return new Token(Token.DOC_COMMENT, tokenStart, tokenStart + tokenLength + 2)
      }

"@" {
        yybegin(JDOC_TAG)
        val start : Int = tokenStart
        tokenStart = yychar
        val len : Int = tokenLength
        tokenLength = 1
        return new Token(Token.DOC_COMMENT, start, start + len)
    }

.|\n { 
        tokenLength += 1 
        null //We need this whenever we don't immediately return a token
     }

}

<JDOC_TAG> {

([:letter:])+ ":"? { 
                       tokenLength += yylength
                       null //We need this whenever we don't immediately return a token
                   }

"*/"  {
          yybegin(YYINITIAL)
          return new Token(Token.DOC_COMMENT, tokenStart, tokenStart + tokenLength + 2)
      }

.|\n  {
          yybegin(JDOC)
          val start : Int = tokenStart
          tokenStart = yychar
          val len : Int = tokenLength
          tokenLength = 1
          return new Token(Token.DOC_COMMENT_TAG, start, start + len)
      }

}

//Error fallback
.|\n {
        null //We need this whenever we don't immediately return a token
     }
<<EOF>> { return null }
