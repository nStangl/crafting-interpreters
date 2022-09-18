package lox

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

object Lox {
    private var hadError = false

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

        if (hadError)
            exitProcess(65)
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

        for (token in tokens) {
            println(token)
        }
    }

    private fun report(line: Int, where: String, message: String) {
        hadError = true
        error("[line $line] Error $where: $message")
    }

    fun error(line: Int, message: String) {
        report(line, "", message)
    }
}