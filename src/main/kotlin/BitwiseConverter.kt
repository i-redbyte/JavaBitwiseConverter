package org.redbyte

import org.redbyte.ast.*
import org.redbyte.generator.KotlinCodeGenerator
import org.redbyte.parser.JavaExpressionParser
import org.redbyte.parser.Parser
import org.redbyte.tokenizer.JavaTokenizer
import org.redbyte.tokenizer.Tokenizer

class BitwiseConverter(
    private val tokenizer: Tokenizer = JavaTokenizer(),
    private val parser: Parser = JavaExpressionParser(),
    private val codeGenerator: ExprVisitor = KotlinCodeGenerator()
) {
    fun convert(javaCode: String): String {
        val tokens = tokenizer.tokenize(javaCode)
        val ast = parser.parse(tokens)
        return ast.accept(codeGenerator)
    }
}
