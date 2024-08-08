package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    static boolean hadError = false;

    /**
     * Entry point of the application. Processes command-line arguments to either
     * run a script file or enter interactive mode.
     *
     * Usage:
     * - To run a script: `java Lox <script-file>`
     * - To enter interactive mode: `java Lox`
     *
     * @param args Command-line arguments. If exactly one argument is provided, 
     *             it is treated as a file path for a script to be executed. 
     *             If no arguments are provided, the program enters interactive mode.
     * @throws IOException If an I/O error occurs while reading the script file 
     *                     or user input.
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64); // Incorrect command usage. From FreeBSD sysexits exit code standards
        } else if (args.length == 1) {
            runFile(args[0]); // Assumes a single argument is a file
        } else {
            runPrompt(); // Interactive mode
        }
    }

    /**
     * Reads the contents of a file specified by the given path, converts the contents
     * from bytes to a string using the default charset, and then processes the string 
     * by passing it to the `run` method.
     *
     * @param path The file path as a string to be read. Must be a valid file path.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path)); // Stores contents of file at `path` in a byte array
        run(new String(bytes, Charset.defaultCharset())); // Converts byte array contents to a string 

        if (hadError) System.exit(65); // Incorrect user input
    }

    /**
     * Continuously prompts the user for input, reads each line from the standard input stream,
     * and processes it using the `run` method. The method displays a ">" prompt to indicate readiness 
     * for user input. The loop terminates when the end of the input stream is reached, such as when 
     * the user signals the end of input (e.g., by pressing Ctrl+D or Ctrl+Z).
     *
     * @throws IOException If an I/O error occurs while reading from the standard input stream.
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in); // Used to convert bytes read from standard input stream to characters
        BufferedReader reader = new BufferedReader(input); // Used to read character input stream 

        for (;;) {
            System.out.print(">");
            String line = reader.readLine(); // Reads chars from user input stream and returns as string
            if (line == null) break; // Ends if user closes input stream
            run(line);
            hadError = false; // reset error flag
        }
    }

    /**
     * Parses the provided source string into a list of tokens and prints each token to the console.
     *
     * This method uses a `Scanner` to tokenize the `source` string, then iterates over the resulting
     * tokens and prints each one. The primary purpose is to display the tokens for debugging or
     * verification purposes.
     *
     * @param source The source string to be tokenized.
     */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens(); // Parse and tokenize source string

        // Just printing the tokens for now.
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    /**
     * Reports an error with a given line number and message.
     * This method forwards the error details to the `report` method.
     *
     * @param line    The line number where the error occurred.
     * @param message The error message to be reported.
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * Formats and prints an error message to the standard error stream.
     * Updates the error flag to indicate that an error has occurred.
     *
     * @param line    The line number where the error occurred.
     * @param where   Additional context or location information about the error.
     * @param message The error message to be printed.
     */
    private static void report(int line, String where, String message) {
        System.err.println(
            "[line " + line + "] Error" + where + ": " + message); // Prints to standard eror stream
        hadError = true;
    }
}