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

lateinit var mp: MediaPlayer
var songID = 0

@Suppress("DEPRECATION", "SENSELESS_COMPARISON")
class CurrentPlayingActivity : AppCompatActivity() {

    private val databaseHandler = DatabaseHandler(this)
    private val musicObject = MusicObject

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_playing)

        // getting the information's of the song
        val songInfo = intent.getStringArrayExtra("songInfo")
        // setting song name
        song_name.text = songInfo?.get(0)
            ?.replace("%20", " ")
            ?.replace("%5B", " ")
            ?.replace("%5D", " ")
            ?.replace("%2C", " ")
            ?.replace("%26", " ")
            ?.replace("%5B", " ")
            ?.replace("%5D", " ")
        // setting song URI
        var songUri = songInfo?.get(1)
        // getting song ID in Database
        songID = songInfo?.get(2)?.toInt()!!

        // Play and pause song
        playPauseButton.setOnClickListener {
            playBtnClick()
        }

        // Playing the song
        mp = MediaPlayer.create(this, songUri?.toUri())
        mp.isLooping = true
        playPauseButton.setBackgroundResource(R.drawable.pause)
        if (songUri != null) {
            musicObject.playMusic(mp)
        }

        // Next button to play the next song
        nextButton.setOnClickListener {
            mp.pause()
            val nextSongID: Int = songID + 2
            songID++
            val songInfoDB = databaseHandler.searchSong(nextSongID)
            songUri = songInfoDB!![1]
            mp = MediaPlayer.create(this, songUri!!.toUri())
            song_name.text = songInfoDB[0]
                .replace("%20", " ")
                .replace("%5B", " ")
                .replace("%5D", " ")
                .replace("%2C", " ")
                .replace("%26", " ")
                .replace("%5B", " ")
                .replace("%5D", " ")
            musicObject.stopMusic()
            musicObject.playMusic(mp)
            playPauseButton.setBackgroundResource(R.drawable.pause)
        }

        // Previous button to play the previous song
        previousButton.setOnClickListener {
            mp.pause()
            val nextSongID: Int = songID - 1
            songID--
            val songInfoDB = databaseHandler.searchSong(nextSongID)
            songUri = songInfoDB!![1]
            mp = MediaPlayer.create(this, songUri!!.toUri())
            song_name.text = songInfoDB[0]
                .replace("%20", " ")
                .replace("%5B", " ")
                .replace("%5D", " ")
                .replace("%2C", " ")
                .replace("%26", " ")
                .replace("%5B", " ")
                .replace("%5D", " ")
            musicObject.stopMusic()
            musicObject.playMusic(mp)
            playPauseButton.setBackgroundResource(R.drawable.pause)
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

        menu.setOnClickListener {
            val favouritesDatabase = FavouritesDatabase(this)
            Log.d("hello", songID.toString())
            val data = databaseHandler.searchSong(songID)
            data?.get(0)?.let { it1 -> favouritesDatabase.addSong(it1, data[1]) }
        }
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
            musicObject.pauseMusic()
            playPauseButton.setBackgroundResource(R.drawable.play)
        } else {
            musicObject.playMusicAgain()
            playPauseButton.setBackgroundResource(R.drawable.pause)
        }
    }
}