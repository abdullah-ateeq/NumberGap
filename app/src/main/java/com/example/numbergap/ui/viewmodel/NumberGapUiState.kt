package com.example.numbergap.ui.viewmodel

import com.example.numbergap.data.model.NumberGapHistory
import com.example.numbergap.data.model.NumberGapResult

data class NumberGapUiState(
    val numberAInput: String = "",
    val numberBInput: String = "",
    val result: NumberGapResult? = null,
    val history: List<NumberGapHistory> = emptyList(),
    val errorMessage: String? = null,
    val isCalculating: Boolean = false,
    val isStepsExpanded: Boolean = false,
    val lastSavedPair: Pair<String, String>? = null,
    val saveSuccessMessage: String? = null
)
