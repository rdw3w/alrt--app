package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RudraAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "RUDRA STUDY LIFE PRO"
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "आपकी दिनचर्या का समय हो गया है!"
        val notificationId = intent.getIntExtra("EXTRA_NOTIFICATION_ID", 1001)
        val isWaterReminder = intent.getBooleanExtra("EXTRA_IS_WATER", false)
        val isHindi = intent.getBooleanExtra("EXTRA_IS_HINDI", true)

        val helper = RudraNotificationHelper(context)
        val channelId = if (isWaterReminder) RudraNotificationHelper.CHANNEL_WATER else RudraNotificationHelper.CHANNEL_ROUTINE

        helper.showNotification(
            notificationId = notificationId,
            title = title,
            message = message,
            channelId = channelId
        )

        // If it was a water reminder, schedule the next periodic reminder
        if (isWaterReminder) {
            val intervalMin = intent.getIntExtra("EXTRA_INTERVAL_MIN", 60)
            helper.scheduleNextWaterReminder(intervalMin, isHindi)
        }
    }
}
