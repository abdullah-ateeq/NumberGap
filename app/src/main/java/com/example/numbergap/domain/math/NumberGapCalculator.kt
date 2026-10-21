package com.example.numbergap.domain.math

import com.example.numbergap.data.model.CalculationStep
import com.example.numbergap.data.model.ComparisonResult
import com.example.numbergap.data.model.NumberGapResult
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object NumberGapCalculator {

    val CALCULATION_CONTEXT = MathContext(50, RoundingMode.HALF_UP)
    private val TWO = BigDecimal("2")
    private val HUNDRED = BigDecimal("100")

    fun calculateSignedDifference(a: BigDecimal, b: BigDecimal): BigDecimal {
        return a.subtract(b, CALCULATION_CONTEXT)
    }

    fun calculateAbsoluteGap(a: BigDecimal, b: BigDecimal): BigDecimal {
        return a.subtract(b, CALCULATION_CONTEXT).abs(CALCULATION_CONTEXT)
    }

    fun calculateSum(a: BigDecimal, b: BigDecimal): BigDecimal {
        return a.add(b, CALCULATION_CONTEXT)
    }

    fun calculateAverage(a: BigDecimal, b: BigDecimal): BigDecimal {
        val sum = a.add(b, CALCULATION_CONTEXT)
        return sum.divide(TWO, CALCULATION_CONTEXT)
    }

    fun calculatePercentageDifference(a: BigDecimal, b: BigDecimal): BigDecimal {
        val absA = a.abs(CALCULATION_CONTEXT)
        val absB = b.abs(CALCULATION_CONTEXT)
        val sumOfMagnitudes = absA.add(absB, CALCULATION_CONTEXT)

        if (sumOfMagnitudes.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO
        }

        val averageMagnitude = sumOfMagnitudes.divide(TWO, CALCULATION_CONTEXT)
        val gap = calculateAbsoluteGap(a, b)

        return gap.divide(averageMagnitude, CALCULATION_CONTEXT).multiply(HUNDRED, CALCULATION_CONTEXT)
    }

    fun compareNumbers(a: BigDecimal, b: BigDecimal): ComparisonResult {
        val cmp = a.compareTo(b)
        return when {
            cmp > 0 -> ComparisonResult.A_GREATER
            cmp < 0 -> ComparisonResult.B_GREATER
            else -> ComparisonResult.EQUAL
        }
    }

    fun calculateLarger(a: BigDecimal, b: BigDecimal): BigDecimal {
        return if (a.compareTo(b) >= 0) a else b
    }

    fun calculateSmaller(a: BigDecimal, b: BigDecimal): BigDecimal {
        return if (a.compareTo(b) <= 0) a else b
    }

    fun generateCalculationSteps(
        a: BigDecimal,
        b: BigDecimal,
        signedDifference: BigDecimal,
        absoluteGap: BigDecimal
    ): List<CalculationStep> {
        val aStr = a.toPlainString()
        val bStr = b.toPlainString()
        val diffStr = signedDifference.toPlainString()
        val gapStr = absoluteGap.toPlainString()

        return listOf(
            CalculationStep(
                label = "Difference",
                expression = "$aStr − $bStr",
                result = diffStr
            ),
            CalculationStep(
                label = "Absolute Gap",
                expression = "|$diffStr|",
                result = gapStr
            )
        )
    }

    fun calculate(a: BigDecimal, b: BigDecimal, timestamp: Long = System.currentTimeMillis()): NumberGapResult {
        val signedDiff = calculateSignedDifference(a, b)
        val absGap = calculateAbsoluteGap(a, b)
        val larger = calculateLarger(a, b)
        val smaller = calculateSmaller(a, b)
        val sum = calculateSum(a, b)
        val average = calculateAverage(a, b)
        val percentageDiff = calculatePercentageDifference(a, b)
        val comparison = compareNumbers(a, b)
        val steps = generateCalculationSteps(a, b, signedDiff, absGap)

        return NumberGapResult(
            numberA = a,
            numberB = b,
            signedDifference = signedDiff,
            absoluteGap = absGap,
            largerNumber = larger,
            smallerNumber = smaller,
            sum = sum,
            average = average,
            percentageDifference = percentageDiff,
            comparison = comparison,
            calculationSteps = steps,
            calculatedAt = timestamp
        )
    }
}
