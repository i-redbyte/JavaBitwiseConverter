package org.redbyte.ast

interface ExprVisitor {
    fun visit(node: Identifier): String
    fun visit(node: NumberLiteral): String
    fun visit(node: Parenthesized): String
    fun visit(node: UnaryOp): String
    fun visit(node: BinaryOp): String
    fun visit(node: AssignmentOp): String
}