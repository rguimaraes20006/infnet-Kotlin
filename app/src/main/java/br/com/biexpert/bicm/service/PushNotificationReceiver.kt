package br.com.biexpert.bicm.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import br.com.biexpert.bicm.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


class PushNotificationReceiver : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle the received message here


        val notification = NotificationCompat.Builder(this, "BIEX_PUSH")
            .setContentTitle( remoteMessage.notification?.title.toString())
            .setContentText(remoteMessage.notification?.title.toString())
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .build()


        println(notification)



    }






}

