package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MorningChecklistItem
import com.example.ui.components.CircularProgressMeter
import com.example.ui.components.GlassCard
import com.example.ui.theme.RudraCyanNeon
import com.example.ui.theme.RudraEmerald
import com.example.ui.theme.RudraGold
import com.example.ui.theme.RudraNavyBorder
import com.example.ui.theme.RudraNavyCard
import com.example.ui.theme.RudraNavyCardElevated
import com.example.ui.theme.RudraNavyDarkest
import com.example.ui.theme.RudraTextMuted
import com.example.ui.theme.RudraTextPrimary
import com.example.ui.theme.RudraTextSecondary
import com.example.ui.viewmodel.RudraViewModel

@Composable
fun MorningChecklistScreen(
    viewModel: RudraViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.morningChecklist.collectAsState()
    val isHindi by viewModel.isHindi.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }

    val checkedCount = remember(items) { items.count { it.isChecked } }
    val totalCount = remember(items) { items.size }
    val progressPercent = remember(checkedCount, totalCount) {
        if (totalCount > 0) (checkedCount.toFloat() / totalCount.toFloat()) * 100f else 0f
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(RudraNavyDarkest)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Morning Progress Banner
        item {
            GlassCard(
                borderColor = RudraGold,
                backgroundColor = RudraNavyCardElevated
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Sun",
                                tint = RudraGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "प्रातः नित्यक्रम चेकलिस्ट" else "Morning Routine Checklist",
                                color = RudraGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isHindi)
                                "सवेरा अनुशासित तो पूरा दिन विजयी! ($checkedCount/$totalCount कार्य पूर्ण)"
                            else
                                "Disciplined morning sets up a winning day! ($checkedCount of $totalCount done)",
                            color = RudraTextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(RudraNavyBorder)
                                    .clickable { viewModel.resetMorningChecklist() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("reset_morning_checklist_btn")
                            ) {
                                Text(
                                    text = if (isHindi) "रीसेट करें" else "Reset Today",
                                    color = RudraCyanNeon,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(RudraCyanNeon)
                                    .clickable { showAddItemDialog = true }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("add_morning_item_btn")
                            ) {
                                Text(
                                    text = "+ " + if (isHindi) "नया कार्य" else "Add Item",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    CircularProgressMeter(
                        progressPercent = progressPercent,
                        size = 85.dp,
                        strokeWidth = 8.dp,
                        primaryColor = RudraGold,
                        centerTitle = "${progressPercent.toInt()}%",
                        centerSubtitle = if (isHindi) "पूर्ण" else "Done"
                    )
                }
            }
        }

        // Checklist Items
        items(items, key = { it.id }) { item ->
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (item.isChecked) RudraEmerald.copy(alpha = 0.4f) else RudraNavyBorder,
                backgroundColor = if (item.isChecked) RudraNavyCard.copy(alpha = 0.8f) else RudraNavyCardElevated
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleChecklistItem(item.id, !item.isChecked) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Checkbox(
                            checked = item.isChecked,
                            onCheckedChange = { checked ->
                                viewModel.toggleChecklistItem(item.id, checked)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = RudraEmerald,
                                uncheckedColor = RudraNavyBorder,
                                checkmarkColor = Color.Black
                            ),
                            modifier = Modifier.testTag("checkbox_${item.id}")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = if (isHindi) item.textHi else item.textEn,
                            color = if (item.isChecked) RudraTextMuted else RudraTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.Medium,
                            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
                        )
                    }

                    IconButton(
                        onClick = { viewModel.deleteChecklistItem(item) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = RudraTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    // Add Item Dialog
    if (showAddItemDialog) {
        var textHi by remember { mutableStateOf("") }
        var textEn by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            title = {
                Text(
                    text = if (isHindi) "नया प्रातः कार्य जोड़ें" else "Add Morning Habit",
                    color = RudraTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = textHi,
                        onValueChange = { textHi = it },
                        label = { Text(if (isHindi) "विवरण (हिन्दी)" else "Habit (Hindi)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = RudraTextPrimary,
                            unfocusedTextColor = RudraTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = textEn,
                        onValueChange = { textEn = it },
                        label = { Text(if (isHindi) "विवरण (English)" else "Habit (English)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = RudraTextPrimary,
                            unfocusedTextColor = RudraTextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (textHi.isNotBlank() || textEn.isNotBlank()) {
                            viewModel.addChecklistItem(
                                MorningChecklistItem(
                                    textHi = textHi.ifBlank { textEn },
                                    textEn = textEn.ifBlank { textHi },
                                    isChecked = false,
                                    orderIndex = items.size + 1
                                )
                            )
                            showAddItemDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RudraCyanNeon, contentColor = Color.Black)
                ) {
                    Text(if (isHindi) "जोड़ें" else "Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddItemDialog = false }) {
                    Text(if (isHindi) "रद्द करें" else "Cancel", color = RudraTextMuted)
                }
            },
            containerColor = RudraNavyCardElevated
        )
    }
}
