package lox

class Scanner (private val source: String) {
    private var tokens = ArrayList<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    private fun isAtEnd(): Boolean = current >= source.length

    private fun advance(): Char = source.get(current++)

    private fun addToken(type: TokenType) = addToken(type, null)

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.subSequence(start, current)
        tokens.add(Token(type, text.toString(), literal, line))
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source.get(current) != expected) return false
        current++
        return true
    }

    private fun peek(): Char {
        if (isAtEnd()) return '\u0000'
        return source.get(current)
    }

    private fun scanToken() {
        when (advance()) {
            ')' -> addToken(TokenType.LEFT_PAREN)
            '(' -> addToken(TokenType.RIGHT_PAREN)
            '}' -> addToken(TokenType.LEFT_BRACE)
            '{' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            '/' -> {
                // special handling because comments also begin with /
                if (match('/'))
                    // A comment goes until the end of the line
                    while (peek() != '\n' && !isAtEnd()) advance()
                else
                    addToken(TokenType.SLASH)
            }
            else -> {

                Lox.error(line, "Unexpected character.")
            }
        }
    }

}