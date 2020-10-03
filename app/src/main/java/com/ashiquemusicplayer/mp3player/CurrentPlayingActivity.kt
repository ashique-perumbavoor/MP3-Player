package com.ashiquemusicplayer.mp3player

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_current_playing.*

@Suppress("DEPRECATION", "SENSELESS_COMPARISON")
class CurrentPlayingActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    private var totalTime = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_playing)
        // getting the information's of the song
        val songInfo = intent.getStringArrayExtra("songInfo")
        // setting song name
        song_name.text = songInfo?.get(0)
        // setting song URI
        val songUri = songInfo?.get(1)

        // Play and pause song
        playPauseButton.setOnClickListener {
            playBtnClick()
        }

        // Playing the song
        mp = MediaPlayer.create(this, songUri?.toUri())
//        mp.start()

        startService(Intent(this, MusicService::class.java).putExtra("songUri", songUri))

        mp.isLooping = true
        playPauseButton.setBackgroundResource(R.drawable.pause)

        // Progressbar creating
        totalTime = mp.duration
        progressBar.max = totalTime
        progressBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mp.seekTo(progress)
                    }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {
                }
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )

        // Threading progressbar for user to change the progressbar and for moving the song to their desired time of the song
        Thread {
            while (mp != null) {
                try {
                    val msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1)
                } catch (e: InterruptedException) { }
            }
        }.start()
    }

    // changing current position of progressbar, elapsed and remaining time
    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        @SuppressLint("SetTextI18n")
        override fun handleMessage(msg: Message) {
            val currentPosition = msg.what
            progressBar.progress = currentPosition
            val elapsedTime = createTimeLabel(currentPosition)
            elapsedTimeLabel.text = elapsedTime
            val remainingTime = createTimeLabel(totalTime - currentPosition)
            remainingTimeLabel.text = "-$remainingTime"
        }
    }

    // Making the time label of current position of the song
    fun createTimeLabel(time: Int): String {
        var timeLabel: String
        val min = time / 1000 / 60
        val sec = time / 1000 % 60
        timeLabel = "$min:"
        if (sec < 10) {
            timeLabel += "0"
        }
        timeLabel += sec
        return timeLabel
    }

    // playing and pausing song and changing the button image
    private fun playBtnClick() {
        if (mp.isPlaying) {
            mp.pause()
            playPauseButton.setBackgroundResource(R.drawable.play)
        } else {
            mp.start()
            playPauseButton.setBackgroundResource(R.drawable.pause)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        mp.stop()
    }
}