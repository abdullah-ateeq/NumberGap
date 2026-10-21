package com.example.numbergap

import com.example.numbergap.data.model.AppRoundingMode
import com.example.numbergap.data.model.AppSettings
import com.example.numbergap.data.model.AppTheme
import com.example.numbergap.data.repository.HistoryRepository
import com.example.numbergap.data.repository.SettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NumberGapRepositoryTest {

    private lateinit var fakePrefsSettings: FakeSharedPreferences
    private lateinit var fakePrefsHistory: FakeSharedPreferences
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var historyRepository: HistoryRepository

    @Before
    fun setUp() {
        fakePrefsSettings = FakeSharedPreferences()
        fakePrefsHistory = FakeSharedPreferences()
        settingsRepository = SettingsRepository(fakePrefsSettings)
        historyRepository = HistoryRepository(fakePrefsHistory)
    }

    @Test
    fun testHistorySave() {
        val saved = historyRepository.saveCalculation("125", "80")
        assertTrue(saved)

        val history = historyRepository.getHistory()
        assertEquals(1, history.size)
        assertEquals("125", history[0].numberA)
        assertEquals("80", history[0].numberB)
    }

    @Test
    fun testDuplicateHistoryConsecutiveIgnored() {
        val saved1 = historyRepository.saveCalculation("125", "80")
        assertTrue(saved1)

        val saved2 = historyRepository.saveCalculation("125", "80")
        assertFalse(saved2)

        val history = historyRepository.getHistory()
        assertEquals(1, history.size)
    }

    @Test
    fun testHistoryLimitMaximum10() {
        // Save 12 distinct entries
        for (i in 1..12) {
            historyRepository.saveCalculation("$i", "0")
        }

        val history = historyRepository.getHistory()
        assertEquals(HistoryRepository.MAX_HISTORY_ENTRIES, history.size)
        assertEquals(10, history.size)

        // The most recently added should be 12, and oldest (1 and 2) removed
        assertEquals("12", history[0].numberA)
        assertEquals("3", history[9].numberA)
    }

    @Test
    fun testHistoryDeletion() {
        historyRepository.saveCalculation("10", "20")
        historyRepository.saveCalculation("30", "40")

        val list = historyRepository.getHistory()
        assertEquals(2, list.size)

        val itemToDelete = list[0]
        historyRepository.deleteEntry(itemToDelete.id)

        val updated = historyRepository.getHistory()
        assertEquals(1, updated.size)
        assertEquals("10", updated[0].numberA)

        historyRepository.clearHistory()
        assertTrue(historyRepository.getHistory().isEmpty())
    }

    @Test
    fun testSettingsPersistence() {
        settingsRepository.updateTheme(AppTheme.DARK)
        settingsRepository.updateDecimalPlaces(2)
        settingsRepository.updateRoundingMode(AppRoundingMode.FLOOR)
        settingsRepository.updateDynamicColor(false)
        settingsRepository.updateHapticFeedback(false)
        settingsRepository.updateAnimations(false)

        val settings = settingsRepository.getSettings()
        assertEquals(AppTheme.DARK, settings.theme)
        assertEquals(2, settings.decimalPlaces)
        assertEquals(AppRoundingMode.FLOOR, settings.roundingMode)
        assertFalse(settings.dynamicColor)
        assertFalse(settings.hapticFeedback)
        assertFalse(settings.animations)

        // Simulate re-creation from same preferences
        val reloadedRepo = SettingsRepository(fakePrefsSettings)
        val reloadedSettings = reloadedRepo.getSettings()
        assertEquals(AppTheme.DARK, reloadedSettings.theme)
        assertEquals(2, reloadedSettings.decimalPlaces)
        assertEquals(AppRoundingMode.FLOOR, reloadedSettings.roundingMode)
    }

    @Test
    fun testSettingsReset() {
        settingsRepository.updateTheme(AppTheme.DARK)
        settingsRepository.updateDecimalPlaces(8)

        settingsRepository.resetSettings()

        val resetSettings = settingsRepository.getSettings()
        assertEquals(AppTheme.SYSTEM, resetSettings.theme)
        assertEquals(4, resetSettings.decimalPlaces)
        assertEquals(AppRoundingMode.HALF_UP, resetSettings.roundingMode)
        assertTrue(resetSettings.dynamicColor)
        assertTrue(resetSettings.hapticFeedback)
    }

    @Test
    fun testLastInputsPersistence() {
        settingsRepository.saveLastInputs("125", "80")
        val (a, b) = settingsRepository.getLastInputs()
        assertEquals("125", a)
        assertEquals("80", b)
    }
}
