package com.craftinginterpreters.lox;

/**
 * Represents a token in the source code.
 * A token consists of its type, text, optional literal value, and its location in the source code.
 */
class Token {
    /** The type of the token (e.g., identifier, keyword, operator). */
    final TokenType type;
    
    /** The exact text of the token as it appears in the source code. */
    final String lexeme;
    
    /** The value of the token if it is a literal (e.g., number, string). */
    final Object literal;
    
    /** The line number in the source code where the token appears. */
    final int line;

    /**
     * Constructs a new Token with the specified type, text, literal value, and source line number.
     *
     * @param type    The type of the token.
     * @param lexeme  The text of the token.
     * @param literal The value of the token if it is a literal.
     * @param line    The line number where the token is found.
     */
    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    /**
     * Returns a string representation of the token.
     * The format is: type lexeme literal.
     *
     * @return A string representing the token's type, text, and literal value.
     */
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
