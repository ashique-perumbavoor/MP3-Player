package com.ashiquemusicplayer.mp3player

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.net.toUri
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MusicService: Service() {

    private lateinit var mp: MediaPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val songUri = intent?.getStringExtra("songUri")
        mp = MediaPlayer.create(this, songUri?.toUri())
        mp.isLooping
        mp.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)
        mp.setScreenOnWhilePlaying(true)

        val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        executorService.execute {
            mp.start()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        mp.stop()
        Log.d("hello", "destroyed")
    }
}