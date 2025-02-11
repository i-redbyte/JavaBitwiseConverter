package org.redbyte.tokenizer

class JavaTokenizer : Tokenizer {
    override fun tokenize(code: String): List<Token> {
        val regex = Regex(
            """(\s+)|(>>>=|<<=|>>=|\^=|\|=|&=|>>>|<<|>>|&&|\|\||[(){}[\\]<>=+*/%&|^~!,;-])|(\d+)|(\w+)"""
        )

        val tokens = mutableListOf<Token>()
        var pos = 0

        regex.findAll(code).forEach { matchResult ->
            val value = matchResult.value
            if (value.isNotBlank() && !value.matches(Regex("\\s+"))) {
                tokens.add(Token(value, pos))
            }
            pos += value.length
        }

        return tokens
    }
}