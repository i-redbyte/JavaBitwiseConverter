package org.redbyte.generator

import org.redbyte.ast.*

class KotlinCodeGenerator : ExprVisitor {
    private val operatorPrecedence = mapOf(
        "unary" to 12,
        "shl" to 11,
        "shr" to 11,
        "ushr" to 11,
        "and" to 8,
        "xor" to 7,
        "or" to 6,
        "literal" to 15
    )

    override fun visit(node: Identifier): String = node.name

    override fun visit(node: NumberLiteral): String = node.value.toString()

    override fun visit(node: Parenthesized): String {
        val inner = node.expr.accept(this)
        return if (node.expr is Identifier || node.expr is NumberLiteral) inner
        else "($inner)"
    }

    override fun visit(node: UnaryOp): String {
        val operand = wrapOperand(node.operand, getPriority("unary"))
        return when (node.operator) {
            "~" -> "$operand.inv()"
            else -> "${node.operator}$operand"
        }
    }

    override fun visit(node: BinaryOp): String {
        val kotlinOp = when (node.operator) {
            "&" -> "and"
            "|" -> "or"
            "^" -> "xor"
            "<<" -> "shl"
            ">>" -> "shr"
            ">>>" -> "ushr"
            else -> node.operator
        }
        val opPriority = getPriority(kotlinOp)
        val left = wrapOperand(node.left, opPriority)
        val right = wrapOperand(node.right, opPriority)
        return "$left $kotlinOp $right"
    }

    override fun visit(node: AssignmentOp): String {
        val kotlinOp = when (node.operator) {
            "&" -> "and"
            "|" -> "or"
            "^" -> "xor"
            "<<" -> "shl"
            ">>" -> "shr"
            ">>>" -> "ushr"
            else -> node.operator
        }
        val value = wrapOperand(node.value, getPriority(kotlinOp))
        return "${node.target.accept(this)} = ${node.target.accept(this)} $kotlinOp $value"
    }

    private fun getPriority(op: String): Int = operatorPrecedence[op.lowercase()] ?: Int.MIN_VALUE

    private fun getNodePrecedence(node: ExprNode): Int = when (node) {
        is Identifier -> getPriority("literal")
        is NumberLiteral -> getPriority("literal")
        is UnaryOp -> getPriority("unary")
        is BinaryOp -> getPriority(
            when (node.operator) {
                "&" -> "and"
                "|" -> "or"
                "^" -> "xor"
                "<<" -> "shl"
                ">>" -> "shr"
                ">>>" -> "ushr"
                else -> node.operator
            }
        )
        else -> Int.MIN_VALUE
    }

    private fun wrapOperand(node: ExprNode, parentPrecedence: Int): String {
        val currentPrecedence = getNodePrecedence(node)
        val expr = node.accept(this)
        return if (currentPrecedence < parentPrecedence ||
            (node is BinaryOp && currentPrecedence == parentPrecedence)) {
            "($expr)"
        } else {
            expr
        }
    }
}

