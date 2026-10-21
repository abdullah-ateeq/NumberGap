package com.example.numbergap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.numbergap.data.model.AppTheme
import com.example.numbergap.data.repository.HistoryRepository
import com.example.numbergap.data.repository.SettingsRepository
import com.example.numbergap.ui.navigation.NumberGapApp
import com.example.numbergap.ui.theme.NumberGapTheme
import com.example.numbergap.ui.viewmodel.NumberGapViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsRepo = SettingsRepository.create(applicationContext)
        val historyRepo = HistoryRepository.create(applicationContext)

        setContent {
            val viewModel: NumberGapViewModel = viewModel(
                factory = NumberGapViewModel.provideFactory(settingsRepo, historyRepo)
            )

            val settings by viewModel.settings.collectAsState()

            val isDark = when (settings.theme) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            NumberGapTheme(
                darkTheme = isDark,
                dynamicColor = settings.dynamicColor
            ) {
                NumberGapApp(viewModel = viewModel)
            }
        }
    }
}
