package com.example.numbergap.ui.home

import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.numbergap.data.model.AppSettings
import com.example.numbergap.domain.math.NumberGapFormatter
import com.example.numbergap.ui.viewmodel.NumberGapUiState
import com.example.numbergap.util.ClipboardUtil
import com.example.numbergap.util.HapticUtil
import com.example.numbergap.util.ShareUtil

@Composable
fun HomeScreen(
    uiState: NumberGapUiState,
    settings: AppSettings,
    onNumberAChanged: (String) -> Unit,
    onNumberBChanged: (String) -> Unit,
    onCalculate: () -> Boolean,
    onSwap: () -> Unit,
    onClear: () -> Unit,
    onSave: () -> Boolean,
    onToggleSteps: () -> Unit,
    onClearSaveMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view: View = LocalView.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.saveSuccessMessage) {
        uiState.saveSuccessMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearSaveMessage()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "NumberGap",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Find the difference between two numbers.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // NUMBER A INPUT
                OutlinedTextField(
                    value = uiState.numberAInput,
                    onValueChange = onNumberAChanged,
                    label = { Text("NUMBER A") },
                    placeholder = { Text("e.g. 125, -10, 1.5E-3") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "First number." },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // SWAP BUTTON
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(
                        onClick = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            onSwap()
                        },
                        modifier = Modifier.semantics { contentDescription = "Swap the two numbers." }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap the two numbers.",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // NUMBER B INPUT
                OutlinedTextField(
                    value = uiState.numberBInput,
                    onValueChange = onNumberBChanged,
                    label = { Text("NUMBER B") },
                    placeholder = { Text("e.g. 80, 50.5, 1E+5") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Second number." },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            val success = onCalculate()
                            if (success) {
                                HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            } else {
                                HapticUtil.performErrorHaptic(view, settings.hapticFeedback)
                            }
                        }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PRIMARY CALCULATE BUTTON
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        val success = onCalculate()
                        if (success) {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                        } else {
                            HapticUtil.performErrorHaptic(view, settings.hapticFeedback)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .semantics { contentDescription = "Calculate difference." },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "CALCULATE GAP",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // CLEAR AND SAVE BUTTONS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            onClear()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear inputs",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("CLEAR")
                    }

                    OutlinedButton(
                        onClick = {
                            val saved = onSave()
                            if (saved) {
                                HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            } else {
                                HapticUtil.performErrorHaptic(view, settings.hapticFeedback)
                            }
                        },
                        enabled = uiState.result != null,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Save calculation",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("SAVE")
                    }
                }
            }
        }

        // ERROR MESSAGE
        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // RESULTS SECTION
        AnimatedVisibility(
            visible = uiState.result != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            uiState.result?.let { result ->
                val dp = settings.decimalPlaces
                val rm = settings.roundingMode.roundingMode

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // 1. PRIMARY GAP
                    PrimaryGapCard(
                        result = result,
                        settings = settings,
                        onCopyGap = { value ->
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            ClipboardUtil.copyToClipboard(context, "Gap", value)
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. COMPARISON VISUAL
                    ComparisonVisual(
                        result = result,
                        settings = settings
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. SUPPORTING RESULTS
                    val signedFormatted = NumberGapFormatter.formatSignedDifference(
                        result.signedDifference, dp, rm
                    )
                    SupportingValueCard(
                        label = "Signed Difference",
                        value = signedFormatted,
                        accessibilityDescription = "Signed difference between number A and number B",
                        onCopy = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            ClipboardUtil.copyToClipboard(context, "Signed Difference", signedFormatted)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val largerFormatted = NumberGapFormatter.formatNumber(result.largerNumber, dp, rm)
                    SupportingValueCard(
                        label = "Larger Number",
                        value = largerFormatted,
                        accessibilityDescription = "Larger number",
                        onCopy = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            ClipboardUtil.copyToClipboard(context, "Larger Number", largerFormatted)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val smallerFormatted = NumberGapFormatter.formatNumber(result.smallerNumber, dp, rm)
                    SupportingValueCard(
                        label = "Smaller Number",
                        value = smallerFormatted,
                        accessibilityDescription = "Smaller number",
                        onCopy = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            ClipboardUtil.copyToClipboard(context, "Smaller Number", smallerFormatted)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val sumFormatted = NumberGapFormatter.formatNumber(result.sum, dp, rm)
                    SupportingValueCard(
                        label = "Sum",
                        value = sumFormatted,
                        accessibilityDescription = "Sum",
                        onCopy = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            ClipboardUtil.copyToClipboard(context, "Sum", sumFormatted)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val avgFormatted = NumberGapFormatter.formatNumber(result.average, dp, rm)
                    SupportingValueCard(
                        label = "Average",
                        value = avgFormatted,
                        accessibilityDescription = "Average",
                        onCopy = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            ClipboardUtil.copyToClipboard(context, "Average", avgFormatted)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val pctFormatted = NumberGapFormatter.formatPercentageDifference(
                        result.percentageDifference, dp, rm
                    )
                    SupportingValueCard(
                        label = "Percentage Difference",
                        value = pctFormatted,
                        accessibilityDescription = "Percentage difference",
                        onCopy = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            ClipboardUtil.copyToClipboard(context, "Percentage Difference", pctFormatted)
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // ACTION ROW: COPY ALL & SHARE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                                val summary = NumberGapFormatter.buildSummaryText(result, settings)
                                ClipboardUtil.copyToClipboard(context, "NumberGap Calculation", summary)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy result",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text("COPY")
                        }

                        OutlinedButton(
                            onClick = {
                                HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                                val summary = NumberGapFormatter.buildSummaryText(result, settings)
                                ShareUtil.shareText(context, summary, "Share NumberGap Calculation")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share result",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text("SHARE")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. CALCULATION STEPS
                    CalculationStepsCard(
                        numberA = uiState.numberAInput,
                        numberB = uiState.numberBInput,
                        steps = result.calculationSteps,
                        isExpanded = uiState.isStepsExpanded,
                        onToggleExpanded = onToggleSteps
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 5. FORMULAS
                    FormulasCard()

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        SnackbarHost(hostState = snackbarHostState)
    }
}
