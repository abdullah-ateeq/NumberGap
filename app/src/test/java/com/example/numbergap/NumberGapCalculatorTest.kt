package com.example.numbergap

import com.example.numbergap.data.model.ComparisonResult
import com.example.numbergap.domain.math.NumberGapCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class NumberGapCalculatorTest {

    @Test
    fun testBasicCalculation() {
        val a = BigDecimal("125")
        val b = BigDecimal("80")

        val result = NumberGapCalculator.calculate(a, b)

        assertEquals(0, BigDecimal("45").compareTo(result.signedDifference))
        assertEquals(0, BigDecimal("45").compareTo(result.absoluteGap))
        assertEquals(0, BigDecimal("125").compareTo(result.largerNumber))
        assertEquals(0, BigDecimal("80").compareTo(result.smallerNumber))
        assertEquals(0, BigDecimal("205").compareTo(result.sum))
        assertEquals(0, BigDecimal("102.5").compareTo(result.average))
        assertEquals(ComparisonResult.A_GREATER, result.comparison)

        // Percentage difference: 45 / 102.5 * 100 = 43.902439...%
        // Check first 4 decimal places
        val expectedPctPrefix = "43.9024"
        assertTrue(result.percentageDifference.toPlainString().startsWith(expectedPctPrefix))
    }

    @Test
    fun testReversedValues() {
        val a = BigDecimal("80")
        val b = BigDecimal("125")

        val result = NumberGapCalculator.calculate(a, b)

        assertEquals(0, BigDecimal("-45").compareTo(result.signedDifference))
        assertEquals(0, BigDecimal("45").compareTo(result.absoluteGap))
        assertEquals(0, BigDecimal("125").compareTo(result.largerNumber))
        assertEquals(0, BigDecimal("80").compareTo(result.smallerNumber))
        assertEquals(ComparisonResult.B_GREATER, result.comparison)
    }

    @Test
    fun testEqualValues() {
        val a = BigDecimal("100")
        val b = BigDecimal("100")

        val result = NumberGapCalculator.calculate(a, b)

        assertEquals(0, BigDecimal.ZERO.compareTo(result.signedDifference))
        assertEquals(0, BigDecimal.ZERO.compareTo(result.absoluteGap))
        assertEquals(0, BigDecimal("100").compareTo(result.largerNumber))
        assertEquals(0, BigDecimal("100").compareTo(result.smallerNumber))
        assertEquals(0, BigDecimal("100").compareTo(result.average))
        assertEquals(0, BigDecimal.ZERO.compareTo(result.percentageDifference))
        assertEquals(ComparisonResult.EQUAL, result.comparison)
    }

    @Test
    fun testNegativeAndPositiveValues() {
        val a = BigDecimal("-10")
        val b = BigDecimal("10")

        val result = NumberGapCalculator.calculate(a, b)

        assertEquals(0, BigDecimal("-20").compareTo(result.signedDifference))
        assertEquals(0, BigDecimal("20").compareTo(result.absoluteGap))
        assertEquals(0, BigDecimal("10").compareTo(result.largerNumber))
        assertEquals(0, BigDecimal("-10").compareTo(result.smallerNumber))
        assertEquals(0, BigDecimal.ZERO.compareTo(result.sum))
        assertEquals(0, BigDecimal.ZERO.compareTo(result.average))

        // Percentage difference: 20 / ((10 + 10) / 2) * 100 = 200%
        assertEquals(0, BigDecimal("200").compareTo(result.percentageDifference))
        assertEquals(ComparisonResult.B_GREATER, result.comparison)
    }

    @Test
    fun testBothNegativeValues() {
        val a = BigDecimal("-10")
        val b = BigDecimal("-25")

        val result = NumberGapCalculator.calculate(a, b)

        assertEquals(0, BigDecimal("15").compareTo(result.signedDifference))
        assertEquals(0, BigDecimal("15").compareTo(result.absoluteGap))
        assertEquals(0, BigDecimal("-10").compareTo(result.largerNumber))
        assertEquals(0, BigDecimal("-25").compareTo(result.smallerNumber))
        assertEquals(0, BigDecimal("-35").compareTo(result.sum))
        assertEquals(0, BigDecimal("-17.5").compareTo(result.average))

        // Percentage difference: 15 / ((10 + 25) / 2) * 100 = 15 / 17.5 * 100 = 85.714285...%
        val expectedPctPrefix = "85.7142"
        assertTrue(result.percentageDifference.toPlainString().startsWith(expectedPctPrefix))
        assertEquals(ComparisonResult.A_GREATER, result.comparison)
    }

    @Test
    fun testDecimals() {
        val a = BigDecimal("12.75")
        val b = BigDecimal("5.25")

        val result = NumberGapCalculator.calculate(a, b)

        assertEquals(0, BigDecimal("7.5").compareTo(result.signedDifference))
        assertEquals(0, BigDecimal("7.5").compareTo(result.absoluteGap))
        assertEquals(0, BigDecimal("18").compareTo(result.sum))
        assertEquals(0, BigDecimal("9").compareTo(result.average))
    }

    @Test
    fun testVerySmallValues() {
        val a = BigDecimal("0.000001")
        val b = BigDecimal("0.000004")

        val gap = NumberGapCalculator.calculateAbsoluteGap(a, b)
        assertEquals(0, BigDecimal("0.000003").compareTo(gap))
    }

    @Test
    fun testScientificNotationValues() {
        val a = BigDecimal("100000") // 1E+5
        val b = BigDecimal("50000")  // 5E+4

        val gap = NumberGapCalculator.calculateAbsoluteGap(a, b)
        assertEquals(0, BigDecimal("50000").compareTo(gap))
    }

    @Test
    fun testVeryLargeValuesWithoutOverflow() {
        val a = BigDecimal("999999999999999999999999999999")
        val b = BigDecimal("1")

        val gap = NumberGapCalculator.calculateAbsoluteGap(a, b)
        assertEquals(0, BigDecimal("999999999999999999999999999998").compareTo(gap))
    }

    @Test
    fun testZeroValues() {
        val a = BigDecimal("0")
        val b = BigDecimal("25")

        val result = NumberGapCalculator.calculate(a, b)
        assertEquals(0, BigDecimal("-25").compareTo(result.signedDifference))
        assertEquals(0, BigDecimal("25").compareTo(result.absoluteGap))
    }

    @Test
    fun testPercentageZeroCaseNoDivisionByZero() {
        val a = BigDecimal("0")
        val b = BigDecimal("0")

        val pct = NumberGapCalculator.calculatePercentageDifference(a, b)
        assertEquals(0, BigDecimal.ZERO.compareTo(pct))
    }

    @Test
    fun testNoIntermediateRounding() {
        val a = BigDecimal("10.000000000000000000000000000000000000000000000001")
        val b = BigDecimal("10.000000000000000000000000000000000000000000000000")

        val gap = NumberGapCalculator.calculateAbsoluteGap(a, b)
        assertEquals(
            BigDecimal("0.000000000000000000000000000000000000000000000001"),
            gap
        )
    }

    @Test
    fun testCalculationSteps() {
        val a = BigDecimal("125")
        val b = BigDecimal("80")
        val diff = NumberGapCalculator.calculateSignedDifference(a, b)
        val gap = NumberGapCalculator.calculateAbsoluteGap(a, b)

        val steps = NumberGapCalculator.generateCalculationSteps(a, b, diff, gap)
        assertEquals(2, steps.size)
        assertEquals("Difference", steps[0].label)
        assertEquals("125 − 80", steps[0].expression)
        assertEquals("45", steps[0].result)

        assertEquals("Absolute Gap", steps[1].label)
        assertEquals("|45|", steps[1].expression)
        assertEquals("45", steps[1].result)
    }
}
