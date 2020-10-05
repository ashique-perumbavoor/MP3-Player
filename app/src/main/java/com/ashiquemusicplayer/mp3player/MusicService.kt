package com.ashiquemusicplayer.mp3player

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MusicService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("hello", "Started")
        val executorService: ExecutorService = Executors.newCachedThreadPool()
        executorService.execute {
            val mp: MediaPlayer = MediaPlayer.create(this, R.raw.closer)
            mp.start()
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("hello", "Destroyed")
    }
}