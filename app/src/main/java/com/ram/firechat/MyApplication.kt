package com.ram.firechat

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        val fireConfig = FirebaseRemoteConfig.getInstance()
        val config = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60
        }
        val defaults = mapOf(
            "show_new_ui" to false
        )
        fireConfig.setConfigSettingsAsync(config)
        fireConfig.setDefaultsAsync(defaults)

        val nChannel  = NotificationChannel(
            "101",
            "messagingChannel",
            NotificationManager.IMPORTANCE_HIGH
        )
        val nManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(nChannel)

    }
}