package lox

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

class Lox {
    companion object {
        private val interpreter: Interpreter = Interpreter()

        private var hadError = false
        private var hadRuntimeError = false

        @JvmStatic
        fun main(args: Array<String>) {
            when (args.size) {
                0 -> runPrompt()
                1 -> runFile(args[0])
                else -> {
                    println("Usage: jlox [script]")
                    exitProcess(64)
                }
            }
        }

        private fun runFile(path: String) {
            val bytes = Files.readAllBytes(Paths.get(path))
            run(String(bytes, Charset.defaultCharset()))

            if (hadError) exitProcess(65)
            if (hadRuntimeError) exitProcess(70)
        }

        private fun runPrompt() {
            val input = InputStreamReader(System.`in`)
            val reader = BufferedReader(input)

            while (true) {
                println("> ")
                val line = reader.readLine() ?: break
                run(line)
                hadError = false
            }
        }

        private fun run(source: String) {
            val scanner = Scanner(source)
            val tokens = scanner.scanTokens()
            val parser = Parser(tokens)
            val expr = parser.parse()

            // Stop when syntax error
            if (hadError) return

            interpreter.interpret(expr)
            println(AstPrinter().print(expr!!))
        }

        private fun report(line: Int, where: String, message: String) {
            hadError = true
            error("[line $line] Error $where: $message")
        }

        fun error(line: Int, message: String) {
            report(line, "", message)
        }

        fun error(token: Token, message: String) {
            if (token.type == TokenType.EOF)
                report(token.line, " at end", message)
            else
                report(token.line, "at '${token.lexeme}'", message)
        }

        fun runtimeError(error: RuntimeError) {
            println("${error.message}\n[line ${error.token.line}]")
            hadError = true
        }

    }
}
