package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.RoutineTask
import java.util.Calendar

class RudraNotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val CHANNEL_ROUTINE = "rudra_routine_alerts"
        const val CHANNEL_WATER = "rudra_water_alerts"
        const val REQUEST_CODE_WATER = 9000
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val routineChannel = NotificationChannel(
                CHANNEL_ROUTINE,
                "Rudra Routine & Study Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Timetable and study session alerts"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 350, 200, 350)
                setSound(defaultSoundUri, audioAttributes)
            }

            val waterChannel = NotificationChannel(
                CHANNEL_WATER,
                "Rudra Health & Water Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Water intake and stretch breaks"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(routineChannel)
            notificationManager.createNotificationChannel(waterChannel)
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun showNotification(
        notificationId: Int,
        title: String,
        message: String,
        channelId: String = CHANNEL_ROUTINE
    ) {
        if (!hasNotificationPermission()) return

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 350, 200, 350))
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, builder.build())
    }

    fun sendTestNotification(isHindi: Boolean) {
        val title = if (isHindi) "रुद्र स्टडी लाइफ प्रो (RUDRA PRO)" else "RUDRA STUDY LIFE PRO"
        val message = if (isHindi) {
            "सूचना परीक्षण सफल! पृष्ठभूमि और अलार्म रिमाइंडर सक्रिय हैं। अनुशासन ही सफलता की कुंजी है।"
        } else {
            "Notification test successful! Background routine alerts are active and ready."
        }
        showNotification(9999, title, message)
    }

    fun scheduleTaskReminder(
        taskId: Long,
        title: String,
        message: String,
        hour: Int,
        minute: Int,
        offsetMinutes: Int = 0
    ) {
        val requestCode = (taskId * 10 + (offsetMinutes + 20)).toInt()

        val intent = Intent(context, RudraAlarmReceiver::class.java).apply {
            putExtra("EXTRA_TITLE", title)
            putExtra("EXTRA_MESSAGE", message)
            putExtra("EXTRA_NOTIFICATION_ID", requestCode)
            putExtra("EXTRA_IS_WATER", false)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (offsetMinutes != 0) {
                add(Calendar.MINUTE, -offsetMinutes)
            }
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun scheduleAllRoutineReminders(
        tasks: List<RoutineTask>,
        isHindi: Boolean,
        preAlerts: Boolean
    ) {
        tasks.forEach { task ->
            val parts = task.startTime.split(":")
            if (parts.size >= 2) {
                val hour = parts[0].toIntOrNull() ?: return@forEach
                val minute = parts[1].toIntOrNull() ?: return@forEach

                val title = if (isHindi) task.titleHi else task.titleEn

                // 1. Alarm exactly at start time
                val startMessage = if (isHindi) {
                    "समय हो गया है: ${task.titleHi} (${task.startTime} से ${task.endTime})"
                } else {
                    "Time to start: ${task.titleEn} (${task.startTime} to ${task.endTime})"
                }
                scheduleTaskReminder(task.id, title, startMessage, hour, minute, offsetMinutes = 0)

                // 2. Pre-alerts (5 mins before and 15 mins before)
                if (preAlerts) {
                    val pre5Msg = if (isHindi) {
                        "5 मिनट में शुरू: ${task.titleHi}। तैयारी कर लें!"
                    } else {
                        "Starting in 5 mins: ${task.titleEn}. Get ready!"
                    }
                    scheduleTaskReminder(task.id, title, pre5Msg, hour, minute, offsetMinutes = 5)

                    // 15 min pre-alert for major study sessions
                    if (task.category == "STUDY") {
                        val pre15Msg = if (isHindi) {
                            "15 मिनट में अध्ययन सत्र: ${task.titleHi}।"
                        } else {
                            "Upcoming study in 15 mins: ${task.titleEn}."
                        }
                        scheduleTaskReminder(task.id, title, pre15Msg, hour, minute, offsetMinutes = 15)
                    }
                }
            }
        }
    }

    fun scheduleNextWaterReminder(intervalMinutes: Int, isHindi: Boolean) {
        val intent = Intent(context, RudraAlarmReceiver::class.java).apply {
            putExtra("EXTRA_TITLE", if (isHindi) "जल संतुलन अलर्ट (Water Reminder)" else "Water & Health Alert")
            putExtra("EXTRA_MESSAGE", if (isHindi) "शरीर और मस्तिष्क को ऊर्जावान रखने के लिए 1 गिलास पानी पिएं!" else "Drink a glass of water to keep your brain and body refreshed!")
            putExtra("EXTRA_NOTIFICATION_ID", REQUEST_CODE_WATER)
            putExtra("EXTRA_IS_WATER", true)
            putExtra("EXTRA_INTERVAL_MIN", intervalMinutes)
            putExtra("EXTRA_IS_HINDI", isHindi)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_WATER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + (intervalMinutes * 60 * 1000L)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
