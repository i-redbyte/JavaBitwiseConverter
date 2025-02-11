package org.redbyte.parser

import org.redbyte.ast.ExprNode
import org.redbyte.tokenizer.Token

interface Parser {
    fun parse(tokens: List<Token>): ExprNode
}