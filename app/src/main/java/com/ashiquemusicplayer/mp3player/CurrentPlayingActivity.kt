package com.ashiquemusicplayer.mp3player

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_current_playing.*

class CurrentPlayingActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    private var totalTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_playing)

        playPauseButton.setOnClickListener {
            playBtnClick()
        }

        mp = MediaPlayer.create(this, R.raw.closer)
        mp.isLooping = true
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

        Thread {
            while (mp != null) {
                try {
                    var msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1)
                } catch (e: InterruptedException) {
                }
            }
        }.start()

        if (savedInstanceState != null) {

        }
    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            var currentPosition = msg.what
            progressBar.progress = currentPosition
            var elapsedTime = createTimeLabel(currentPosition)
            elapsedTimeLabel.text = elapsedTime
            var remainingTime = createTimeLabel(totalTime - currentPosition)
            remainingTimeLabel.text = "-$remainingTime"
        }
    }

    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        var min = time / 1000 / 60
        var sec = time / 1000 % 60
        var milli = time

        timeLabel = "$min:"
        if (sec < 10) {
            timeLabel += "0"
        }
        timeLabel += sec
        return timeLabel
    }

    fun playBtnClick() {

        if (mp.isPlaying) {
            mp.pause()
            playPauseButton.setBackgroundResource(R.drawable.play)
        } else {
            mp.start()
            playPauseButton.setBackgroundResource(R.drawable.pause)
        }
    }
}