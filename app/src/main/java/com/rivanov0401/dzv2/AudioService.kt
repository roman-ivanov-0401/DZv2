package com.rivanov0401.dzv2

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AudioService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var currentUri: String? = null

    private val NOTIFICATION_ID = 1
    private val channelId = "audio_service_channel"
    private val channelName = "Audio Playback"

    companion object {
        const val ACTION_PLAY = "com.example.app.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.app.ACTION_PAUSE"
        const val ACTION_STOP = "com.example.app.ACTION_STOP"

        const val EXTRA_AUDIO_URI = "com.example.app.EXTRA_AUDIO_URI"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> playAudio(intent.getStringExtra(EXTRA_AUDIO_URI))
            ACTION_PAUSE -> pauseAudio()
            ACTION_STOP -> stopAudio()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun pauseAudio() {
        mediaPlayer?.pause()
    }

    private fun stopAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(true)
        stopSelf()
    }

    private fun playAudio(uri: String?) {
        if (uri != null && (mediaPlayer == null || uri != currentUri)) {
            mediaPlayer?.release()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, Uri.parse(uri))
                setOnCompletionListener {
                    stopSelf()
                }
                prepare()
                start()
            }
            currentUri = uri
            startForeground(NOTIFICATION_ID, createNotification())
        } else if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    private fun createNotification(): Notification {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)

        val stopIntent = Intent(this, AudioService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Playing Audio")
            .setContentText("Your audio is playing")
            .setSmallIcon(R.drawable.name_icn_24_playlist)
            .addAction(R.drawable.carbon_close_filled, "Stop", stopPendingIntent)
            .setOngoing(true)
            .build()
    }
}