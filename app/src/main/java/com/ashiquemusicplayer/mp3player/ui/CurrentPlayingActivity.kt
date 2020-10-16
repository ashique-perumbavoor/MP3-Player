package com.ashiquemusicplayer.mp3player.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.ashiquemusicplayer.mp3player.*
import com.ashiquemusicplayer.mp3player.database.DatabaseHandler
import com.ashiquemusicplayer.mp3player.database.FavouritesDatabase
import com.ashiquemusicplayer.mp3player.database.RecentDatabase
import com.ashiquemusicplayer.mp3player.notification.NotificationService
import com.ashiquemusicplayer.mp3player.objects.MusicObject
import kotlinx.android.synthetic.main.activity_current_playing.*

@Suppress("DEPRECATION")
class CurrentPlayingActivity : AppCompatActivity() {

    private val databaseHandler = DatabaseHandler(this)
    private val musicObject = MusicObject
    lateinit var mp: MediaPlayer
    private var songID = 0
    private var songUri = ""
    private var songName = ""

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_playing)

        // getting the information's of the song
        val songInfo = intent.getStringArrayExtra("songInfo")
        // setting song name
        songName = songInfo?.get(0).toString()
        song_name.text = songName
            .replace("%20", " ")
            .replace("%5B", " ")
            .replace("%5D", " ")
            .replace("%2C", " ")
            .replace("%26", " ")
            .replace("%5B", " ")
            .replace("%5D", " ")
        // setting song Uri
        songUri = songInfo?.get(1).toString()
        // getting song ID in Database
        songID = songInfo?.get(2)?.toInt()!!
        // Play and pause song
        playPauseButton.setOnClickListener {
            playBtnClick()
        }

        // Playing the song
        mp = MediaPlayer.create(this, songUri.toUri())
        mp.isLooping = true
        playPauseButton.setBackgroundResource(R.drawable.pause)
        MusicObject.playMusic(mp, songName, songID, songUri.toUri())

        // Starting Notification
        startService(Intent(this, NotificationService::class.java).putExtra("songName", songName))

        // Next button to play the next song
        nextButton.setOnClickListener {
            mp.pause()
            val nextSongID: Int = songID + 1
            songID++
            val songInfoDB = databaseHandler.searchSong(nextSongID)
            songUri = songInfoDB!![1]
            mp = MediaPlayer.create(this, songUri.toUri())
            songName = songInfoDB[0]
            song_name.text = songInfoDB[0]
                .replace("%20", " ")
                .replace("%5B", " ")
                .replace("%5D", " ")
                .replace("%2C", " ")
                .replace("%26", " ")
                .replace("%5B", " ")
                .replace("%5D", " ")
            MusicObject.stopMusic()
            MusicObject.playMusic(mp, songName, songID, songUri.toUri())
            startService(Intent(this, NotificationService::class.java).putExtra("songName", songName))
            playPauseButton.setBackgroundResource(R.drawable.pause)
            val recentDatabase = RecentDatabase(this)
            recentDatabase.addSong(songInfo[0], songInfo[1].toUri())
        }

        // fast forwarding 5 seconds on long click
        nextButton.setOnLongClickListener {
            val time = mp.currentPosition + 5000
            mp.seekTo(time)
            true
        }

        // Previous button to play the previous song
        previousButton.setOnClickListener {
            mp.pause()
            val nextSongID: Int = songID - 1
            songID--
            val songInfoDB = databaseHandler.searchSong(nextSongID)
            songUri = songInfoDB!![1]
            mp = MediaPlayer.create(this, songUri.toUri())
            songName = songInfoDB[0]
            song_name.text = songInfoDB[0]
                .replace("%20", " ")
                .replace("%5B", " ")
                .replace("%5D", " ")
                .replace("%2C", " ")
                .replace("%26", " ")
                .replace("%5B", " ")
                .replace("%5D", " ")
            MusicObject.stopMusic()
            MusicObject.playMusic(mp, songName, songID, songUri.toUri())
            startService(Intent(this, NotificationService::class.java).putExtra("songName", songName))
            playPauseButton.setBackgroundResource(R.drawable.pause)
            val recentDatabase = RecentDatabase(this)
            recentDatabase.addSong(songInfo[0], songInfo[1].toUri())
        }

        // delaying 5 seconds on long click
        previousButton.setOnLongClickListener {
            val time = mp.currentPosition - 5000
            mp.seekTo(time)
            true
        }

        // Progressbar creating
        progressBar.max = mp.duration
        progressBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
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
            while (true) {
                try {
                    val msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1)
                } catch (e: InterruptedException) { }
            }
        }.start()

        // Menu for actions that can be performed with the music
        menu.setOnClickListener {
            val popupMenu = PopupMenu(this, menu)
            popupMenu.menuInflater.inflate(R.menu.current_playing_popup, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item!!.itemId) {
                    R.id.addToPlaylist -> toPlaylist()
                    R.id.addToFavourites -> toFavourites()
                    R.id.setAsRingtone -> setAsRingtone()
                }
                true
            }
            popupMenu.show()
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
            MusicObject.pauseMusic()
            stopService(Intent(this, NotificationService::class.java))
            playPauseButton.setBackgroundResource(R.drawable.play)
        } else {
            MusicObject.playMusicAgain()
            startService(Intent(this, NotificationService::class.java).putExtra("songName", songName))
            playPauseButton.setBackgroundResource(R.drawable.pause)
        }
    }

    private fun toFavourites() {
        val favouritesDatabase = FavouritesDatabase(this)
        val data = databaseHandler.searchSong(songID)
        data?.get(0)?.let { it1 -> favouritesDatabase.addSong(it1, data[1]) }
        Toast.makeText(this, "Added to favourites", Toast.LENGTH_LONG).show()
    }

    private fun toPlaylist() {
        val songInfo = arrayOf(song_name.text.toString(), songUri, songID.toString())
        startActivity(Intent(this, ChoosePlaylist::class.java).putExtra("songInfo", songInfo))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAsRingtone() {
        val settingsCanWrite: Boolean = Settings.System.canWrite(applicationContext)
        if (!settingsCanWrite) {
            Toast.makeText(this, "Give permission in order to set the music as ringtone", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS))
        } else {
            RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, songUri.toUri())
            Toast.makeText(this, "Ringtone changed.", Toast.LENGTH_LONG).show()
        }
    }
}