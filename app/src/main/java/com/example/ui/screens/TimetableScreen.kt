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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RoutineTask
import com.example.data.model.Subject
import com.example.data.model.TaskCategory
import com.example.ui.components.GlassCard
import com.example.ui.components.SubjectTargetItem
import com.example.ui.theme.RudraCoral
import com.example.ui.theme.RudraCyanDark
import com.example.ui.theme.RudraCyanNeon
import com.example.ui.theme.RudraEmerald
import com.example.ui.theme.RudraGold
import com.example.ui.theme.RudraNavyBorder
import com.example.ui.theme.RudraNavyCard
import com.example.ui.theme.RudraNavyCardElevated
import com.example.ui.theme.RudraNavyDarkest
import com.example.ui.theme.RudraOrange
import com.example.ui.theme.RudraTextMuted
import com.example.ui.theme.RudraTextPrimary
import com.example.ui.theme.RudraTextSecondary
import com.example.ui.theme.RudraVioletNeon
import com.example.ui.viewmodel.RudraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    viewModel: RudraViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.allTasks.collectAsState()
    val isHindi by viewModel.isHindi.collectAsState()
    val is24h by viewModel.is24HourFormat.collectAsState()
    val studySessions by viewModel.studySessions.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, STUDY, HEALTH, MEALS, BREAKS
    var showAddEditDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<RoutineTask?>(null) }
    var showRestoreConfirmDialog by remember { mutableStateOf(false) }
    var showSubjectTargetsPane by remember { mutableStateOf(false) }

    // Check for overlapping tasks
    val sortedTasks = remember(tasks) { tasks.sortedBy { viewModel.timeToMinutes(it.startTime) } }
    val overlappingPairs = remember(sortedTasks) {
        val list = mutableListOf<String>()
        for (i in 0 until sortedTasks.size - 1) {
            val t1 = sortedTasks[i]
            val t2 = sortedTasks[i + 1]
            val end1 = viewModel.timeToMinutes(t1.endTime)
            val start2 = viewModel.timeToMinutes(t2.startTime)
            if (end1 > start2 && end1 < 1440 && start2 < 1440) {
                list.add("${t1.titleEn} (${t1.endTime}) ⟷ ${t2.titleEn} (${t2.startTime})")
            }
        }
        list
    }

    val filteredTasks = remember(sortedTasks, selectedFilter) {
        when (selectedFilter) {
            "STUDY" -> sortedTasks.filter { it.category == "STUDY" }
            "HEALTH" -> sortedTasks.filter { it.category in listOf("HEALTH", "HYGIENE") }
            "MEALS" -> sortedTasks.filter { it.category in listOf("MEALS", "BREAKS") }
            "SLEEP" -> sortedTasks.filter { it.category in listOf("SLEEP", "ROUTINE") }
            else -> sortedTasks
        }
    }

    // Calculate actual subject completed minutes
    val subjectCompletedMap = remember(studySessions) {
        val map = mutableMapOf<String, Int>()
        Subject.entries.forEach { s -> map[s.key] = 0 }
        studySessions.forEach { session ->
            val curr = map[session.subjectKey] ?: 0
            map[session.subjectKey] = curr + session.durationMinutes
        }
        map
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = RudraNavyDarkest,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    taskToEdit = null
                    showAddEditDialog = true
                },
                containerColor = RudraCyanNeon,
                contentColor = Color.Black,
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .testTag("fab_add_task")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Action Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isHindi) "दैनिक समय सारिणी (Timetable)" else "Daily Timetable",
                            color = RudraTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isHindi) "कुल 23 गतिविधियाँ • 13 घंटे अध्ययन" else "23 Activities • 13.0 Study Hours",
                            color = RudraTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Row {
                        OutlinedButton(
                            onClick = { showSubjectTargetsPane = !showSubjectTargetsPane },
                            border = androidx.compose.foundation.BorderStroke(1.dp, RudraGold),
                            modifier = Modifier.testTag("btn_targets_toggle")
                        ) {
                            Text(
                                text = if (isHindi) "लक्ष्य विवरण" else "Targets",
                                color = RudraGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { showRestoreConfirmDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(RudraNavyCardElevated)
                                .testTag("btn_restore_schedule")
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Restore Defaults",
                                tint = RudraCyanNeon,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Overlap warning banner if any overlap detected
            if (overlappingPairs.isNotEmpty()) {
                item {
                    GlassCard(
                        borderColor = RudraCoral,
                        backgroundColor = RudraCoral.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Warning",
                                tint = RudraCoral,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "समय ओवरलैप चेतावनी (Overlap Warning)!" else "Schedule Overlap Detected!",
                                    color = RudraCoral,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = overlappingPairs.first(),
                                    color = RudraTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Subject Targets Collapsible Section
            if (showSubjectTargetsPane) {
                item {
                    GlassCard(
                        borderColor = RudraGold,
                        backgroundColor = RudraNavyCard
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isHindi) "विषयवार अध्ययन लक्ष्य (Subject Targets)" else "Subject Study Targets (Preserved)",
                                    color = RudraGold,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = if (isHindi) "कुल: 13 घंटे" else "Total: 13.0 hrs",
                                    color = RudraCyanNeon,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Subject.entries.forEach { subject ->
                                SubjectTargetItem(
                                    subject = subject,
                                    completedMinutes = subjectCompletedMap[subject.key] ?: 0,
                                    isHindi = isHindi,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Filter Chips Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filters = listOf(
                        "ALL" to (if (isHindi) "सभी (All)" else "All"),
                        "STUDY" to (if (isHindi) "अध्ययन (Study)" else "Study"),
                        "HEALTH" to (if (isHindi) "स्वास्थ्य/स्वच्छता" else "Health/Hygiene"),
                        "MEALS" to (if (isHindi) "भोजन/ब्रेक" else "Meals/Breaks"),
                        "SLEEP" to (if (isHindi) "निद्रा/योजना" else "Sleep/Routine")
                    )

                    items(filters) { (key, label) ->
                        FilterChip(
                            selected = selectedFilter == key,
                            onClick = { selectedFilter = key },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RudraCyanNeon,
                                selectedLabelColor = Color.Black,
                                containerColor = RudraNavyCardElevated,
                                labelColor = RudraTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedFilter == key,
                                borderColor = if (selectedFilter == key) RudraCyanNeon else RudraNavyBorder
                            )
                        )
                    }
                }
            }

            // Timeline Activity Cards
            items(filteredTasks, key = { it.id }) { task ->
                TimelineTaskCard(
                    task = task,
                    isHindi = isHindi,
                    is24h = is24h,
                    viewModel = viewModel,
                    onEdit = {
                        taskToEdit = task
                        showAddEditDialog = true
                    },
                    onDelete = { viewModel.deleteTask(task) }
                )
            }
        }
    }

    // Add / Edit Task Dialog
    if (showAddEditDialog) {
        AddEditTaskDialog(
            task = taskToEdit,
            isHindi = isHindi,
            onDismiss = { showAddEditDialog = false },
            onSave = { updatedTask ->
                viewModel.saveTask(updatedTask)
                showAddEditDialog = false
            }
        )
    }

    // Restore Confirm Dialog
    if (showRestoreConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirmDialog = false },
            title = {
                Text(
                    text = if (isHindi) "मूल दिनचर्या पुनर्स्थापित करें?" else "Restore Default Schedule?",
                    color = RudraTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isHindi)
                        "क्या आप मूल 23 गतिविधियों और 13 घंटे के अध्ययन समय सारिणी को पुनर्स्थापित करना चाहते हैं?"
                    else
                        "This will reset all routine tasks back to the original 23 activities and 13-hour competitive study schedule.",
                    color = RudraTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.restoreDefaultSchedule()
                        showRestoreConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RudraCyanNeon, contentColor = Color.Black)
                ) {
                    Text(if (isHindi) "पुनर्स्थापित करें" else "Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirmDialog = false }) {
                    Text(if (isHindi) "रद्द करें" else "Cancel", color = RudraTextMuted)
                }
            },
            containerColor = RudraNavyCardElevated
        )
    }
}

@Composable
fun TimelineTaskCard(
    task: RoutineTask,
    isHindi: Boolean,
    is24h: Boolean,
    viewModel: RudraViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val cat = TaskCategory.fromKey(task.category)
    val catColor = Color(cat.colorHex)
    val subject = Subject.fromKey(task.subjectKey)
    var showMenu by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = when {
            task.isCompleted -> RudraEmerald.copy(alpha = 0.5f)
            task.isSkipped -> RudraOrange.copy(alpha = 0.4f)
            task.isMissed -> RudraCoral.copy(alpha = 0.4f)
            else -> RudraNavyBorder
        },
        backgroundColor = if (task.isCompleted) RudraNavyCard.copy(alpha = 0.8f) else RudraNavyCardElevated
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category & Subject tags
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(catColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isHindi) cat.labelHi else cat.labelEn,
                            color = catColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (subject != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(subject.colorHex).copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${subject.targetHours}h",
                                color = Color(subject.colorHex),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Time Range
                Text(
                    text = "${viewModel.formatTimeString(task.startTime, is24h)} - ${viewModel.formatTimeString(task.endTime, is24h)}",
                    color = RudraGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = if (isHindi) task.titleHi else task.titleEn,
                color = if (task.isCompleted) RudraTextMuted else RudraTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )

            if (task.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📝 ${task.notes}",
                    color = RudraTextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row: Done, Skip, Missed, Edit / Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Done Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (task.isCompleted) RudraEmerald else RudraNavyCard)
                            .border(1.dp, if (task.isCompleted) RudraEmerald else RudraNavyBorder, RoundedCornerShape(6.dp))
                            .clickable {
                                if (task.isCompleted) viewModel.resetTaskStatus(task.id)
                                else viewModel.markTaskDone(task.id)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (task.isCompleted) "✓ " + (if (isHindi) "पूर्ण" else "Done") else (if (isHindi) "पूर्ण करें" else "Mark Done"),
                            color = if (task.isCompleted) Color.Black else RudraTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Skip Today Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (task.isSkipped) RudraOrange else RudraNavyCard)
                            .border(1.dp, if (task.isSkipped) RudraOrange else RudraNavyBorder, RoundedCornerShape(6.dp))
                            .clickable {
                                if (task.isSkipped) viewModel.resetTaskStatus(task.id)
                                else viewModel.markTaskSkipped(task.id)
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (task.isSkipped) (if (isHindi) "छोड़ा गया" else "Skipped") else (if (isHindi) "छोड़ें" else "Skip"),
                            color = if (task.isSkipped) Color.Black else RudraOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Missed Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (task.isMissed) RudraCoral else RudraNavyCard)
                            .border(1.dp, if (task.isMissed) RudraCoral else RudraNavyBorder, RoundedCornerShape(6.dp))
                            .clickable {
                                if (task.isMissed) viewModel.resetTaskStatus(task.id)
                                else viewModel.markTaskMissed(task.id)
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (task.isMissed) (if (isHindi) "छूटा" else "Missed") else (if (isHindi) "मिस" else "Miss"),
                            color = if (task.isMissed) Color.White else RudraCoral,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Edit / Delete Overflow
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = RudraTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isHindi) "संपादित करें (Edit)" else "Edit") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isHindi) "हटाएँ (Delete)" else "Delete") },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = RudraCoral) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditTaskDialog(
    task: RoutineTask?,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onSave: (RoutineTask) -> Unit
) {
    var titleHi by remember { mutableStateOf(task?.titleHi ?: "") }
    var titleEn by remember { mutableStateOf(task?.titleEn ?: "") }
    var startTime by remember { mutableStateOf(task?.startTime ?: "06:00") }
    var endTime by remember { mutableStateOf(task?.endTime ?: "07:00") }
    var category by remember { mutableStateOf(task?.category ?: "STUDY") }
    var subjectKey by remember { mutableStateOf(task?.subjectKey ?: "") }
    var notes by remember { mutableStateOf(task?.notes ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (task == null) {
                    if (isHindi) "नई गतिविधि जोड़ें" else "Add New Activity"
                } else {
                    if (isHindi) "गतिविधि संपादित करें" else "Edit Activity"
                },
                color = RudraTextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = titleHi,
                    onValueChange = { titleHi = it },
                    label = { Text(if (isHindi) "शीर्षक (हिन्दी)" else "Title (Hindi)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = RudraTextPrimary,
                        unfocusedTextColor = RudraTextPrimary
                    )
                )

                OutlinedTextField(
                    value = titleEn,
                    onValueChange = { titleEn = it },
                    label = { Text(if (isHindi) "शीर्षक (English)" else "Title (English)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = RudraTextPrimary,
                        unfocusedTextColor = RudraTextPrimary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start (HH:mm)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = RudraTextPrimary,
                            unfocusedTextColor = RudraTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End (HH:mm)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = RudraTextPrimary,
                            unfocusedTextColor = RudraTextPrimary
                        )
                    )
                }

                // Category selection chips
                Text(
                    text = if (isHindi) "श्रेणी चुनें:" else "Select Category:",
                    color = RudraTextSecondary,
                    fontSize = 11.sp
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(TaskCategory.entries.toTypedArray()) { cat ->
                        FilterChip(
                            selected = category == cat.key,
                            onClick = { category = cat.key },
                            label = { Text(if (isHindi) cat.labelHi else cat.labelEn, fontSize = 10.sp) }
                        )
                    }
                }

                if (category == "STUDY") {
                    Text(
                        text = if (isHindi) "विषय चुनें:" else "Select Subject:",
                        color = RudraTextSecondary,
                        fontSize = 11.sp
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(Subject.entries.toTypedArray()) { sub ->
                            FilterChip(
                                selected = subjectKey == sub.key,
                                onClick = { subjectKey = sub.key },
                                label = { Text(if (isHindi) sub.nameHi else sub.nameEn, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = RudraCoral,
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titleHi.isBlank() && titleEn.isBlank()) {
                        errorMessage = if (isHindi) "कृपया शीर्षक भरें" else "Please enter title"
                        return@Button
                    }
                    val updated = (task ?: RoutineTask(
                        id = 0,
                        titleHi = titleHi.ifBlank { titleEn },
                        titleEn = titleEn.ifBlank { titleHi },
                        startTime = startTime,
                        endTime = endTime,
                        category = category,
                        subjectKey = if (category == "STUDY" && subjectKey.isNotBlank()) subjectKey else null,
                        notes = notes
                    )).copy(
                        titleHi = titleHi.ifBlank { titleEn },
                        titleEn = titleEn.ifBlank { titleHi },
                        startTime = startTime,
                        endTime = endTime,
                        category = category,
                        subjectKey = if (category == "STUDY" && subjectKey.isNotBlank()) subjectKey else null,
                        notes = notes
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RudraCyanNeon, contentColor = Color.Black)
            ) {
                Text(if (isHindi) "सुरक्षित करें" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isHindi) "रद्द करें" else "Cancel", color = RudraTextMuted)
            }
        },
        containerColor = RudraNavyCardElevated
    )
}
