package org.redbyte

import org.redbyte.generator.KotlinCodeGenerator


fun main() {
    val converter = BitwiseConverter()

    val testExpressions = listOf(
        "a >>> 1 & b << c >> 2" to "a ushr 1 and (b shl c shr 2)",
        "x ^= y | z & 0xFF" to "x = x xor (y or (z and 0xFF))",
        "~(a << 3) | (b >> 2)" to "(a shl 3).inv() or (b shr 2)"
    )

    for ((java, expected) in testExpressions) {
        val result = converter.convert(java)
        println("Java:   $java")
        println("Kotlin: $result")
        check(result == expected) {
            "Test failed!\nExpected: $expected\nActual:   $result"
        }
    }

    println("All tests passed!")
}