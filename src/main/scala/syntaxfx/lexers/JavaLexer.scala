package syntaxfx.lexers

import syntaxfx.Lexer
import syntaxfx.Token

class JavaLexer extends Lexer {
    //Keep track of current location
    var currentIndex : Int = 0

    //Supported states
    val INITIAL_STATE : Int = 0
    val KEYWORD : Int = 1

    //Keep track of the current state
    var currentState : Int = INITIAL_STATE

    //Keep track of the position of the last state change
    var stateStart : Int = 0

    //The string to be lexed
    var str : String = ""


    override def setString(str : String) : Unit = {
        //Reset the string and all the lexing variables
        this.str = str
        currentIndex = 0
        stateStart = 0
        currentState = INITIAL_STATE
    }

    def parseKeyword(str : String) : Int = {
        val keywords : Map[String,Int] = Map(
            "package" -> Token.KEYWORD,
            "void" -> Token.KEYWORD,
            "public" -> Token.KEYWORD,
            "protected" -> Token.KEYWORD,
            "private" -> Token.KEYWORD,
            "static" -> Token.KEYWORD,
            "final" -> Token.KEYWORD,
            "abstract" -> Token.KEYWORD,
            "native" -> Token.KEYWORD,
            "strictfp" -> Token.KEYWORD,
            "synchronized" -> Token.KEYWORD,
            "transient" -> Token.KEYWORD,
            "volatile" -> Token.KEYWORD,
            "const" -> Token.KEYWORD,
            "extends" -> Token.KEYWORD,
            "implements" -> Token.KEYWORD,
            "throws" -> Token.KEYWORD,
            "class" -> Token.KEYWORD,

            "import" -> Token.KEYWORD2,
            "if" -> Token.KEYWORD2,
            "else" -> Token.KEYWORD2,
            "switch" -> Token.KEYWORD2,
            "case" -> Token.KEYWORD2,
            "default" -> Token.KEYWORD2,
            "for" -> Token.KEYWORD2,
            "while" -> Token.KEYWORD2,
            "do" -> Token.KEYWORD2,
            "continue" -> Token.KEYWORD2,
            "break" -> Token.KEYWORD2,
            "return" -> Token.KEYWORD2,
            "goto" -> Token.KEYWORD2,
            "try" -> Token.KEYWORD2,
            "catch" -> Token.KEYWORD2,
            "finally" -> Token.KEYWORD2,
            "throw" -> Token.KEYWORD2,
            "assert" -> Token.KEYWORD2
        )
        //If the keyword is recognized
        if (keywords.contains(str)) {
            return keywords(str)
        }
        else {
            return Token.IDENTIFIER
        }
    }

    override def getNextToken() : Token = {
        var tok : Token = null
        var foundToken : Boolean = false

        //Go through the string, character by character
        while (!foundToken && currentIndex < str.length) {
            //Depending on the current state, do something different
            currentState match {
                case INITIAL_STATE =>
                    tok = handleInitialState
                case KEYWORD =>
                    tok = handleKeyword
            }
            //If a token was indeed found
            if (tok != null) {
                foundToken = true
            }
            currentIndex += 1
            //If we have reached the end of the string and haven't found a token
            if (currentIndex == str.length && !foundToken) {
                tok = new Token(Token.OTHER, stateStart, currentIndex)
            }
        }
        //Return the token
        return tok
    }

    //Handle the INITIAL_STATE state
    def handleInitialState() : Token = {
        //If we have found a letter
        if (str.charAt(currentIndex).isLetter) {
            var tok : Token = new Token(Token.OTHER, stateStart, currentIndex)
            stateStart = currentIndex
            currentState = KEYWORD
            return tok
        }
        //None of the above conditions apply - return null
        return null
    }

    //Handle the KEYWORD state
    def handleKeyword() : Token = {
        //If we find anything that isn't a letter
        if (!(str.charAt(currentIndex).isLetter)) {
            var tok : Token = new Token(parseKeyword(str.substring(stateStart, currentIndex)), stateStart, currentIndex)
            stateStart = currentIndex + 1
            currentState = INITIAL_STATE
            return tok
        }
        //None of the above conditions apply - return null
        return null
    }
}
