package com.ashiquemusicplayer.mp3player.objects

import android.media.MediaPlayer
import android.net.Uri
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object MusicObject {

    private lateinit var mediaPlayer: MediaPlayer
    private val executor: ExecutorService = Executors.newCachedThreadPool()
    private var flag = 0
    private var musicName = ""
    private var musicID = 1
    private var musicUri: Uri? = null

    // Function to play music
    fun playMusic(mp: MediaPlayer,name: String,ID: Int,Uri: Uri) {
        if (flag > 0) {
            pauseMusic()
        }
        flag++
        musicName = name
        musicID = ID
        musicUri = Uri
        mediaPlayer = mp
        executor.execute {
            mediaPlayer.start()
        }
    }

    // Function to play currently playing music again
    fun playMusicAgain() {
        executor.execute {
            mediaPlayer.start()
        }
    }

    // Function to pause music
    fun pauseMusic() {
        mediaPlayer.pause()
    }

    // Function to stop music
    fun stopMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }

    fun secondaryPlayMusic(): Array<Any?> {
        return arrayOf(musicName, musicUri, musicID, mediaPlayer)
    }

    fun getMediaPlayer(): MediaPlayer {
        return mediaPlayer
    }
}