package com.example.numbergap.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.numbergap.ui.about.AboutScreen
import com.example.numbergap.ui.history.HistoryScreen
import com.example.numbergap.ui.home.HomeScreen
import com.example.numbergap.ui.settings.SettingsScreen
import com.example.numbergap.ui.splash.SplashScreen
import com.example.numbergap.ui.viewmodel.NumberGapViewModel

@Composable
fun NumberGapApp(
    viewModel: NumberGapViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val showBottomBar = currentRoute != Screen.Splash.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    Screen.bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                screen.icon?.let { icon ->
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = screen.title
                                    )
                                }
                            },
                            label = { Text(screen.title) },
                            selected = selected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    uiState = uiState,
                    settings = settings,
                    onNumberAChanged = viewModel::onNumberAChanged,
                    onNumberBChanged = viewModel::onNumberBChanged,
                    onCalculate = viewModel::calculateGap,
                    onSwap = viewModel::swapNumbers,
                    onClear = viewModel::clearInputs,
                    onSave = viewModel::saveCurrentCalculation,
                    onToggleSteps = viewModel::toggleStepsExpanded,
                    onClearSaveMessage = viewModel::clearSaveSuccessMessage
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    historyList = uiState.history,
                    settings = settings,
                    onSelectEntry = { entry ->
                        viewModel.restoreHistory(entry)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onDeleteEntry = viewModel::deleteHistoryEntry,
                    onClearAllHistory = viewModel::clearAllHistory
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    settings = settings,
                    onUpdateTheme = viewModel::updateTheme,
                    onUpdateDynamicColor = viewModel::updateDynamicColor,
                    onUpdateHaptics = viewModel::updateHapticFeedback,
                    onUpdateAnimations = viewModel::updateAnimations,
                    onUpdateDecimalPlaces = viewModel::updateDecimalPlaces,
                    onUpdateRoundingMode = viewModel::updateRoundingMode,
                    onUpdateConfirmDelete = viewModel::updateConfirmDelete,
                    onClearHistory = viewModel::clearAllHistory,
                    onResetSettings = viewModel::resetSettings
                )
            }

            composable(Screen.About.route) {
                AboutScreen()
            }
        }
    }
}
