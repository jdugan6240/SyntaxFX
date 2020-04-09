package syntaxfx.lexers

import syntaxfx.Lexer
import syntaxfx.Token

class JavaLexer extends Lexer {
    //Keep track of current location
    var currentIndex : Int = 0

    //Supported states
    val INITIAL_STATE : Int = 0
    val KEYWORD : Int = 1
    val STRING : Int = 2
    val CHAR : Int = 3
    val COMMENT : Int = 4

    //Keep track of the current state
    var currentState : Int = INITIAL_STATE

    //Keep track of the position of the last state change
    var stateStart : Int = 0

    //The string to be lexed
    var str : String = ""

    //Keeps track of how many backslashes have been found - used in strings to detect escaped characters
    var numSlashes : Int = 0

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
                case STRING =>
                    tok = handleString
                case CHAR =>
                    tok = handleChar
                case COMMENT =>
                    tok = handleComment
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
        //If we have found the beginning of a string
        if (str.charAt(currentIndex) == '"') {
            var tok : Token = new Token(Token.OTHER, stateStart, currentIndex)
            stateStart = currentIndex
            currentState = STRING
            return tok
        }
        //If we have found the beginning of a char literal
        if (str.charAt(currentIndex) == '\'') {
            var tok : Token = new Token(Token.OTHER, stateStart, currentIndex)
            stateStart = currentIndex
            currentState = CHAR
            return tok
        }
        //If we have found the beginning of a single-line comment
        if (str.charAt(currentIndex) == '/' && str.charAt(currentIndex - 1) == '/') {
            var tok : Token = new Token(Token.OTHER, stateStart, currentIndex - 1)
            stateStart = currentIndex - 1
            currentState = COMMENT
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

    //Handle the STRING state
    def handleString() : Token = {
        //If we have found a backslash
        if (str.charAt(currentIndex) == '\\') {
            numSlashes += 1
            if (numSlashes == 2) numSlashes = 0 //2 backslashes equals an escaped backslash
        }
        //Anything else
        else {
            numSlashes = 0
        }
        //If we have found a newline character, bail out of the string state
        if (str.charAt(currentIndex) == '\n') {
            var tok : Token = new Token(Token.OTHER, stateStart, currentIndex)
            stateStart = currentIndex + 1
            currentState = INITIAL_STATE
            return tok
        }
        //If we found the end of the string and it's not escaped
        if (str.charAt(currentIndex) == '"' && numSlashes == 0) {
            var tok : Token = new Token(Token.STRING, stateStart, currentIndex + 1)
            stateStart = currentIndex + 1
            currentState = INITIAL_STATE
            return tok
        }
        //None of the above conditions apply - return null
        return null
    }

    //Handle the CHAR state
    def handleChar() : Token = {
        //If we have found a backslash
        if (str.charAt(currentIndex) == '\\') {
            numSlashes += 1
            if (numSlashes == 2) numSlashes = 0 //2 backslashes equals an escaped backslash
        }
        //Anything else
        else {
            numSlashes = 0
        }
        //If we have found a newline character, bail out of the string state
        if (str.charAt(currentIndex) == '\n') {
            var tok : Token = new Token(Token.OTHER, stateStart, currentIndex)
            stateStart = currentIndex + 1
            currentState = INITIAL_STATE
            return tok
        }
        //If we found the end of the char literal and it's not escaped
        if (str.charAt(currentIndex) == '\'' && numSlashes == 0) {
            var tok : Token = new Token(Token.STRING, stateStart, currentIndex + 1)
            stateStart = currentIndex + 1
            currentState = INITIAL_STATE
            return tok
        }
        //None of the above conditions apply - return null
        return null
    }

    //Handle the COMMENT state
    def handleComment() : Token = {
        //If we found a newline character, exit the comment
        if (str.charAt(currentIndex) == '\n') {
            var tok : Token = new Token(Token.COMMENT, stateStart, currentIndex)
            stateStart = currentIndex + 1
            currentState = INITIAL_STATE
            return tok
        }
        //If this character is the end of the string, exit the comment
        if (currentIndex == str.length() - 1) {
            var tok : Token = new Token(Token.COMMENT, stateStart, currentIndex)
            stateStart = currentIndex + 1
            currentState = INITIAL_STATE
            return tok
        }
        //None of the above conditions apply - return null
        return null
    }

}
