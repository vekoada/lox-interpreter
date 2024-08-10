package com.craftinginterpreters.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * This class is responsible for generating Abstract Syntax Tree (AST) classes based on provided types.
 * It defines the structure of AST classes and writes them to an output directory.
 */
public class GenerateAST {
    /**
     * Main method for generating Abstract Syntax Tree (AST) classes.
     * Checks if the correct number of arguments is provided and calls defineAST method to generate AST classes.
     *
     * @param args The command-line arguments containing the output directory.
     * @throws IOException If an I/O error occurs during AST generation.
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) { // Should have 1 argument
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAST(outputDir, "Expr", Arrays.asList(
            "Binary   : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal  : Object value",
            "Unary    : Token operator, Expr right"
        ));
    }

    /**
     * Defines the Abstract Syntax Tree (AST) classes based on the provided types.
     *
     * @param outputDir The output directory where the AST classes will be generated.
     * @param baseName  The base name for the AST classes.
     * @param types     The list of types to generate AST classes for.
     * @throws IOException If an I/O error occurs while creating the AST classes.
     */
    private static void defineAST(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java"; 
        PrintWriter writer = new PrintWriter(path, "UTF-8"); // Create a new file in the output directory

        // Write the package statement for the AST classes
        writer.println("package com.craftinginterpreters.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {"); // Define the abstract base class

        defineOperation(writer, baseName, types); // In the Visitor pattern, "Visitor" is typically used, but "Operation" is more intuitive for this context.

        // Base accept method
        writer.println();
        writer.println("  abstract <R> R execute(Operation<R> operation);"); // Using "execute" instead of "accept". 

        // Generate AST classes based on the provided types
        for (String type : types) {
            String className = type.split(":")[0].trim(); // Extract the class name
            String fields = type.split(":")[1].trim(); // Extract the field list
            defineType(writer, baseName, className, fields); // Define the AST class
        }

        writer.println("}");
        writer.close();
    }

    /**
     * Defines an operation interface for executing operations on different types of AST nodes. More commonly known as the Visitor pattern.
     *
     * @param writer    The PrintWriter object to write the operation interface.
     * @param baseName  The base name for the AST classes.
     * @param types     The list of types to generate operation methods for.
     */
    private static void defineOperation(PrintWriter writer, String baseName, List<String> types) {
        writer.println("  interface Operation<R> {"); // Define the visitor interface (We will use Operation instead).

        for (String type : types) {

            String typeName = type.split(":")[0].trim(); // Get the name of the type (e.g. "Binary")
            writer.println("    R on" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");"); // For each type, define a visit method (e.g. "R operator.onBinary(Binary binary)"). Using "on" instead of visit for clarity
        }
        
        writer.println("  }");
    }

    /**
     * Defines a static class for each AST type with the specified class name and fields.
     * Initializes the constructor for the AST class and stores constructor parameters in class fields.
     *
     * @param writer    The PrintWriter object to write the AST class definition.
     * @param baseName  The base name for the AST class.
     * @param className The name of the AST class being defined.
     * @param fieldList The list of fields for the AST class constructor.
     */
    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        // Define a static class for each AST type
        writer.println("  static class " + className + " extends " + baseName + " {");

        // Define the constructor for the AST class
        writer.println("    " + className + "(" + fieldList + ") {");

        // Store constructor parameters in class fields
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1]; // Extract the field name
            writer.println("      this." + name + " = " + name + ";"); // Store the field name in the class
        }

        writer.println("    }");

        // Operation (Visitor) pattern
        writer.println();
        writer.println("    @Override");
        writer.println("    <R> R execute(Operation<R> operation) {");
        writer.println("      return operation.on" + className + baseName + "(this);");
        writer.println("    }");

        // Define fields for the AST class
        writer.println();
        for (String field : fields) {
            writer.println("    final " + field + ";");
        }

        writer.println();
    }
}