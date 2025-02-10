package org.redbyte

sealed class ExprNode {
    override fun toString(): String {
        return when (this) {
            is Identifier -> "Identifier($name)"
            is NumberLiteral -> "NumberLiteral($value)"
            is Parenthesized -> "Parenthesized($expr)"
            is UnaryOp -> "UnaryOp($operator, $operand)"
            is BinaryOp -> "BinaryOp($operator, $left, $right)"
        }
    }
}

data class Identifier(val name: String) : ExprNode()
data class NumberLiteral(val value: Int) : ExprNode()
data class Parenthesized(val expr: ExprNode) : ExprNode()
data class UnaryOp(val operator: String, val operand: ExprNode) : ExprNode()
data class BinaryOp(val operator: String, val left: ExprNode, val right: ExprNode) : ExprNode()
