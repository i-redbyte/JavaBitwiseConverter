package org.redbyte.tokenizer

interface Tokenizer {
    fun tokenize(code: String): List<Token>
}