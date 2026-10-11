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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subject
import com.example.ui.components.CircularProgressMeter
import com.example.ui.components.GlassCard
import com.example.ui.theme.RudraCyanDark
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
import com.example.ui.theme.RudraVioletNeon
import com.example.ui.viewmodel.RudraViewModel
import com.example.ui.viewmodel.TimerMode
import com.example.ui.viewmodel.TimerState
import java.util.Locale

@Composable
fun FocusTimerScreen(
    viewModel: RudraViewModel,
    modifier: Modifier = Modifier
) {
    val timerState by viewModel.timerState.collectAsState()
    val isHindi by viewModel.isHindi.collectAsState()
    val studySessions by viewModel.studySessions.collectAsState()

    var showLogSessionDialog by remember { mutableStateOf(false) }

    // Format remaining or elapsed time
    val displaySeconds = if (timerState.mode == TimerMode.STOPWATCH) {
        timerState.elapsedSeconds
    } else {
        timerState.remainingSeconds
    }

    val minutes = displaySeconds / 60
    val seconds = displaySeconds % 60
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    val progressPercent = if (timerState.totalSeconds > 0) {
        ((timerState.totalSeconds - timerState.remainingSeconds).toFloat() / timerState.totalSeconds.toFloat()) * 100f
    } else 0f

    val subjectColor = Color(timerState.selectedSubject.colorHex)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(RudraNavyDarkest)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mode Selector Tabs
        item {
            GlassCard(
                borderColor = RudraNavyBorder,
                backgroundColor = RudraNavyCardElevated
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val modes = listOf(
                        TimerMode.POMODORO to (if (isHindi) "पॉमोडोरो (50m/10m)" else "Pomodoro (50m)"),
                        TimerMode.COUNTDOWN to (if (isHindi) "उलटी गिनती (Countdown)" else "Countdown (60m)"),
                        TimerMode.STOPWATCH to (if (isHindi) "स्टॉपवॉच (Open)" else "Stopwatch")
                    )

                    modes.forEach { (mode, label) ->
                        val isSelected = timerState.mode == mode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) RudraCyanNeon else Color.Transparent)
                                .clickable { viewModel.setTimerMode(mode) }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                .testTag("timer_mode_${mode.name}")
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.Black else RudraTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Subject Selector
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isHindi) "अध्ययन विषय चुनें:" else "Select Study Subject:",
                    color = RudraTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(Subject.entries.toTypedArray()) { sub ->
                        val isSelected = timerState.selectedSubject == sub
                        val color = Color(sub.colorHex)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) color.copy(alpha = 0.25f) else RudraNavyCardElevated)
                                .border(1.dp, if (isSelected) color else RudraNavyBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.setSelectedSubject(sub) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("select_sub_${sub.key}")
                        ) {
                            Text(
                                text = if (isHindi) sub.nameHi else sub.nameEn,
                                color = if (isSelected) color else RudraTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Big Circular Timer Display
        item {
            GlassCard(
                borderColor = if (timerState.isBreakPhase) RudraEmerald else subjectColor,
                backgroundColor = RudraNavyCard
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (timerState.isBreakPhase) {
                        Text(
                            text = if (isHindi) "☕ ब्रेक का समय! विश्राम करें" else "☕ Break Phase! Rest & Hydrate",
                            color = RudraEmerald,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = if (isHindi) timerState.selectedSubject.nameHi else timerState.selectedSubject.nameEn,
                            color = subjectColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    CircularProgressMeter(
                        progressPercent = progressPercent,
                        size = 190.dp,
                        strokeWidth = 14.dp,
                        primaryColor = if (timerState.isBreakPhase) RudraEmerald else subjectColor,
                        centerTitle = timeFormatted,
                        centerSubtitle = when (timerState.state) {
                            TimerState.RUNNING -> if (isHindi) "सत्र चालू है..." else "Focus in Progress..."
                            TimerState.PAUSED -> if (isHindi) "सत्र रुका हुआ" else "Paused"
                            TimerState.FINISHED -> if (isHindi) "सत्र समाप्त!" else "Session Done!"
                            TimerState.IDLE -> if (isHindi) "तैयार" else "Ready"
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Preset Duration Chips (for Countdown Mode)
                    if (timerState.mode == TimerMode.COUNTDOWN && timerState.state == TimerState.IDLE) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(30, 45, 60, 90).forEach { mins ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (timerState.totalSeconds == mins * 60) RudraCyanNeon else RudraNavyCardElevated)
                                        .clickable { viewModel.setTimerDurationMinutes(mins) }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${mins}m",
                                        color = if (timerState.totalSeconds == mins * 60) Color.Black else RudraTextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Main Controls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reset Button
                        IconButton(
                            onClick = { viewModel.resetTimer() },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(RudraNavyCardElevated)
                                .border(1.dp, RudraNavyBorder, CircleShape)
                                .testTag("timer_reset_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = RudraTextMuted)
                        }

                        // Play / Pause Primary Button
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(if (timerState.state == TimerState.RUNNING) RudraGold else RudraCyanNeon)
                                .clickable {
                                    if (timerState.state == TimerState.RUNNING) {
                                        viewModel.pauseTimer()
                                    } else {
                                        viewModel.startTimer()
                                    }
                                }
                                .testTag("timer_main_action_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (timerState.state == TimerState.RUNNING) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.Black,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        // Finish & Log Button
                        IconButton(
                            onClick = {
                                if (timerState.elapsedSeconds > 30 || timerState.state == TimerState.FINISHED) {
                                    showLogSessionDialog = true
                                } else {
                                    viewModel.resetTimer()
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(RudraNavyCardElevated)
                                .border(1.dp, RudraEmerald, CircleShape)
                                .testTag("timer_finish_btn")
                        ) {
                            Icon(Icons.Default.Done, contentDescription = "Finish Session", tint = RudraEmerald)
                        }
                    }
                }
            }
        }

        // Recent Study History for Today
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isHindi) "आज के अध्ययन सत्र (Today's Logs)" else "Today's Study Session Logs",
                    color = RudraTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (studySessions.isEmpty()) {
                    GlassCard(
                        borderColor = RudraNavyBorder,
                        backgroundColor = RudraNavyCardElevated
                    ) {
                        Text(
                            text = if (isHindi)
                                "अभी तक कोई सत्र दर्ज नहीं किया गया है। ऊपर टाइमर शुरू करें!"
                            else
                                "No focused sessions recorded yet today. Start a timer above!",
                            color = RudraTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    studySessions.take(5).forEach { session ->
                        val sub = Subject.fromKey(session.subjectKey)
                        GlassCard(
                            borderColor = RudraNavyBorder,
                            backgroundColor = RudraNavyCardElevated,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isHindi) sub?.nameHi ?: session.subjectKey else sub?.nameEn ?: session.subjectKey,
                                        color = sub?.colorHex?.let { Color(it) } ?: RudraCyanNeon,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (session.topicCovered.isNotEmpty()) {
                                        Text(
                                            text = "📖 ${session.topicCovered}",
                                            color = RudraTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${session.durationMinutes} " + (if (isHindi) "मिनट" else "min"),
                                        color = RudraGold,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (session.questionsSolved > 0) {
                                        Text(
                                            text = "${session.questionsSolved} Qs",
                                            color = RudraEmerald,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Log Session Dialog
    if (showLogSessionDialog) {
        var topic by remember { mutableStateOf("") }
        var questions by remember { mutableStateOf("10") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showLogSessionDialog = false },
            title = {
                Text(
                    text = if (isHindi) "सत्र सुरक्षित करें (Log Session)" else "Log Focused Study Session",
                    color = RudraTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = (if (isHindi) "विषय: " else "Subject: ") +
                                (if (isHindi) timerState.selectedSubject.nameHi else timerState.selectedSubject.nameEn),
                        color = subjectColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = (if (isHindi) "अध्ययन समय: " else "Focused Time: ") +
                                "${(timerState.elapsedSeconds / 60).coerceAtLeast(1)} " +
                                (if (isHindi) "मिनट" else "minutes"),
                        color = RudraGold,
                        fontSize = 12.sp
                    )

                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text(if (isHindi) "अध्याय / टॉपिक" else "Chapter / Topic Covered") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = RudraTextPrimary,
                            unfocusedTextColor = RudraTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = questions,
                        onValueChange = { questions = it },
                        label = { Text(if (isHindi) "हल किए गए प्रश्न (Questions Solved)" else "Questions Solved") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = RudraTextPrimary,
                            unfocusedTextColor = RudraTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text(if (isHindi) "सत्र नोट्स" else "Session Notes") },
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
                        val qCount = questions.toIntOrNull() ?: 0
                        viewModel.finishAndLogSession(topic, qCount, notes)
                        showLogSessionDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RudraEmerald, contentColor = Color.Black)
                ) {
                    Text(if (isHindi) "दर्ज करें" else "Save Session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogSessionDialog = false }) {
                    Text(if (isHindi) "रद्द करें" else "Cancel", color = RudraTextMuted)
                }
            },
            containerColor = RudraNavyCardElevated
        )
    }
}
