package com.craftinginterpreters.lox;

/**
 * Enum representing the various types of tokens used in the lexer or parser.
 * This includes single-character symbols, multi-character operators, literals,
 * keywords, and the end-of-file marker.
 *
 * Token types are categorized as follows:
 * - Single-character tokens (e.g., parentheses, operators)
 * - One or two-character tokens (e.g., equality, comparison operators)
 * - Literals (e.g., identifiers, strings, numbers)
 * - Keywords (e.g., control flow keywords, data types)
 * - EOF (End-of-File marker)
 */

 enum TokenType {
    // Single-character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // One or two character tokens
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals
    IDENTIFIER, STRING, NUMBER, 

    // Keywords
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    EOF
}
