package org.redbyte

fun main() {
    val converter = BitwiseConverter()
    val testExpressions = listOf(
        "a >>> 1 & b << c >> 2" to "a ushr 1 and (b shl c shr 2)",
        "x ^= y | z & 0xFF" to "x = x xor (y or (z and 0xFF))",
        "~(a << 3) | (b >> 2)" to "(a shl 3).inv() or (b shr 2)",
        "a & b | c ^ d" to "a and b or (c xor d)",
        "a | b & c | d" to "a or (b and c) or d",
        "a ^ b ^ c" to "a xor b xor c",
        "a & b ^ c | d" to "a and b xor c or d",
        "a << b & c" to "a shl b and c",
        "a | b ^ c << d & e" to "a or (b xor (c shl d and e))",
        "~a & b" to "a.inv() and b",
        "~(a & b) | c" to "(a and b).inv() or c",
        "a ^ ~b & c" to "a xor (b.inv() and c)",
        "x &= y << 2" to "x = x and (y shl 2)",
        "x |= ~y ^ z" to "x = x or (y.inv() xor z)",
        "a ^ b & c ^ d" to "a xor (b and c) xor d"
    )
    println("RUN ${testExpressions.size} tests:")
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