package org.redbyte

fun main() {
    val javaExpression = " a | b << c & ~d ^ e >> f & g | h >> i & j"
    val converter = BitwiseConverter(javaExpression)
    println("Java: $javaExpression")
    println("Kotlin: ${converter.convert()}")
}