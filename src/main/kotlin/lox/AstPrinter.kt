package lox

class AstPrinter : Expr.Visitor<String> {

    companion object {
        /* Not explicitly needed, useful for debugging though */
        @JvmStatic
        fun main(args: Array<String>) {
            val expression = Expr.Binary(
                Expr.Unary(
                    Token(TokenType.MINUS, "-", null, 1),
                    Expr.Literal(123)
                ),
                Token(TokenType.STAR, "*", null, 1),
                Expr.Grouping(Expr.Literal(45.67))
            )

            println(AstPrinter().print(expression))
        }
    }

    fun print(expr: Expr) = expr.accept(this)

    private fun parenthesize(name: String, vararg expressions: Expr): String {
        val builder = StringBuilder()
        builder.append("($name")

        for (expr in expressions)
            builder.append(" ").append(expr.accept(this))

        builder.append(")")
        return builder.toString()
    }

    override fun visitBinaryExpr(expr: Expr.Binary): String = parenthesize(expr.operator.lexeme, expr.left, expr.right)

    override fun visitGroupingExpr(expr: Expr.Grouping): String = parenthesize("group", expr.expression)

    override fun visitLiteralExpr(expr: Expr.Literal): String {
        if (expr.value == null)
            return "nil"

        return expr.value.toString()
    }

    override fun visitUnaryExpr(expr: Expr.Unary): String = parenthesize(expr.operator.lexeme, expr.right)
}
