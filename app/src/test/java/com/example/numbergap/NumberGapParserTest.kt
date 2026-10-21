package com.example.numbergap

import com.example.numbergap.domain.math.NumberGapParser
import com.example.numbergap.domain.math.NumberGapValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class NumberGapParserTest {

    @Test
    fun testParsePositiveInteger() {
        val result = NumberGapParser.parseNumber("100")
        assertTrue(result.isSuccess)
        assertEquals(BigDecimal("100"), result.getOrThrow())
    }

    @Test
    fun testParseNegativeNumber() {
        val result = NumberGapParser.parseNumber("-50")
        assertTrue(result.isSuccess)
        assertEquals(BigDecimal("-50"), result.getOrThrow())
    }

    @Test
    fun testParseZero() {
        val result = NumberGapParser.parseNumber("0")
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow().compareTo(BigDecimal.ZERO))
    }

    @Test
    fun testParseDecimalNumbers() {
        val r1 = NumberGapParser.parseNumber("12.75")
        assertTrue(r1.isSuccess)
        assertEquals(BigDecimal("12.75"), r1.getOrThrow())

        val r2 = NumberGapParser.parseNumber("0.000001")
        assertTrue(r2.isSuccess)
        assertEquals(BigDecimal("0.000001"), r2.getOrThrow())
    }

    @Test
    fun testParseLeadingAndTrailingDots() {
        val r1 = NumberGapParser.parseNumber(".5")
        assertTrue(r1.isSuccess)
        assertEquals(0, r1.getOrThrow().compareTo(BigDecimal("0.5")))

        val r2 = NumberGapParser.parseNumber("10.")
        assertTrue(r2.isSuccess)
        assertEquals(0, r2.getOrThrow().compareTo(BigDecimal("10.0")))

        val r3 = NumberGapParser.parseNumber("-.5")
        assertTrue(r3.isSuccess)
        assertEquals(0, r3.getOrThrow().compareTo(BigDecimal("-0.5")))

        val r4 = NumberGapParser.parseNumber("+10.")
        assertTrue(r4.isSuccess)
        assertEquals(0, r4.getOrThrow().compareTo(BigDecimal("10.0")))
    }

    @Test
    fun testParseExplicitPlusSign() {
        val result = NumberGapParser.parseNumber("+25")
        assertTrue(result.isSuccess)
        assertEquals(BigDecimal("25"), result.getOrThrow())
    }

    @Test
    fun testParseScientificNotation() {
        val r1 = NumberGapParser.parseNumber("1E+5")
        assertTrue(r1.isSuccess)
        assertEquals(0, r1.getOrThrow().compareTo(BigDecimal("100000")))

        val r2 = NumberGapParser.parseNumber("1.5E-3")
        assertTrue(r2.isSuccess)
        assertEquals(0, r2.getOrThrow().compareTo(BigDecimal("0.0015")))

        val r3 = NumberGapParser.parseNumber("-2.5E+8")
        assertTrue(r3.isSuccess)
        assertEquals(0, r3.getOrThrow().compareTo(BigDecimal("-250000000")))
    }

    @Test
    fun testParseVeryLargeNumbers() {
        val huge = "999999999999999999999999999999999999999"
        val result = NumberGapParser.parseNumber(huge)
        assertTrue(result.isSuccess)
        assertEquals(BigDecimal(huge), result.getOrThrow())
    }

    @Test
    fun testParseVerySmallNumbers() {
        val tiny = "0.000000000001"
        val result = NumberGapParser.parseNumber(tiny)
        assertTrue(result.isSuccess)
        assertEquals(BigDecimal(tiny), result.getOrThrow())
    }

    @Test
    fun testParseEmptyInput() {
        val result = NumberGapParser.parseNumber("")
        assertTrue(result.isFailure)
        assertEquals(NumberGapValidator.ERROR_INVALID_NUMBER, result.exceptionOrNull()?.message)
    }

    @Test
    fun testParseWhitespaceInput() {
        val result = NumberGapParser.parseNumber("   ")
        assertTrue(result.isFailure)
        assertEquals(NumberGapValidator.ERROR_INVALID_NUMBER, result.exceptionOrNull()?.message)
    }

    @Test
    fun testParseInvalidInputs() {
        val invalidInputs = listOf("abc", "12..5", "1.2.3", "--", "++", "1E", "E5", "+-5", ".")
        for (input in invalidInputs) {
            val result = NumberGapParser.parseNumber(input)
            assertTrue("Expected failure for '$input'", result.isFailure)
            assertEquals(NumberGapValidator.ERROR_INVALID_NUMBER, result.exceptionOrNull()?.message)
        }
    }

    @Test
    fun testParseLengthExceeds500() {
        val longString = "1".repeat(501)
        val result = NumberGapParser.parseNumber(longString)
        assertTrue(result.isFailure)
        assertEquals(NumberGapValidator.ERROR_TOO_LONG, result.exceptionOrNull()?.message)
    }
}
