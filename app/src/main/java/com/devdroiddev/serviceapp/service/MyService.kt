package com.devdroiddev.serviceapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.media.audiofx.BassBoost
import android.media.audiofx.BassBoost.Settings
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.Settings.System.DEFAULT_RINGTONE_URI
import android.support.v4.os.IResultReceiver.Default
import androidx.core.app.NotificationCompat
import com.devdroiddev.serviceapp.MainActivity
import com.devdroiddev.serviceapp.R
import android.provider.Settings.System.DEFAULT_RINGTONE_URI
import android.util.Log


class MyService : Service() {

    companion object {
        var isServiceRunning: Boolean = false
    }

    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "ForegroundServiceChannel"
    private lateinit var mediaPlayer: MediaPlayer
    private val myBinder = MyBinder()

    inner class MyBinder : Binder() {
        fun getService(): MyService {
            return this@MyService
        }
    }


    override fun onBind(p0: Intent?): IBinder? {
        return myBinder
    }

    override fun onCreate() {
        isServiceRunning = true
        mediaPlayer = MediaPlayer.create(this, DEFAULT_RINGTONE_URI)
        mediaPlayer.isLooping = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var from = intent?.getStringExtra("from")

        when (from) {
            "onStartMethod" -> stopForeground(true)
            "onStopMethod" -> startServiceForeground()
        }
        mediaPlayer.start()
        return START_STICKY
    }

    private fun startServiceForeground() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)

        // Create the Notification
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Running in the foreground")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }


    override fun onDestroy() {
        mediaPlayer.stop()
        isServiceRunning = false
        super.onDestroy()
    }

}



