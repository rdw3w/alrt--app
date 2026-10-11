package com.example.ui.screens

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.RudraCoral
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
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: RudraViewModel,
    modifier: Modifier = Modifier
) {
    val isHindi by viewModel.isHindi.collectAsState()
    val is24h by viewModel.is24HourFormat.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    val preAlertsEnabled by viewModel.preAlertsEnabled.collectAsState()
    val remindersCount by viewModel.remindersScheduledCount.collectAsState()
    var rescheduleFeedback by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    var showExportDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonInput by remember { mutableStateOf("") }
    var importMessage by remember { mutableStateOf<String?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }
    var testNotifSent by remember { mutableStateOf(false) }

    // Android 13+ Notification Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.sendTestAlert()
            testNotifSent = true
        }
    }

    val hasNotifPermission = viewModel.notificationHelper.hasNotificationPermission()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(RudraNavyDarkest)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Settings & Language
        item {
            Text(
                text = if (isHindi) "ऐप एवं भाषा सेटिंग्स (App & Language)" else "App & Language Settings",
                color = RudraTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            GlassCard(
                borderColor = RudraNavyBorder,
                backgroundColor = RudraNavyCard
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Language Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = RudraCyanNeon, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "भाषा (Language)" else "Interface Language",
                                    color = RudraTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (isHindi) "वर्तमान: हिन्दी" else "Current: English",
                                    color = RudraTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.toggleLanguage() },
                            colors = ButtonDefaults.buttonColors(containerColor = RudraCyanNeon, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("settings_lang_btn")
                        ) {
                            Text(if (isHindi) "English करें" else "Switch हिन्दी", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // 12h / 24h Clock Format
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = RudraGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "घड़ी प्रारूप (Time Format)" else "Time Format",
                                    color = RudraTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (is24h) "24-घंटे प्रारूप (24-Hour)" else "12-घंटे प्रारूप (12-Hour AM/PM)",
                                    color = RudraTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = is24h,
                            onCheckedChange = { viewModel.toggleTimeFormat() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = RudraGold
                            ),
                            modifier = Modifier.testTag("switch_time_format")
                        )
                    }
                }
            }
        }

        // Real Notifications & Alarm Section
        item {
            Text(
                text = if (isHindi) "अलर्ट व सूचनाएं (Alerts & Reminders)" else "Notifications & Alarms",
                color = RudraTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            GlassCard(
                borderColor = RudraCyanDark,
                backgroundColor = RudraNavyCard
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Notification Permission Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = RudraEmerald, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "सिस्टम सूचना अनुमति" else "Notification Permission",
                                    color = RudraTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (hasNotifPermission) {
                                        if (isHindi) "सक्रिय है (Enabled ✓)" else "Enabled ✓"
                                    } else {
                                        if (isHindi) "अनुमति आवश्यक" else "Permission Required"
                                    },
                                    color = if (hasNotifPermission) RudraEmerald else RudraCoral,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (!hasNotifPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Button(
                                onClick = {
                                    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RudraCyanNeon, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (isHindi) "अनुमति दें" else "Allow", fontSize = 11.sp)
                            }
                        }
                    }

                    // Test Notification Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isHindi) "तत्काल सूचना परीक्षण" else "Test Notification Now",
                                color = RudraTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (testNotifSent) {
                                    if (isHindi) "सूचना भेजी गई! चेक करें।" else "Alert sent! Check status bar."
                                } else {
                                    if (isHindi) "फोन पर सूचना की जांच करें" else "Verify notification on device"
                                },
                                color = if (testNotifSent) RudraEmerald else RudraTextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotifPermission) {
                                    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.sendTestAlert()
                                    testNotifSent = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RudraVioletNeon, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_test_notification")
                        ) {
                            Text(if (isHindi) "टेस्ट भेजें" else "Test Alert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Sound Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = RudraCyanNeon, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isHindi) "अलार्म ध्वनि (Sound)" else "Alarm Sound",
                                color = RudraTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = { viewModel.toggleSound() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = RudraCyanNeon
                            )
                        )
                    }

                    // Vibration Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Vibration, contentDescription = null, tint = RudraGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isHindi) "कंपन (Vibration)" else "Vibration Alerts",
                                color = RudraTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = { viewModel.toggleVibration() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = RudraGold
                            )
                        )
                    }

                    // Pre-Alerts (15 min and 5 min before start)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = RudraCyanNeon, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "पूर्व चेतावनी (Pre-Alerts 15m/5m)" else "Pre-Alerts (15m & 5m before)",
                                    color = RudraTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (preAlertsEnabled) {
                                        if (isHindi) "गतिविधि से 15 और 5 मिनट पहले सचेत करें" else "Notify 15 and 5 mins before start"
                                    } else {
                                        if (isHindi) "केवल निर्धारित समय पर अलार्म" else "Only at start time"
                                    },
                                    color = RudraTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = preAlertsEnabled,
                            onCheckedChange = { viewModel.togglePreAlerts() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = RudraCyanNeon
                            ),
                            modifier = Modifier.testTag("switch_pre_alerts")
                        )
                    }

                    // Reschedule All 23 Alarms Button
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(RudraNavyCardElevated)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHindi) "दैनिक रूटीन अलार्म शेड्यूलर" else "Daily Routine Alarm Scheduler",
                                    color = RudraTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (rescheduleFeedback) {
                                        if (isHindi) "सभी 23 अलार्म सफलतापूर्वक सक्रिय किए गए! ✓" else "All 23 alarms successfully active! ✓"
                                    } else {
                                        if (isHindi) "ऐप बंद रहने व रीबूट के बाद भी अलर्ट चालू रखने हेतु" else "Persistent background alarms for all tasks"
                                    },
                                    color = if (rescheduleFeedback) RudraEmerald else RudraTextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.scheduleAllRemindersNow()
                                    rescheduleFeedback = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RudraCyanNeon, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_reschedule_alarms")
                            ) {
                                Text(if (isHindi) "शेड्यूल करें" else "Reschedule", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Data Storage & Privacy Section
        item {
            Text(
                text = if (isHindi) "डेटा बैकअप व गोपनीयता (Data & Privacy)" else "Data Storage & Privacy",
                color = RudraTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            GlassCard(
                borderColor = RudraNavyBorder,
                backgroundColor = RudraNavyCard
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = RudraEmerald, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi)
                                "आपकी सभी दिनचर्या, अध्ययन लॉग और सेटिंग्स आपके डिवाइस में सुरक्षित (Local Room DB) रहती हैं।"
                            else
                                "All routine data, checklist, and focus study logs are securely stored locally on your device.",
                            color = RudraTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Export Data
                        Button(
                            onClick = {
                                scope.launch {
                                    exportedJsonText = viewModel.repository.exportDataAsJson()
                                    showExportDialog = true
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_export_data"),
                            colors = ButtonDefaults.buttonColors(containerColor = RudraNavyCardElevated, contentColor = RudraCyanNeon),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isHindi) "एक्सपोर्ट JSON" else "Export JSON", fontSize = 11.sp)
                        }

                        // Import Data
                        Button(
                            onClick = {
                                importMessage = null
                                importJsonInput = ""
                                showImportDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_import_data"),
                            colors = ButtonDefaults.buttonColors(containerColor = RudraNavyCardElevated, contentColor = RudraGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isHindi) "इम्पोर्ट JSON" else "Import JSON", fontSize = 11.sp)
                        }
                    }

                    // Reset all data
                    Button(
                        onClick = { showResetDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_reset_all"),
                        colors = ButtonDefaults.buttonColors(containerColor = RudraCoral.copy(alpha = 0.2f), contentColor = RudraCoral),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isHindi) "सभी डेटा रीसेट करें (मूल दिनचर्या)" else "Reset All Data to Defaults", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text(
                    text = if (isHindi) "डेटा बैकअप (JSON Export)" else "Exported Routine JSON",
                    color = RudraTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isHindi)
                            "आप इस JSON डेटा को सुरक्षित रूप से कॉपी करके सहेज सकते हैं:"
                        else
                            "Copy this JSON snippet to keep a backup of your daily routine and focus logs:",
                        color = RudraTextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(RudraNavyDarkest)
                            .padding(8.dp)
                    ) {
                        LazyColumn {
                            item {
                                Text(
                                    text = exportedJsonText,
                                    color = RudraCyanNeon,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RudraCyanNeon, contentColor = Color.Black)
                ) {
                    Text(if (isHindi) "ठीक है" else "Close")
                }
            },
            containerColor = RudraNavyCardElevated
        )
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = {
                Text(
                    text = if (isHindi) "डेटा इम्पोर्ट करें (JSON Import)" else "Import Routine JSON",
                    color = RudraTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isHindi) "बैकअप JSON डेटा यहाँ पेस्ट करें:" else "Paste backup JSON data below:",
                        color = RudraTextSecondary,
                        fontSize = 11.sp
                    )
                    OutlinedTextField(
                        value = importJsonInput,
                        onValueChange = { importJsonInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = RudraTextPrimary,
                            unfocusedTextColor = RudraTextPrimary
                        )
                    )
                    if (importMessage != null) {
                        Text(
                            text = importMessage ?: "",
                            color = if (importMessage?.contains("✓") == true) RudraEmerald else RudraCoral,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val success = viewModel.repository.importDataFromJson(importJsonInput)
                            if (success) {
                                importMessage = if (isHindi) "डेटा सफलतापूर्वक इम्पोर्ट हुआ! ✓" else "Data imported successfully! ✓"
                            } else {
                                importMessage = if (isHindi) "अमान्य JSON प्रारूप!" else "Invalid JSON format!"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RudraGold, contentColor = Color.Black)
                ) {
                    Text(if (isHindi) "इम्पोर्ट करें" else "Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text(if (isHindi) "बंद करें" else "Close", color = RudraTextMuted)
                }
            },
            containerColor = RudraNavyCardElevated
        )
    }

    // Reset Confirm Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = if (isHindi) "सभी डेटा रीसेट करें?" else "Reset All Routine Data?",
                    color = RudraCoral,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isHindi)
                        "यह क्रिया आपकी सभी संशोधित गतिविधियों और इतिहास को हटाकर मूल 23 गतिविधियों और 13 घंटे के टाइमटेबल को फिर से लोड कर देगी।"
                    else
                        "This will restore the original 23-task timetable and default checklist. All custom items will be reset.",
                    color = RudraTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.restoreDefaultSchedule()
                        viewModel.restoreDefaultChecklist()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RudraCoral, contentColor = Color.White)
                ) {
                    Text(if (isHindi) "हाँ, रीसेट करें" else "Yes, Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(if (isHindi) "रद्द करें" else "Cancel", color = RudraTextMuted)
                }
            },
            containerColor = RudraNavyCardElevated
        )
    }
}
