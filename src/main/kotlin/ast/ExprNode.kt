package org.redbyte.ast

sealed class ExprNode {
    abstract fun accept(visitor: ExprVisitor): String
}

data class Identifier(val name: String) : ExprNode() {
    override fun accept(visitor: ExprVisitor) = visitor.visit(this)
}

data class NumberLiteral(val value: Int) : ExprNode() {
    override fun accept(visitor: ExprVisitor) = visitor.visit(this)
}

data class Parenthesized(val expr: ExprNode) : ExprNode() {
    override fun accept(visitor: ExprVisitor) = visitor.visit(this)
}

data class UnaryOp(val operator: String, val operand: ExprNode) : ExprNode() {
    override fun accept(visitor: ExprVisitor) = visitor.visit(this)
}

data class BinaryOp(val operator: String, val left: ExprNode, val right: ExprNode) : ExprNode() {
    override fun accept(visitor: ExprVisitor) = visitor.visit(this)
}

data class AssignmentOp(val operator: String, val target: ExprNode, val value: ExprNode) : ExprNode() {
    override fun accept(visitor: ExprVisitor) = visitor.visit(this)
}
