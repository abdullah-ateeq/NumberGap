package com.example.numbergap.domain.math

object NumberGapValidator {
    const val MAX_INPUT_LENGTH = 500
    const val ERROR_NUMBER_A_EMPTY = "Enter Number A."
    const val ERROR_NUMBER_B_EMPTY = "Enter Number B."
    const val ERROR_TOO_LONG = "Number is too long. Maximum 500 characters."
    const val ERROR_INVALID_NUMBER = "Enter a valid number."

    fun validateInput(input: String, fieldName: String): Result<String> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter $fieldName."))
        }
        if (trimmed.length > MAX_INPUT_LENGTH) {
            return Result.failure(IllegalArgumentException(ERROR_TOO_LONG))
        }
        return Result.success(trimmed)
    }
}
