package com.ashiquemusicplayer.mp3player

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_current_playing.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

lateinit var mp: MediaPlayer

@Suppress("DEPRECATION", "SENSELESS_COMPARISON")
class CurrentPlayingActivity : AppCompatActivity() {

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private val databaseHandler = DatabaseHandler(this)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_playing)

        // getting the information's of the song
        val songInfo = intent.getStringArrayExtra("songInfo")
        // setting song name
        song_name.text = songInfo?.get(0)
        // setting song URI
        var songUri = songInfo?.get(1)
        // getting song ID in Database
        var songID = songInfo?.get(2)?.toInt()

        // Play and pause song
        playPauseButton.setOnClickListener {
            playBtnClick()
        }

        // Playing the song
        mp = MediaPlayer.create(this, songUri?.toUri())
        mp.isLooping = true
        playPauseButton.setBackgroundResource(R.drawable.pause)
        playSong()

        nextButton.setOnClickListener {
            mp.pause()
            val nextSongID: Int = songID!!.toInt() + 2
            songID++
            val songInfoDB = databaseHandler.searchSong(nextSongID)
            songUri = songInfoDB!![1]
            mp = MediaPlayer.create(this, songUri!!.toUri())
            song_name.text = songInfoDB[0]
            mp.start()
        }

        previousButton.setOnClickListener {
            mp.pause()
            val nextSongID: Int = songID!!.toInt() - 1
            songID--
            val songInfoDB = databaseHandler.searchSong(nextSongID)
            songUri = songInfoDB!![1]
            mp = MediaPlayer.create(this, songUri!!.toUri())
            song_name.text = songInfoDB[0]
            mp.start()
        }

        // Progressbar creating
        progressBar.max = mp.duration
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
            val remainingTime = createTimeLabel(mp.duration - currentPosition)
            remainingTimeLabel.text = "-$remainingTime"
        }
    }

    // Making the time label of current position of the song
    fun createTimeLabel(time: Int): String {
        val timeLabel: String
        val min = time / 1000 / 60
        val sec = time / 1000 % 60
        timeLabel = if (sec < 10) {
            "$min:0$sec"
        } else {
            "$min:$sec"
        }
        return timeLabel
    }

    // playing and pausing song and changing the button image
    private fun playBtnClick() {
        if (mp.isPlaying) {
            mp.pause()
            playPauseButton.setBackgroundResource(R.drawable.play)
        } else {
            playSong()
            playPauseButton.setBackgroundResource(R.drawable.pause)
        }
    }

    private fun playSong() {
        executorService.execute {
            mp.start()
        }
    }
}