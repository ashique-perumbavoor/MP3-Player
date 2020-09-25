package com.ashiquemusicplayer.mp3player

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_current_playing.*

@Suppress("DEPRECATION")
class CurrentPlayingActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    private var totalTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_playing)

        // Play and pause song
        playPauseButton.setOnClickListener {
            playBtnClick()
        }

        // Assigning the music for playing
        mp = MediaPlayer.create(this, R.raw.closer)
        mp.isLooping = true

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

        // Threading progressbar for user to change the progressbar and for moving the music to their desired time of the music
        Thread {
                val msg = Message()
                msg.what = mp.currentPosition
                handler.sendMessage(msg)
                Thread.sleep(1)
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
}