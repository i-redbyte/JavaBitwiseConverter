package org.redbyte


class BitwiseConverter(private val javaCode: String) {
    private val tokens = mutableListOf<String>()
    private var currentPosition = 0

    init {
        tokenize()
    }

    private fun tokenize() {
        val regex = Regex(
            """(\s+)|(>>>|<<|>>)|([(){}[\\]<>=+*/%&|^~!,;-])|(\d+)|(\w+)"""
        )
        tokens.addAll(
            regex.findAll(javaCode)
                .map { it.value }
                .filter { it.isNotBlank() }
        )
    }
    private fun currentToken(): String? {
        return if (currentPosition < tokens.size) tokens[currentPosition] else null
    }

    private fun consumeToken() {
        currentPosition++
    }

    fun convert(): String {
        val expr = parseExpression()
        //TODO: for logs
//        println("Parsed Expression: $expr")
        return generateKotlinExpr(expr)
    }

    private fun parseExpression(): ExprNode = parseBitwiseOr()

    private fun parseBitwiseOr(): ExprNode {
        var node = parseBitwiseXor()
        while (currentToken() == "|") {
            consumeToken()
            node = BinaryOp("|", node, parseBitwiseXor())
        }
        return node
    }

    private fun parseBitwiseXor(): ExprNode {
        var node = parseBitwiseAnd()
        while (currentToken() == "^") {
            consumeToken()
            node = BinaryOp("^", node, parseBitwiseAnd())
        }
        return node
    }

    private fun parseBitwiseAnd(): ExprNode {
        var node = parseShift()
        while (currentToken() == "&") {
            consumeToken()
            node = BinaryOp("&", node, parseShift())
        }
        return node
    }

    private fun parseShift(): ExprNode {
        var node = parseUnary()
        while (true) {
            when (val token = currentToken()) {
                "<<", ">>", ">>>" -> {
                    consumeToken()
                    node = BinaryOp(token, node, parseUnary())
                }
                else -> break
            }
        }
        return node
    }

    private fun parseUnary(): ExprNode {
        return when (currentToken()) {
            "~", "!", "-", "+" -> {
                val op = currentToken()!!
                consumeToken()
                UnaryOp(op, parseUnary())
            }

            else -> parsePrimary()
        }
    }

    private fun parsePrimary(): ExprNode {
        return when (val token = currentToken()) {
            "(" -> {
                consumeToken()
                val expr = parseExpression()
                if (currentToken() != ")") throw IllegalArgumentException("Missing ')'")
                consumeToken()
                Parenthesized(expr)
            }

            else -> {
                consumeToken()
                if (token!!.matches(Regex("[0-9]+"))) NumberLiteral(token.toInt())
                else Identifier(token)
            }
        }
    }

    private fun generateKotlinExpr(node: ExprNode): String = when (node) {
        is Identifier -> node.name
        is NumberLiteral -> node.value.toString()
        is Parenthesized -> "(${generateKotlinExpr(node.expr)})"
        is UnaryOp -> when (node.operator) {
            "~" -> "${generateOperand(node.operand, 2)}.inv()"
            else -> "${node.operator}${generateOperand(node.operand, 2)}"
        }

        is BinaryOp -> {
            val kotlinOp = when (node.operator) {
                "&" -> "and"
                "|" -> "or"
                "^" -> "xor"
                "<<" -> "shl"
                ">>" -> "shr"
                ">>>" -> "ushr"
                else -> node.operator
            }
            "${generateOperand(node.left, getPriority(kotlinOp))} $kotlinOp ${
                generateOperand(
                    node.right,
                    getPriority(kotlinOp) + 1
                )
            }"
        }
    }

    private fun generateOperand(node: ExprNode, parentPriority: Int): String {
        val currentPriority = getPriorityForNode(node)
        val expr = generateKotlinExpr(node)
        return when (node) {
            is BinaryOp -> if (currentPriority < parentPriority) "($expr)" else expr
            is UnaryOp -> expr // Unary expressions do not need parentheses
            else -> expr
        }
    }

    private fun getPriority(op: String): Int = when (op) {
        "shl", "shr", "ushr" -> 4
        "and" -> 7
        "xor" -> 8
        "or" -> 9
        else -> Int.MAX_VALUE
    }

    private fun getPriorityForNode(node: ExprNode): Int = when (node) {
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

        is UnaryOp -> 2
        else -> Int.MAX_VALUE
    }

    private fun currentAndNextToken(): Pair<String?, String?> {
        val current = currentToken()
        val next = if (currentPosition + 1 < tokens.size) tokens[currentPosition + 1] else null
        return Pair(current, next)
    }
}