package com.psethi.zenselect.manager

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings

class ZenManager(private val context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun hasPermission(): Boolean {
        return notificationManager.isNotificationPolicyAccessGranted
    }

    fun enableZenMode() {
        if (hasPermission()) {
            notificationManager.setInterruptionFilter(
                NotificationManager.INTERRUPTION_FILTER_NONE
            )
        }else {
            requestDndPermission()
        }
    }

    fun disableZenMode() {
        if (hasPermission()) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
        }
    }

    private fun requestDndPermission() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            context.startActivity(intent)
        }
    }
}