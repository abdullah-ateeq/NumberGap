package com.example.numbergap.ui.settings

import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.numbergap.data.model.AppRoundingMode
import com.example.numbergap.data.model.AppSettings
import com.example.numbergap.data.model.AppTheme
import com.example.numbergap.util.HapticUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onUpdateTheme: (AppTheme) -> Unit,
    onUpdateDynamicColor: (Boolean) -> Unit,
    onUpdateHaptics: (Boolean) -> Unit,
    onUpdateAnimations: (Boolean) -> Unit,
    onUpdateDecimalPlaces: (Int) -> Unit,
    onUpdateRoundingMode: (AppRoundingMode) -> Unit,
    onUpdateConfirmDelete: (Boolean) -> Unit,
    onClearHistory: () -> Unit,
    onResetSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view: View = LocalView.current
    val scrollState = rememberScrollState()

    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }

    var decimalDropdownExpanded by remember { mutableStateOf(false) }
    var roundingDropdownExpanded by remember { mutableStateOf(false) }
    var themeDropdownExpanded by remember { mutableStateOf(false) }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear History") },
            text = { Text("Clear all saved calculations?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                        onClearHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text("Reset Settings") },
            text = { Text("Reset all settings to default values?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                        onResetSettings()
                        showResetSettingsDialog = false
                    }
                ) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Preferences and display configuration",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: APPEARANCE
        SettingsSectionHeader(title = "APPEARANCE")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Theme selection
                ExposedDropdownMenuBox(
                    expanded = themeDropdownExpanded,
                    onExpandedChange = { themeDropdownExpanded = !themeDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = when (settings.theme) {
                            AppTheme.SYSTEM -> "System"
                            AppTheme.LIGHT -> "Light"
                            AppTheme.DARK -> "Dark"
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Theme") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = themeDropdownExpanded,
                        onDismissRequest = { themeDropdownExpanded = false }
                    ) {
                        AppTheme.entries.forEach { theme ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (theme) {
                                            AppTheme.SYSTEM -> "System"
                                            AppTheme.LIGHT -> "Light"
                                            AppTheme.DARK -> "Dark"
                                        }
                                    )
                                },
                                onClick = {
                                    onUpdateTheme(theme)
                                    themeDropdownExpanded = false
                                },
                                trailingIcon = if (settings.theme == theme) {
                                    {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else null
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dynamic Color
                SettingsSwitchRow(
                    icon = Icons.Default.ColorLens,
                    title = "Dynamic Color",
                    subtitle = "Use Material You dynamic system colors",
                    checked = settings.dynamicColor,
                    onCheckedChange = {
                        HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                        onUpdateDynamicColor(it)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: BEHAVIOR
        SettingsSectionHeader(title = "BEHAVIOR")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsSwitchRow(
                    icon = Icons.Default.Vibration,
                    title = "Haptic Feedback",
                    subtitle = "Vibrate on button clicks and calculations",
                    checked = settings.hapticFeedback,
                    onCheckedChange = {
                        HapticUtil.performLightHaptic(view, true)
                        onUpdateHaptics(it)
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                )

                SettingsSwitchRow(
                    icon = Icons.Default.Animation,
                    title = "Animations",
                    subtitle = "Subtle result and transition animations",
                    checked = settings.animations,
                    onCheckedChange = {
                        HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                        onUpdateAnimations(it)
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                )

                SettingsSwitchRow(
                    icon = Icons.Default.Delete,
                    title = "Confirm Delete",
                    subtitle = "Prompt before deleting calculations",
                    checked = settings.confirmDelete,
                    onCheckedChange = {
                        HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                        onUpdateConfirmDelete(it)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: NUMBER DISPLAY
        SettingsSectionHeader(title = "NUMBER DISPLAY")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Decimal Places
                ExposedDropdownMenuBox(
                    expanded = decimalDropdownExpanded,
                    onExpandedChange = { decimalDropdownExpanded = !decimalDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = "${settings.decimalPlaces} decimal places",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Decimal Places") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = decimalDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = decimalDropdownExpanded,
                        onDismissRequest = { decimalDropdownExpanded = false }
                    ) {
                        AppSettings.ALLOWED_DECIMAL_PLACES.forEach { places ->
                            DropdownMenuItem(
                                text = { Text("$places decimal places") },
                                onClick = {
                                    onUpdateDecimalPlaces(places)
                                    decimalDropdownExpanded = false
                                },
                                trailingIcon = if (settings.decimalPlaces == places) {
                                    {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else null
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rounding Mode
                ExposedDropdownMenuBox(
                    expanded = roundingDropdownExpanded,
                    onExpandedChange = { roundingDropdownExpanded = !roundingDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = settings.roundingMode.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rounding Mode") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roundingDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = roundingDropdownExpanded,
                        onDismissRequest = { roundingDropdownExpanded = false }
                    ) {
                        AppRoundingMode.entries.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.displayName) },
                                onClick = {
                                    onUpdateRoundingMode(mode)
                                    roundingDropdownExpanded = false
                                },
                                trailingIcon = if (settings.roundingMode == mode) {
                                    {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else null
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Display settings do not affect intermediate calculations, which always preserve full BigDecimal precision.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: DATA
        SettingsSectionHeader(title = "DATA & RESET")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsActionRow(
                    icon = Icons.Default.Delete,
                    title = "Clear History",
                    subtitle = "Remove all saved calculations",
                    onClick = { showClearHistoryDialog = true },
                    isDestructive = true
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                )

                SettingsActionRow(
                    icon = Icons.Default.RestartAlt,
                    title = "Reset Settings",
                    subtitle = "Restore all settings to default values",
                    onClick = { showResetSettingsDialog = true },
                    isDestructive = true
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.size(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.semantics { contentDescription = "$title toggle" }
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.size(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
