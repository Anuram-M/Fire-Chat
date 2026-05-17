package com.ram.firechat.service

import android.annotation.SuppressLint
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ram.firechat.R

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        createNotification(message)
        Log.d("FIRENOTI", "onMessageReceived: notification receiverd")
    }

    @SuppressLint("MissingPermission")
    private fun createNotification(message: RemoteMessage) {

        val chatId = message.data.get("chatId")

        val groupKey = "chat_${chatId}"
        val notificationId = System.currentTimeMillis().toInt()

        val body = message.data.get("body")

        val sender = message.data.get("senderName")
        val person = Person.Builder()
            .setName(sender)
            .build()

        val messaageStyle = NotificationCompat.MessagingStyle(person)
            .addMessage(body, System.currentTimeMillis(), person)
        val notification = NotificationCompat.Builder(this, "101")
//            .setContentTitle(message.data.get("title"))
//            .setContentText(message.data.get("body"))
            .setStyle(messaageStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setGroup(groupKey)
            .setSmallIcon(R.mipmap.ic_launcher_new_foreground)
            .build()

        val manager = NotificationManagerCompat.from(this)

        manager.notify(notificationId, notification)

        val summary = NotificationCompat.Builder(this, "101")
            .setContentTitle("New Message")
            .setGroup(groupKey)
            .setSmallIcon(R.mipmap.ic_launcher_new_foreground)
            .setGroupSummary(true)
            .build()

        manager.notify(groupKey.hashCode(), summary)
    }
}