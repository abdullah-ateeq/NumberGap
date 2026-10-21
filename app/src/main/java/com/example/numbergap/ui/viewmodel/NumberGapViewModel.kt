package com.example.numbergap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.numbergap.data.model.AppRoundingMode
import com.example.numbergap.data.model.AppSettings
import com.example.numbergap.data.model.AppTheme
import com.example.numbergap.data.model.NumberGapHistory
import com.example.numbergap.data.repository.HistoryRepository
import com.example.numbergap.data.repository.SettingsRepository
import com.example.numbergap.domain.math.NumberGapCalculator
import com.example.numbergap.domain.math.NumberGapFormatter
import com.example.numbergap.domain.math.NumberGapParser
import com.example.numbergap.domain.math.NumberGapValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NumberGapViewModel(
    private val settingsRepository: SettingsRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NumberGapUiState())
    val uiState: StateFlow<NumberGapUiState> = _uiState.asStateFlow()

    val settings: StateFlow<AppSettings> = settingsRepository.settingsFlow

    init {
        // Observe history changes
        viewModelScope.launch {
            historyRepository.historyFlow.collect { historyList ->
                _uiState.update { it.copy(history = historyList) }
            }
        }

        // Load last inputs
        val (lastA, lastB) = settingsRepository.getLastInputs()
        if (lastA.isNotEmpty() || lastB.isNotEmpty()) {
            _uiState.update { it.copy(numberAInput = lastA, numberBInput = lastB) }
            if (lastA.isNotEmpty() && lastB.isNotEmpty()) {
                calculateGap(saveToLastInputs = false)
            }
        }
    }

    fun onNumberAChanged(input: String) {
        _uiState.update {
            it.copy(
                numberAInput = input,
                errorMessage = null
            )
        }
    }

    fun onNumberBChanged(input: String) {
        _uiState.update {
            it.copy(
                numberBInput = input,
                errorMessage = null
            )
        }
    }

    fun calculateGap(saveToLastInputs: Boolean = true): Boolean {
        val currentState = _uiState.value
        val rawA = currentState.numberAInput.trim()
        val rawB = currentState.numberBInput.trim()

        if (rawA.isEmpty()) {
            _uiState.update { it.copy(errorMessage = NumberGapValidator.ERROR_NUMBER_A_EMPTY) }
            return false
        }

        if (rawB.isEmpty()) {
            _uiState.update { it.copy(errorMessage = NumberGapValidator.ERROR_NUMBER_B_EMPTY) }
            return false
        }

        val parseAResult = NumberGapParser.parseNumber(rawA)
        if (parseAResult.isFailure) {
            _uiState.update {
                it.copy(
                    errorMessage = parseAResult.exceptionOrNull()?.message
                        ?: NumberGapValidator.ERROR_INVALID_NUMBER
                )
            }
            return false
        }

        val parseBResult = NumberGapParser.parseNumber(rawB)
        if (parseBResult.isFailure) {
            _uiState.update {
                it.copy(
                    errorMessage = parseBResult.exceptionOrNull()?.message
                        ?: NumberGapValidator.ERROR_INVALID_NUMBER
                )
            }
            return false
        }

        val a = parseAResult.getOrThrow()
        val b = parseBResult.getOrThrow()

        val result = NumberGapCalculator.calculate(a, b)

        _uiState.update {
            it.copy(
                result = result,
                errorMessage = null
            )
        }

        if (saveToLastInputs) {
            settingsRepository.saveLastInputs(rawA, rawB)
        }

        return true
    }

    fun swapNumbers() {
        val currentState = _uiState.value
        val newA = currentState.numberBInput
        val newB = currentState.numberAInput

        _uiState.update {
            it.copy(
                numberAInput = newA,
                numberBInput = newB,
                errorMessage = null
            )
        }

        if (currentState.result != null || (newA.isNotEmpty() && newB.isNotEmpty())) {
            calculateGap()
        }
    }

    fun clearInputs() {
        _uiState.update {
            it.copy(
                numberAInput = "",
                numberBInput = "",
                result = null,
                errorMessage = null
            )
        }
        settingsRepository.saveLastInputs("", "")
    }

    fun toggleStepsExpanded() {
        _uiState.update { it.copy(isStepsExpanded = !it.isStepsExpanded) }
    }

    fun saveCurrentCalculation(): Boolean {
        val state = _uiState.value
        val a = state.numberAInput.trim()
        val b = state.numberBInput.trim()

        if (a.isEmpty() || b.isEmpty() || state.result == null) {
            return false
        }

        val saved = historyRepository.saveCalculation(a, b)
        if (saved) {
            _uiState.update {
                it.copy(
                    lastSavedPair = Pair(a, b),
                    saveSuccessMessage = "Calculation saved"
                )
            }
        }
        return saved
    }

    fun clearSaveSuccessMessage() {
        _uiState.update { it.copy(saveSuccessMessage = null) }
    }

    fun restoreHistory(entry: NumberGapHistory) {
        _uiState.update {
            it.copy(
                numberAInput = entry.numberA,
                numberBInput = entry.numberB,
                errorMessage = null
            )
        }
        calculateGap()
    }

    fun deleteHistoryEntry(id: String) {
        historyRepository.deleteEntry(id)
    }

    fun clearAllHistory() {
        historyRepository.clearHistory()
    }

    fun updateTheme(theme: AppTheme) {
        settingsRepository.updateTheme(theme)
    }

    fun updateDynamicColor(enabled: Boolean) {
        settingsRepository.updateDynamicColor(enabled)
    }

    fun updateHapticFeedback(enabled: Boolean) {
        settingsRepository.updateHapticFeedback(enabled)
    }

    fun updateAnimations(enabled: Boolean) {
        settingsRepository.updateAnimations(enabled)
    }

    fun updateDecimalPlaces(places: Int) {
        settingsRepository.updateDecimalPlaces(places)
    }

    fun updateRoundingMode(mode: AppRoundingMode) {
        settingsRepository.updateRoundingMode(mode)
    }

    fun updateConfirmDelete(enabled: Boolean) {
        settingsRepository.updateConfirmDelete(enabled)
    }

    fun resetSettings() {
        settingsRepository.resetSettings()
    }

    fun getFormattedSummary(): String? {
        val result = _uiState.value.result ?: return null
        return NumberGapFormatter.buildSummaryText(result, settings.value)
    }

    companion object {
        fun provideFactory(
            settingsRepository: SettingsRepository,
            historyRepository: HistoryRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NumberGapViewModel(settingsRepository, historyRepository) as T
            }
        }
    }
}
