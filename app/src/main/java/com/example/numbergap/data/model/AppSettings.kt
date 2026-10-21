package com.example.numbergap.data.model

import java.math.RoundingMode

enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AppRoundingMode(val displayName: String, val roundingMode: RoundingMode) {
    HALF_UP("Half Up", RoundingMode.HALF_UP),
    HALF_DOWN("Half Down", RoundingMode.HALF_DOWN),
    HALF_EVEN("Half Even", RoundingMode.HALF_EVEN),
    UP("Up", RoundingMode.UP),
    DOWN("Down", RoundingMode.DOWN),
    CEILING("Ceiling", RoundingMode.CEILING),
    FLOOR("Floor", RoundingMode.FLOOR);

    companion object {
        fun fromName(name: String, default: AppRoundingMode = HALF_UP): AppRoundingMode {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: default
        }
    }
}

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animations: Boolean = true,
    val decimalPlaces: Int = 4,
    val roundingMode: AppRoundingMode = AppRoundingMode.HALF_UP,
    val confirmDelete: Boolean = true
) {
    companion object {
        val ALLOWED_DECIMAL_PLACES = listOf(0, 1, 2, 3, 4, 6, 8, 10, 12)
    }
}
