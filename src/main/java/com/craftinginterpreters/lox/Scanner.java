package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Scanner class is responsible for tokenizing a source code string.
 * <p>
 * This class reads a source code string and converts it into a sequence of tokens. It recognizes various token types including keywords, identifiers, literals, and symbols. The scanner also handles comments and whitespace while maintaining the current line number and reporting errors for invalid syntax or unterminated constructs.
 * </p>
 * 
 * <p>
 * Usage:
 * <ul>
 *   <li>Create an instance of the Scanner with the source code string.</li>
 *   <li>Invoke the {@link #scanTokens()} method to generate a list of tokens.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Features:
 * <ul>
 *   <li>Identification and categorization of reserved keywords.</li>
 *   <li>Support for single-line (<code>//</code>) and block (<code>/* ... *\/</code>) comments.</li>
 *   <li>Handling of identifiers, numeric literals, and string literals.</li>
 *   <li>Line number tracking and error reporting for unexpected characters or unterminated strings.</li>
 * </ul>
 * </p>
 */
class Scanner {
    private final String source; // Source code to be scanned
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0; // Starting position of current lexeme
    private int current = 0; // Current position in source code
    private int line = 1; // Current line number in source code
    private static final Map<String, TokenType> keywords;

    static { // Reserved keywords
      keywords = new HashMap<>();
      keywords.put("and",   TokenType.AND);
      keywords.put("class",  TokenType.CLASS);
      keywords.put("else",   TokenType.ELSE);
      keywords.put("false",  TokenType.FALSE);
      keywords.put("for",    TokenType.FOR);
      keywords.put("fun",    TokenType.FUN);
      keywords.put("if",     TokenType.IF);
      keywords.put("nil",    TokenType.NIL);
      keywords.put("or",     TokenType.OR);
      keywords.put("print",  TokenType.PRINT);
      keywords.put("return", TokenType.RETURN);
      keywords.put("super",  TokenType.SUPER);
      keywords.put("this",   TokenType.THIS);
      keywords.put("true",   TokenType.TRUE);
      keywords.put("var",    TokenType.VAR);
      keywords.put("while",  TokenType.WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }

    /**
     * Scans the source code and generates a list of tokens.
     * <p>
     * Iterates through the source code, lexing each token and adding it to the list. Continues until the end of the source code is reached, then adds an EOF token to mark the end of the input.
     * </p>
     * 
     * @return A list of tokens generated from the source code.
     */
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // Beginning of next lexeme
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    /**
     * Processes the current character and generates the corresponding token.
     * <p>
     * This method identifies the type of token based on the current character and handles special cases such as multi-character tokens, comments, and whitespace. It updates the token list with the appropriate token and advances to the next character in the source code.
     * </p>
     */
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;

            case '!': 
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); // If the next char after ! is a =, then add BANG_EQUAL token
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
                
            case '/':
                if (match('/')) {
                    // A comment extends until the end of a line
                    while (peek() != '\n' && !isAtEnd()) advance(); // Don't call addToken() since comments meaningless to parser
                } else if (match('*')) {
                    while (peek() != '*' && peekNext() != '/' && !isAtEnd()) advance();

                     // Handle the end of the block comment
                    if (peek() == '*' && peekNext() == '/') {
                        advance(); // Consume the '*'
                        advance();  // Consume the '/'
                    } else Lox.error(line, "Unterminated block comment.");

                } else addToken(TokenType.SLASH);
                break;

            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
        
            case '\n':
                line++;
                break;

            case '"': string(); break;

            default:
            if (isDigit(c)) {
                number();
            } else if (isAlpha(c)) {
                identifier();
            } else Lox.error(line, "Unexpected character.");
            break;
        }
    }

    /**
     * Scans and processes an identifier from the source code.
     * <p>
     * This method collects a sequence of alphanumeric characters starting from the current position to form an identifier. It then determines if the identifier matches a reserved keyword or is a user-defined identifier, and adds the corresponding token to the list.
     * </p>
     */
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER; // Text is a user-defined identifier if it doesn't match reserved keywords
        addToken(type);
    }

    /**
     * Scans and processes a numeric literal from the source code.
     * <p>
     * This method collects digits to form an integer or decimal number. If a decimal point is encountered, it continues to scan for additional digits to handle fractional numbers. The resulting number is then added as a token to the list.
     * </p>
     */
    private void number() {
        while (isDigit(peek())) advance();

        // Look for fractional part
        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // Consume the decimal point

            while(isDigit(peek())) advance();
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    /**
     * Scans and processes a string literal from the source code.
     * <p>
     * This method collects characters until the closing quotation mark is found or the end of the source code is reached. It handles multi-line strings by tracking line breaks. If the string is not properly terminated, it reports an error. The string value, excluding the surrounding quotes, is then added as a token.
     * </p>
     */
    private void string() {
        while (peek() != '"' && !isAtEnd()) { // Support multi-line strings
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // Consume the closing "
        advance();

        // Trim the quotes from the beginning and end of the string value
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    /**
     * Checks if the current character matches the expected character.
     * <p>
     * If the current character in the source code matches the expected character, this method advances to the next character and returns true. Otherwise, it returns false without advancing.
     * </p>
     * 
     * @param expected The character to match against the current character.
     * @return {@code true} if the current character matches the expected character, {@code false} otherwise.
     */
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++; // Only move to next character if it's expected (for tokens of 2 chars)
        return true;
    }

    /**
     * Returns the current character in the source code without advancing.
     * <p>
     * This method provides access to the character at the current position in the source code without moving the position forward.
     * </p>
     * 
     * @return The current character, or {@code '\0'} if the end of the source code is reached.
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current); // Returns current unconsumed character and doesn't advance
    }

    /**
     * Returns the next character in the source code without advancing.
     * <p>
     * This method looks ahead one character from the current position without consuming it. It provides a way to check the next character in the source code.
     * </p>
     * 
     * @return The next character, or {@code '\0'} if there is no next character (end of source code).
     */
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1); // Look ahead a second character
    }

    /**
     * Checks if the character is an alphabetic letter or underscore.
     * <p>
     * This method returns {@code true} if the character is a lowercase letter, uppercase letter, or an underscore. Otherwise, it returns {@code false}.
     * </p>
     * 
     * @param c The character to check.
     * @return {@code true} if the character is a letter or underscore, {@code false} otherwise.
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }

    /**
     * Checks if the character is an alphanumeric character.
     * <p>
     * This method returns {@code true} if the character is either an alphabetic letter (or underscore) or a digit.
     * </p>
     * 
     * @param c The character to check.
     * @return {@code true} if the character is alphanumeric (letter or digit), {@code false} otherwise.
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * Checks if the character is a digit.
     * <p>
     * This method returns {@code true} if the character is a digit (0-9). Otherwise, it returns {@code false}.
     * </p>
     * 
     * @param c The character to check.
     * @return {@code true} if the character is a digit, {@code false} otherwise.
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Checks if the end of the source code has been reached.
     * <p>
     * This method returns {@code true} if the current position is at or beyond the end of the source code string. Otherwise, it returns {@code false}.
     * </p>
     * 
     * @return {@code true} if the end of the source code is reached, {@code false} otherwise.
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Advances to the next character in the source code.
     * <p>
     * This method returns the current character and moves the current position forward by one character.
     * </p>
     * 
     * @return The character at the current position before advancing.
     */
    private char advance() {
        return source.charAt(current++); // Returns current character in source code string and increments
    }

    /**
     * Adds a token to the list of tokens with the specified type.
     * <p>
     * This method creates a token of the given type with no associated literal value and adds it to the list of tokens.
     * </p>
     * 
     * @param type The type of the token to add.
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Adds a token to the list of tokens with the specified type and literal value.
     * <p>
     * This method creates a token with the given type and an optional literal value, then adds it to the list of tokens. The token text is derived from the source code substring between the start and current positions.
     * </p>
     * 
     * @param type The type of the token to add.
     * @param literal The literal value associated with the token, or {@code null} if none.
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}