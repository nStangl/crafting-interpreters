package lox

import lox.TokenType.*

class Interpreter : Expr.Visitor<Any?> {

    fun interpret(expr: Expr?) {
        try {
            val value = evaluate(expr!!)
            println(stringify(value))
        } catch (err: RuntimeError) {
            Lox.runtimeError(err)
        }
    }

    private fun stringify(obj: Any?): String {
        if (obj == null) return "nil"
        if (obj is Double) {
            var text = obj.toString()
            if (text.endsWith(".0"))
                text = text.substring(0, text.length - 2)
            return text
        }
        return obj.toString()
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            GREATER -> {
                checkNumberOperand(expr.operator, left, right)
                (left as Double) > (right as Double)
            }
            GREATER_EQUAL -> {
                checkNumberOperand(expr.operator, left, right)
                left as Double >= right as Double
            }
            LESS -> {
                checkNumberOperand(expr.operator, left, right)
                (left as Double) < (right as Double)
            }
            LESS_EQUAL -> {
                checkNumberOperand(expr.operator, left, right)
                left as Double <= right as Double
            }
            MINUS -> {
                checkNumberOperand(expr.operator, right)
                left as Double - right as Double
            }
            PLUS -> {
                if (left is Double && right is Double)
                    left + right
                if (left is String && right is String)
                    left + right
                throw RuntimeError(expr.operator, "Operands must be two numbers or two strings.")
            }
            SLASH -> {
                checkNumberOperand(expr.operator, left, right)
                left as Double / right as Double
            }
            STAR -> {
                checkNumberOperand(expr.operator, left, right)
                left as Double * right as Double
            }
            BANG_EQUAL -> !isEqual(left, right)
            EQUAL_EQUAL -> isEqual(left, right)
            else -> null
        }
    }

    override fun visitGroupingExpr(expr: Expr.Grouping) = evaluate(expr.expression)

    override fun visitLiteralExpr(expr: Expr.Literal) = expr.value

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)
        return when (expr.operator.type) {
            MINUS -> -(right as Double)
            BANG -> !isTruthy(right)
            else -> null
        }
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperand(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers.")
    }

    private fun isEqual(lhs: Any?, rhs: Any?): Boolean {
        if (lhs == null && rhs == null) return true
        if (lhs == null) return false
        return lhs == rhs
    }

    private fun isTruthy(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj is Boolean) return obj
        return true
    }

    private fun evaluate(expr: Expr) = expr.accept(this)
}