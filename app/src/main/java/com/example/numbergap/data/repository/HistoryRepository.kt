package com.example.numbergap.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.numbergap.data.model.NumberGapHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class HistoryRepository(private val sharedPreferences: SharedPreferences) {

    private val _historyFlow = MutableStateFlow<List<NumberGapHistory>>(loadHistory())
    val historyFlow: StateFlow<List<NumberGapHistory>> = _historyFlow.asStateFlow()

    fun getHistory(): List<NumberGapHistory> = _historyFlow.value

    /**
     * Saves a calculation if it is not an exact duplicate of the most recent entry.
     * Returns true if saved, false if ignored due to duplicate.
     * Keeps at most [MAX_HISTORY_ENTRIES] items.
     */
    fun saveCalculation(numberA: String, numberB: String, timestamp: Long = System.currentTimeMillis()): Boolean {
        val currentList = _historyFlow.value
        val mostRecent = currentList.firstOrNull()

        // Check if exact same A/B pair was saved consecutively
        if (mostRecent != null && mostRecent.numberA == numberA && mostRecent.numberB == numberB) {
            return false
        }

        val newItem = NumberGapHistory(
            id = UUID.randomUUID().toString(),
            numberA = numberA,
            numberB = numberB,
            savedAt = timestamp
        )

        val updatedList = (listOf(newItem) + currentList).take(MAX_HISTORY_ENTRIES)
        persistHistory(updatedList)
        _historyFlow.value = updatedList
        return true
    }

    fun deleteEntry(id: String) {
        val updatedList = _historyFlow.value.filterNot { it.id == id }
        persistHistory(updatedList)
        _historyFlow.value = updatedList
    }

    fun clearHistory() {
        persistHistory(emptyList())
        _historyFlow.value = emptyList()
    }

    private fun loadHistory(): List<NumberGapHistory> {
        val jsonString = sharedPreferences.getString(KEY_HISTORY_DATA, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<NumberGapHistory>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    NumberGapHistory(
                        id = obj.getString("id"),
                        numberA = obj.getString("numberA"),
                        numberB = obj.getString("numberB"),
                        savedAt = obj.getLong("savedAt")
                    )
                )
            }
            list.take(MAX_HISTORY_ENTRIES)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun persistHistory(history: List<NumberGapHistory>) {
        try {
            val jsonArray = JSONArray()
            for (item in history) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("numberA", item.numberA)
                    put("numberB", item.numberB)
                    put("savedAt", item.savedAt)
                }
                jsonArray.put(obj)
            }
            sharedPreferences.edit().putString(KEY_HISTORY_DATA, jsonArray.toString()).apply()
        } catch (_: Exception) {
            // Fail safe
        }
    }

    companion object {
        const val MAX_HISTORY_ENTRIES = 10
        private const val PREFS_NAME = "numbergap_history"
        private const val KEY_HISTORY_DATA = "history_json_data"

        fun create(context: Context): HistoryRepository {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return HistoryRepository(prefs)
        }
    }
}
