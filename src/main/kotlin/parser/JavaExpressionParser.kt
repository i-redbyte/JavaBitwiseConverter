package org.redbyte.parser

import org.redbyte.ast.*
import org.redbyte.tokenizer.Token

class JavaExpressionParser : Parser {
    private lateinit var tokens: List<Token>
    private var pos = 0

    override fun parse(tokens: List<Token>): ExprNode {
        this.tokens = tokens
        this.pos = 0
        return parseExpression()
    }

    private fun currentToken(): Token? = if (pos < tokens.size) tokens[pos] else null
    private fun consume() {
        pos++
    }

    private fun parseExpression(): ExprNode = parseAssignment()

    private fun parseAssignment(): ExprNode {
        val target = parseBitwiseOr()

        currentToken()?.value?.let { token ->
            if (token.matches(Regex("[+\\-*/%&|^]="))) {
                consume()
                return AssignmentOp(token.dropLast(1), target, parseExpression())
            }
            if (token.matches(Regex("(<<|>>|>>>)="))) {
                consume()
                return AssignmentOp(token.dropLast(1), target, parseExpression())
            }
        }
        return target
    }

    private fun parseBitwiseOr(): ExprNode {
        var node = parseBitwiseXor()
        while (currentToken()?.value == "|") {
            consume()
            node = BinaryOp("|", node, parseBitwiseXor())
        }
        return node
    }

    private fun parseBitwiseXor(): ExprNode {
        var node = parseBitwiseAnd()
        while (currentToken()?.value == "^") {
            consume()
            node = BinaryOp("^", node, parseBitwiseAnd())
        }
        return node
    }

    private fun parseBitwiseAnd(): ExprNode {
        var node = parseShift()
        while (currentToken()?.value == "&") {
            consume()
            node = BinaryOp("&", node, parseShift())
        }
        return node
    }

    private fun parseShift(): ExprNode {
        var node = parseUnary()
        while (true) {
            when (currentToken()?.value) {
                "<<", ">>", ">>>" -> {
                    val op = currentToken()!!.value
                    consume()
                    node = BinaryOp(op, node, parseUnary())
                }

                else -> break
            }
        }
        return node
    }

    private fun parseUnary(): ExprNode {
        currentToken()?.value?.let { token ->
            if (token in setOf("~", "!", "-", "+")) {
                consume()
                return UnaryOp(token, parseUnary())
            }
        }
        return parsePrimary()
    }

    private fun parsePrimary(): ExprNode {
        val token = currentToken() ?: throw IllegalArgumentException("Unexpected end of input")

        return when (token.value) {
            "(" -> {
                consume()
                val expr = parseExpression()
                if (currentToken()?.value != ")") {
                    throw IllegalArgumentException("Missing ')' at position ${token.position}")
                }
                consume()
                Parenthesized(expr)
            }

            else -> {
                consume()
                when {
                    token.value.matches(Regex("\\d+")) -> {
                        NumberLiteral(token.value.toInt())
                    }
                    else -> Identifier(token.value)
                }
            }
        }
    }
}