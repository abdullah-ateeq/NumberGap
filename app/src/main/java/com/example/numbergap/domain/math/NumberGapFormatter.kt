package com.example.numbergap.domain.math

import com.example.numbergap.data.model.AppSettings
import com.example.numbergap.data.model.ComparisonResult
import com.example.numbergap.data.model.NumberGapResult
import java.math.BigDecimal
import java.math.RoundingMode

object NumberGapFormatter {

    fun formatNumber(
        value: BigDecimal,
        decimalPlaces: Int = 4,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return "0"
        }

        val rounded = try {
            value.setScale(decimalPlaces, roundingMode)
        } catch (_: Exception) {
            value
        }

        if (rounded.compareTo(BigDecimal.ZERO) == 0) {
            return "0"
        }

        val stripped = rounded.stripTrailingZeros()
        return stripped.toPlainString()
    }

    fun formatSignedDifference(
        value: BigDecimal,
        decimalPlaces: Int = 4,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val formatted = formatNumber(value, decimalPlaces, roundingMode)
        return if (value.compareTo(BigDecimal.ZERO) > 0 && formatted != "0") {
            "+$formatted"
        } else {
            formatted
        }
    }

    fun formatPercentageDifference(
        value: BigDecimal,
        decimalPlaces: Int = 4,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val formatted = formatNumber(value, decimalPlaces, roundingMode)
        return "$formatted%"
    }

    fun formatComparisonText(
        result: NumberGapResult,
        decimalPlaces: Int = 4,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val gapFormatted = formatNumber(result.absoluteGap, decimalPlaces, roundingMode)
        return when (result.comparison) {
            ComparisonResult.A_GREATER -> "Number A is $gapFormatted greater than Number B."
            ComparisonResult.B_GREATER -> "Number B is $gapFormatted greater than Number A."
            ComparisonResult.EQUAL -> "Both numbers are equal."
        }
    }

    fun buildSummaryText(result: NumberGapResult, settings: AppSettings): String {
        val dp = settings.decimalPlaces
        val rm = settings.roundingMode.roundingMode

        val aStr = formatNumber(result.numberA, dp, rm)
        val bStr = formatNumber(result.numberB, dp, rm)
        val gapStr = formatNumber(result.absoluteGap, dp, rm)
        val signedStr = formatSignedDifference(result.signedDifference, dp, rm)
        val largerStr = formatNumber(result.largerNumber, dp, rm)
        val smallerStr = formatNumber(result.smallerNumber, dp, rm)
        val sumStr = formatNumber(result.sum, dp, rm)
        val avgStr = formatNumber(result.average, dp, rm)
        val pctStr = formatPercentageDifference(result.percentageDifference, dp, rm)

        return buildString {
            appendLine("NumberGap")
            appendLine()
            appendLine("Number A: $aStr")
            appendLine("Number B: $bStr")
            appendLine()
            appendLine("Gap: $gapStr")
            appendLine("Signed Difference: $signedStr")
            appendLine()
            appendLine("Larger Number: $largerStr")
            appendLine("Smaller Number: $smallerStr")
            appendLine()
            appendLine("Sum: $sumStr")
            appendLine("Average: $avgStr")
            appendLine()
            append("Percentage Difference: $pctStr")
        }
    }
}
