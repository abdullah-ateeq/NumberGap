package com.example.numbergap.domain.math

import java.math.BigDecimal

object NumberGapParser {

    fun parseNumber(input: String): Result<BigDecimal> {
        val trimmed = input.trim()

        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException(NumberGapValidator.ERROR_INVALID_NUMBER))
        }

        if (trimmed.length > NumberGapValidator.MAX_INPUT_LENGTH) {
            return Result.failure(IllegalArgumentException(NumberGapValidator.ERROR_TOO_LONG))
        }

        val normalized = normalizeInput(trimmed) ?: return Result.failure(
            IllegalArgumentException(NumberGapValidator.ERROR_INVALID_NUMBER)
        )

        return try {
            val bd = BigDecimal(normalized)
            Result.success(bd)
        } catch (_: Exception) {
            Result.failure(IllegalArgumentException(NumberGapValidator.ERROR_INVALID_NUMBER))
        }
    }

    private fun normalizeInput(raw: String): String? {
        var str = raw.trim()
        if (str.isEmpty()) return null

        // Check sign
        var sign = ""
        if (str.startsWith("+") || str.startsWith("-")) {
            sign = str.substring(0, 1)
            str = str.substring(1)
            if (str.isEmpty()) return null
        }

        // Must not start with another sign
        if (str.startsWith("+") || str.startsWith("-")) {
            return null
        }

        // Split by exponent ('e' or 'E')
        val eIndex = str.indexOfAny(charArrayOf('e', 'E'))
        val mantissa: String
        val exponentPart: String

        if (eIndex != -1) {
            mantissa = str.substring(0, eIndex)
            val rest = str.substring(eIndex + 1)
            if (rest.isEmpty()) return null

            // Validate exponent: optional +/-, followed by 1 or more digits
            var expSign = ""
            var expDigits = rest
            if (rest.startsWith("+") || rest.startsWith("-")) {
                expSign = rest.substring(0, 1)
                expDigits = rest.substring(1)
            }
            if (expDigits.isEmpty() || !expDigits.all { it.isDigit() }) {
                return null
            }
            exponentPart = "E$expSign$expDigits"
        } else {
            mantissa = str
            exponentPart = ""
        }

        // Validate and normalize mantissa
        if (mantissa.isEmpty()) return null

        val dotCount = mantissa.count { it == '.' }
        if (dotCount > 1) return null

        val normalizedMantissa = if (dotCount == 1) {
            val parts = mantissa.split('.')
            val before = parts[0]
            val after = parts[1]

            // Check if all non-empty parts are digits
            if (before.isNotEmpty() && !before.all { it.isDigit() }) return null
            if (after.isNotEmpty() && !after.all { it.isDigit() }) return null

            // At least one part must have digits (e.g. "." alone is invalid)
            if (before.isEmpty() && after.isEmpty()) return null

            val normalizedBefore = if (before.isEmpty()) "0" else before
            val normalizedAfter = if (after.isEmpty()) "0" else after

            "$normalizedBefore.$normalizedAfter"
        } else {
            if (!mantissa.all { it.isDigit() }) return null
            mantissa
        }

        val fullNormalized = "$sign$normalizedMantissa$exponentPart"
        return fullNormalized
    }
}
