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

    val TAG = "PlayingActivityHello"
    private lateinit var mp: MediaPlayer
    private var totalTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_playing)

        playPauseButton.setOnClickListener {
            playBtnClick()
        }

        mp = MediaPlayer.create(this, R.raw.closer)
        mp.isLooping = true
        totalTime = mp.duration

        var flag = 0
        progressBar.max = totalTime
        progressBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mp.seekTo(progress)
                    }

                    if (flag == 0) {
                        if (savedInstanceState != null) {
                            var remainigTime = savedInstanceState.getInt("currentPlaying")
                            Log.d("remainigTimetwo", remainigTime.toString())
                            mp.seekTo(remainigTime)
                            mp.start()
                            playPauseButton.setBackgroundResource(R.drawable.pause)
                            flag = 1
                        }
                        flag = 1
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

//        var current = mp.currentPosition
        outState.putInt("currentPlaying", mp.currentPosition)

    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG,"onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }

    override fun onRestart() {
        Log.d(TAG, "onRestart")
        super.onRestart()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
        if (mp.isPlaying) {
            mp.pause()
        }
    }

}