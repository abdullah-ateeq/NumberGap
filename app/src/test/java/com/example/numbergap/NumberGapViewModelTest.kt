package com.example.numbergap

import com.example.numbergap.data.model.AppTheme
import com.example.numbergap.data.model.NumberGapHistory
import com.example.numbergap.data.repository.HistoryRepository
import com.example.numbergap.data.repository.SettingsRepository
import com.example.numbergap.domain.math.NumberGapValidator
import com.example.numbergap.ui.viewmodel.NumberGapViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class NumberGapViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakePrefsSettings: FakeSharedPreferences
    private lateinit var fakePrefsHistory: FakeSharedPreferences
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var historyRepository: HistoryRepository
    private lateinit var viewModel: NumberGapViewModel

    @Before
    fun setUp() {
        fakePrefsSettings = FakeSharedPreferences()
        fakePrefsHistory = FakeSharedPreferences()
        settingsRepository = SettingsRepository(fakePrefsSettings)
        historyRepository = HistoryRepository(fakePrefsHistory)
        viewModel = NumberGapViewModel(settingsRepository, historyRepository)
    }

    @Test
    fun testInputChanges() {
        viewModel.onNumberAChanged("125")
        assertEquals("125", viewModel.uiState.value.numberAInput)

        viewModel.onNumberBChanged("80")
        assertEquals("80", viewModel.uiState.value.numberBInput)
    }

    @Test
    fun testCalculationSuccess() {
        viewModel.onNumberAChanged("125")
        viewModel.onNumberBChanged("80")

        val success = viewModel.calculateGap()
        assertTrue(success)

        val state = viewModel.uiState.value
        assertNotNull(state.result)
        assertEquals(0, BigDecimal("45").compareTo(state.result!!.absoluteGap))
        assertEquals(0, BigDecimal("45").compareTo(state.result!!.signedDifference))
        assertNull(state.errorMessage)
    }

    @Test
    fun testCalculationEmptyNumberA() {
        viewModel.onNumberAChanged("")
        viewModel.onNumberBChanged("80")

        val success = viewModel.calculateGap()
        assertFalse(success)
        assertEquals(NumberGapValidator.ERROR_NUMBER_A_EMPTY, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun testCalculationEmptyNumberB() {
        viewModel.onNumberAChanged("125")
        viewModel.onNumberBChanged("")

        val success = viewModel.calculateGap()
        assertFalse(success)
        assertEquals(NumberGapValidator.ERROR_NUMBER_B_EMPTY, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun testSwapNumbers() {
        viewModel.onNumberAChanged("125")
        viewModel.onNumberBChanged("80")
        viewModel.calculateGap()

        viewModel.swapNumbers()

        val state = viewModel.uiState.value
        assertEquals("80", state.numberAInput)
        assertEquals("125", state.numberBInput)
        assertNotNull(state.result)
        assertEquals(0, BigDecimal("-45").compareTo(state.result!!.signedDifference))
        assertEquals(0, BigDecimal("45").compareTo(state.result!!.absoluteGap))
    }

    @Test
    fun testClearInputsKeepsHistory() {
        viewModel.onNumberAChanged("125")
        viewModel.onNumberBChanged("80")
        viewModel.calculateGap()
        viewModel.saveCurrentCalculation()

        viewModel.clearInputs()

        val state = viewModel.uiState.value
        assertEquals("", state.numberAInput)
        assertEquals("", state.numberBInput)
        assertNull(state.result)
        assertNull(state.errorMessage)
        assertEquals(1, state.history.size)
    }

    @Test
    fun testHistorySaveAndRestore() {
        viewModel.onNumberAChanged("50.5")
        viewModel.onNumberBChanged("25.25")
        viewModel.calculateGap()
        viewModel.saveCurrentCalculation()

        assertEquals(1, viewModel.uiState.value.history.size)

        // Clear and restore
        viewModel.clearInputs()
        val historyItem = viewModel.uiState.value.history[0]
        viewModel.restoreHistory(historyItem)

        val state = viewModel.uiState.value
        assertEquals("50.5", state.numberAInput)
        assertEquals("25.25", state.numberBInput)
        assertNotNull(state.result)
        assertEquals(0, BigDecimal("25.25").compareTo(state.result!!.absoluteGap))
    }

    @Test
    fun testSettingsChanges() {
        viewModel.updateTheme(AppTheme.DARK)
        assertEquals(AppTheme.DARK, viewModel.settings.value.theme)

        viewModel.updateDecimalPlaces(6)
        assertEquals(6, viewModel.settings.value.decimalPlaces)

        viewModel.updateDynamicColor(false)
        assertFalse(viewModel.settings.value.dynamicColor)
    }
}
