package org.redbyte

fun main() {
    val javaExpression = " a >>> 1 & b << c >> 2 "
    val converter = BitwiseConverter(javaExpression)
    println("Java: $javaExpression")
    println("Kotlin: ${converter.convert()}")
}