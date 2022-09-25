package lox

import java.io.PrintWriter
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Usage: generate_ast <output directory>")
        exitProcess(64)
    }

    val outputDir = args[0]
    defineAst(
        outputDir, "Expr", listOf(
            "Binary   | val left: Expr, val operator: Token, val right: Expr",
            "Grouping | val expression: Expr",
            "Literal  | val value: Any?",
            "Unary    | val operator: Token, val right: Expr"
        )
    )
}

private fun defineAst(
    outputDir: String,
    baseName: String,
    types: List<String>
) {
    val path = "$outputDir/$baseName.kt"
    val writer = PrintWriter(path, Charsets.UTF_8)

    writer.println("package lox\n")
    writer.println("/* This file is generated using GenerateAst.kt. Do not edit by hand! */\n")
    writer.println("abstract class $baseName {\n")

    writer.println("\tabstract fun <R> accept(visitor: Visitor<R>): R\n")

    defineVisitor(writer, baseName, types)
    writer.println()

    for (type in types) {
        val className = type.split("|")[0].trim()
        val fields = type.split("|")[1].trim()
        defineType(writer, baseName, className, fields)
        writer.println()
    }

    writer.println("}")
    writer.close()
}

private fun defineType(writer: PrintWriter, baseName: String, className: String, fieldList: String) {
    writer.println("\tclass $className($fieldList) : $baseName() {")

    // Visitor pattern
    writer.println("\t\toverride fun <R> accept(visitor: Visitor<R>) = visitor.visit$className$baseName(this)")
    writer.println("\t}")
}

private fun defineVisitor(writer: PrintWriter, baseName: String, types: List<String>) {
    writer.println("\tinterface Visitor<R> {")

    for (type in types) {
        val typeName = type.split("|")[0].trim()
        writer.println("\t\tfun visit$typeName$baseName(${baseName.lowercase()}: $typeName): R")
    }

    writer.println("\t}")
}