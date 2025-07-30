package com.example.notlisten

import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import org.json.JSONArray

class NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            val packageName = it.packageName
            if (packageName == "com.whatsapp") {
                val extras = it.notification.extras
                val title = extras.getString("android.title")
                val text = extras.getCharSequence("android.text")?.toString()

                val logEntry = "From: $title - Message: $text"
                Log.d("NotifLogger", logEntry)

                val prefs = getSharedPreferences("notifs", Context.MODE_PRIVATE)
                val stored = prefs.getString("log", "[]")
                val jsonArray = JSONArray(stored)

                // Auto-clear logic: keep only last 100
                val maxItems = 100
                val newArray = JSONArray()
                val start = if (jsonArray.length() >= maxItems) jsonArray.length() - maxItems + 1 else 0
                for (i in start until jsonArray.length()) {
                    newArray.put(jsonArray.getString(i))
                }
                newArray.put(logEntry)

                prefs.edit().putString("log", newArray.toString()).apply()
            }
        }
    }
}
