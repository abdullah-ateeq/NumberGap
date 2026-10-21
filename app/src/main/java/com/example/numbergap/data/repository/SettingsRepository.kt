package com.example.numbergap.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.numbergap.data.model.AppRoundingMode
import com.example.numbergap.data.model.AppSettings
import com.example.numbergap.data.model.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(private val sharedPreferences: SharedPreferences) {

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<AppSettings> = _settingsFlow.asStateFlow()

    fun getSettings(): AppSettings = _settingsFlow.value

    fun updateTheme(theme: AppTheme) {
        sharedPreferences.edit().putString(KEY_THEME, theme.name).apply()
        _settingsFlow.value = _settingsFlow.value.copy(theme = theme)
    }

    fun updateDynamicColor(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(dynamicColor = enabled)
    }

    fun updateHapticFeedback(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_HAPTIC, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(hapticFeedback = enabled)
    }

    fun updateAnimations(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_ANIMATIONS, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(animations = enabled)
    }

    fun updateDecimalPlaces(places: Int) {
        val validPlaces = if (places in AppSettings.ALLOWED_DECIMAL_PLACES) places else 4
        sharedPreferences.edit().putInt(KEY_DECIMAL_PLACES, validPlaces).apply()
        _settingsFlow.value = _settingsFlow.value.copy(decimalPlaces = validPlaces)
    }

    fun updateRoundingMode(roundingMode: AppRoundingMode) {
        sharedPreferences.edit().putString(KEY_ROUNDING_MODE, roundingMode.name).apply()
        _settingsFlow.value = _settingsFlow.value.copy(roundingMode = roundingMode)
    }

    fun updateConfirmDelete(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_CONFIRM_DELETE, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(confirmDelete = enabled)
    }

    fun saveLastInputs(numberA: String, numberB: String) {
        sharedPreferences.edit()
            .putString(KEY_LAST_NUMBER_A, numberA)
            .putString(KEY_LAST_NUMBER_B, numberB)
            .apply()
    }

    fun getLastInputs(): Pair<String, String> {
        val a = sharedPreferences.getString(KEY_LAST_NUMBER_A, "") ?: ""
        val b = sharedPreferences.getString(KEY_LAST_NUMBER_B, "") ?: ""
        return Pair(a, b)
    }

    fun resetSettings() {
        sharedPreferences.edit()
            .remove(KEY_THEME)
            .remove(KEY_DYNAMIC_COLOR)
            .remove(KEY_HAPTIC)
            .remove(KEY_ANIMATIONS)
            .remove(KEY_DECIMAL_PLACES)
            .remove(KEY_ROUNDING_MODE)
            .remove(KEY_CONFIRM_DELETE)
            .apply()
        _settingsFlow.value = AppSettings()
    }

    private fun loadSettings(): AppSettings {
        val themeName = sharedPreferences.getString(KEY_THEME, AppTheme.SYSTEM.name) ?: AppTheme.SYSTEM.name
        val theme = runCatching { AppTheme.valueOf(themeName) }.getOrDefault(AppTheme.SYSTEM)
        val dynamicColor = sharedPreferences.getBoolean(KEY_DYNAMIC_COLOR, true)
        val haptic = sharedPreferences.getBoolean(KEY_HAPTIC, true)
        val animations = sharedPreferences.getBoolean(KEY_ANIMATIONS, true)
        val decimalPlaces = sharedPreferences.getInt(KEY_DECIMAL_PLACES, 4)
        val roundingModeName = sharedPreferences.getString(KEY_ROUNDING_MODE, AppRoundingMode.HALF_UP.name)
            ?: AppRoundingMode.HALF_UP.name
        val roundingMode = AppRoundingMode.fromName(roundingModeName)
        val confirmDelete = sharedPreferences.getBoolean(KEY_CONFIRM_DELETE, true)

        return AppSettings(
            theme = theme,
            dynamicColor = dynamicColor,
            hapticFeedback = haptic,
            animations = animations,
            decimalPlaces = decimalPlaces,
            roundingMode = roundingMode,
            confirmDelete = confirmDelete
        )
    }

    companion object {
        private const val PREFS_NAME = "numbergap_settings"
        private const val KEY_THEME = "pref_theme"
        private const val KEY_DYNAMIC_COLOR = "pref_dynamic_color"
        private const val KEY_HAPTIC = "pref_haptic"
        private const val KEY_ANIMATIONS = "pref_animations"
        private const val KEY_DECIMAL_PLACES = "pref_decimal_places"
        private const val KEY_ROUNDING_MODE = "pref_rounding_mode"
        private const val KEY_CONFIRM_DELETE = "pref_confirm_delete"
        private const val KEY_LAST_NUMBER_A = "pref_last_number_a"
        private const val KEY_LAST_NUMBER_B = "pref_last_number_b"

        fun create(context: Context): SettingsRepository {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return SettingsRepository(prefs)
        }
    }
}
