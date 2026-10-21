package com.example.numbergap.data.model

import java.math.BigDecimal

data class NumberGapResult(
    val numberA: BigDecimal,
    val numberB: BigDecimal,
    val signedDifference: BigDecimal,
    val absoluteGap: BigDecimal,
    val largerNumber: BigDecimal,
    val smallerNumber: BigDecimal,
    val sum: BigDecimal,
    val average: BigDecimal,
    val percentageDifference: BigDecimal,
    val comparison: ComparisonResult,
    val calculationSteps: List<CalculationStep>,
    val calculatedAt: Long
)
