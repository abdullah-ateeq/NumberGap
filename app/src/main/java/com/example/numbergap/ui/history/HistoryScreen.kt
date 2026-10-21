package com.example.numbergap.ui.history

import android.text.format.DateFormat
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.numbergap.data.model.AppSettings
import com.example.numbergap.data.model.NumberGapHistory
import com.example.numbergap.domain.math.NumberGapCalculator
import com.example.numbergap.domain.math.NumberGapFormatter
import com.example.numbergap.domain.math.NumberGapParser
import com.example.numbergap.util.HapticUtil
import java.util.Date

@Composable
fun HistoryScreen(
    historyList: List<NumberGapHistory>,
    settings: AppSettings,
    onSelectEntry: (NumberGapHistory) -> Unit,
    onDeleteEntry: (String) -> Unit,
    onClearAllHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view: View = LocalView.current
    var itemToDelete by remember { mutableStateOf<NumberGapHistory?>(null) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    // Confirmation dialog for single entry deletion
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete calculation") },
            text = { Text("Delete this calculation?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemToDelete?.let { entry ->
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            onDeleteEntry(entry.id)
                        }
                        itemToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirmation dialog for clearing all history
    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text("Clear History") },
            text = { Text("Clear all saved calculations?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                        onClearAllHistory()
                        showClearAllDialog = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Last ${historyList.size} of 10 calculations",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            if (historyList.isNotEmpty()) {
                IconButton(
                    onClick = { showClearAllDialog = true },
                    modifier = Modifier.semantics { contentDescription = "Clear all history" }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear all history",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "No history",
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No saved calculations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Calculate a gap and tap Save to store it here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(historyList, key = { it.id }) { item ->
                    HistoryItemCard(
                        entry = item,
                        settings = settings,
                        onClick = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            onSelectEntry(item)
                        },
                        onDelete = {
                            if (settings.confirmDelete) {
                                itemToDelete = item
                            } else {
                                HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                                onDeleteEntry(item.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryItemCard(
    entry: NumberGapHistory,
    settings: AppSettings,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val formattedDate = remember(entry.savedAt) {
        val format = DateFormat.getMediumDateFormat(context)
        val timeFormat = DateFormat.getTimeFormat(context)
        val d = Date(entry.savedAt)
        "${format.format(d)} • ${timeFormat.format(d)}"
    }

    // Recalculate gap dynamically for display
    val gapDisplay = remember(entry.numberA, entry.numberB, settings.decimalPlaces, settings.roundingMode) {
        val parseA = NumberGapParser.parseNumber(entry.numberA)
        val parseB = NumberGapParser.parseNumber(entry.numberB)
        if (parseA.isSuccess && parseB.isSuccess) {
            val gap = NumberGapCalculator.calculateAbsoluteGap(parseA.getOrThrow(), parseB.getOrThrow())
            NumberGapFormatter.formatNumber(gap, settings.decimalPlaces, settings.roundingMode.roundingMode)
        } else {
            "?"
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics { contentDescription = "Calculation: ${entry.numberA} and ${entry.numberB}, Gap: $gapDisplay" },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${entry.numberA} − ${entry.numberB}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gap: $gapDisplay",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.semantics { contentDescription = "Delete calculation" }
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete calculation",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}


