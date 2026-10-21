package com.example.numbergap

import com.example.numbergap.data.model.AppSettings
import com.example.numbergap.domain.math.NumberGapCalculator
import com.example.numbergap.domain.math.NumberGapFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class NumberGapFormatterTest {

    @Test
    fun testTrailingZerosRemoval() {
        val v1 = BigDecimal("45.0000")
        assertEquals("45", NumberGapFormatter.formatNumber(v1, 4))

        val v2 = BigDecimal("102.5000")
        assertEquals("102.5", NumberGapFormatter.formatNumber(v2, 4))

        val v3 = BigDecimal("0.2500")
        assertEquals("0.25", NumberGapFormatter.formatNumber(v3, 4))

        val v4 = BigDecimal("0.000100")
        assertEquals("0.0001", NumberGapFormatter.formatNumber(v4, 4))
    }

    @Test
    fun testRoundingModes() {
        val value = BigDecimal("2.555")

        // Half Up to 2 decimal places -> 2.56
        assertEquals("2.56", NumberGapFormatter.formatNumber(value, 2, RoundingMode.HALF_UP))

        // Down / Floor to 2 decimal places -> 2.55
        assertEquals("2.55", NumberGapFormatter.formatNumber(value, 2, RoundingMode.DOWN))

        // Ceiling / Up to 2 decimal places -> 2.56
        assertEquals("2.56", NumberGapFormatter.formatNumber(value, 2, RoundingMode.CEILING))
    }

    @Test
    fun testSignedDifferenceFormatting() {
        val positive = BigDecimal("45")
        assertEquals("+45", NumberGapFormatter.formatSignedDifference(positive, 4))

        val negative = BigDecimal("-40")
        assertEquals("-40", NumberGapFormatter.formatSignedDifference(negative, 4))

        val zero = BigDecimal.ZERO
        assertEquals("0", NumberGapFormatter.formatSignedDifference(zero, 4))
    }

    @Test
    fun testPercentageFormatting() {
        val pct = BigDecimal("43.902439")
        assertEquals("43.9024%", NumberGapFormatter.formatPercentageDifference(pct, 4))

        val zeroPct = BigDecimal.ZERO
        assertEquals("0%", NumberGapFormatter.formatPercentageDifference(zeroPct, 4))
    }

    @Test
    fun testComparisonText() {
        val a = BigDecimal("125")
        val b = BigDecimal("80")
        val result = NumberGapCalculator.calculate(a, b)

        val text = NumberGapFormatter.formatComparisonText(result, 4)
        assertEquals("Number A is 45 greater than Number B.", text)
    }

    @Test
    fun testSummaryTextContainsAllSections() {
        val a = BigDecimal("125")
        val b = BigDecimal("80")
        val result = NumberGapCalculator.calculate(a, b)
        val settings = AppSettings()

        val summary = NumberGapFormatter.buildSummaryText(result, settings)

        assertTrue(summary.contains("NumberGap"))
        assertTrue(summary.contains("Number A: 125"))
        assertTrue(summary.contains("Number B: 80"))
        assertTrue(summary.contains("Gap: 45"))
        assertTrue(summary.contains("Signed Difference: +45"))
        assertTrue(summary.contains("Larger Number: 125"))
        assertTrue(summary.contains("Smaller Number: 80"))
        assertTrue(summary.contains("Sum: 205"))
        assertTrue(summary.contains("Average: 102.5"))
        assertTrue(summary.contains("Percentage Difference: 43.9024%"))
    }
}
