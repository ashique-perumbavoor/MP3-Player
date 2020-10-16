package com.ashiquemusicplayer.mp3player.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.ashiquemusicplayer.mp3player.*
import com.ashiquemusicplayer.mp3player.database.DatabaseHandler
import com.ashiquemusicplayer.mp3player.database.RecentDatabase
import com.ashiquemusicplayer.mp3player.models.Model
import com.ashiquemusicplayer.mp3player.others.MyListAdapter
import kotlinx.android.synthetic.main.activity_main.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    // list of permission we need
    @RequiresApi(Build.VERSION_CODES.P)
    private val permissions = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WAKE_LOCK,
        android.Manifest.permission.WRITE_SETTINGS
    )
    // object of DatabaseHandler class
    private val databaseHandler = DatabaseHandler(this)
    private var flag = 0
    private lateinit var songName: Array<String>
    private lateinit var temporaryArray: Array<String>

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // deleting current database if exist
        deleteDatabase(databaseHandler.databaseName)
        // scanning for songs in the device and storing details in a database
        getSongs()
        // showing the songs that scanned to the user
        updateList()

        // Playing the desired song of the user
        songView.setOnItemClickListener { parent, view, position, id ->
            Log.d("song info", "$parent    $view    $position    $id")
            flag++
            val songInfo = databaseHandler.searchSong(position + 1)
            if (songInfo != null) {
                startActivity(
                    Intent(this, CurrentPlayingActivity::class.java).putExtra(
                        "songInfo",
                        songInfo
                    )
                )
                val recentDatabase = RecentDatabase(this)
                recentDatabase.addSong(songInfo[0], songInfo[1].toUri())
            } else {
                Toast.makeText(this, "Error in accessing the media file", Toast.LENGTH_LONG).show()
            }
        }

        // Action listener for showing the information's of the application
        info_button.setOnClickListener {
            startActivity(Intent(this, Info::class.java))
        }

        // Action listener for showing recent activity
        recent.setOnClickListener {
            startActivity(Intent(this, RecentActivity::class.java))
        }

        // Action listener of favourite musics
        favourite.setOnClickListener {
            startActivity(Intent(this, Favourites::class.java))
        }

        // Action listener of playlist
        playListButton.setOnClickListener {
            startActivity(Intent(this, Playlist::class.java))
        }

        // Button listener to go to current playing activity
        currentPlaying.setOnClickListener {
            if (flag > 0) {
                startActivity(Intent(this, MusicPlayingActivity::class.java))
            } else {
                Toast.makeText(this, "Play any music to use this feature", Toast.LENGTH_LONG).show()
            }
        }

        // Click listener for searching the song
        search.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        shuffle.setOnClickListener {
            temporaryArray = songName
            var count = 0
            for ((index, i) in temporaryArray.withIndex()) {
                if (index % 2 != 0) {
                    songName[count] = temporaryArray[index]
                    count++
                }
            }
            for ((index, i) in temporaryArray.withIndex()) {
                if (index % 2 == 0) {
                    songName[count] = temporaryArray[index]
                    count++
                }
            }
            val myListAdapter = MyListAdapter(this, songName)
            songView.adapter = myListAdapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStart() {
        super.onStart()
        // checking for permissions and if permission not granted asking for permission
        if (hasNoPermission()) {
            requestPermission()
        }
    }

    // function to check permission
    private fun hasNoPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WAKE_LOCK
                ) != PackageManager.PERMISSION_GRANTED
    }

    // requesting for permission
    @RequiresApi(Build.VERSION_CODES.P)
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 0)
    }

    // function to scan and store song details in a database
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getSongs(){
        val contentResolver = contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver.query(uri, null, null, null , null)
        if (songCursor != null && songCursor.moveToFirst()) {
            do {
                databaseHandler.addSong(
                    songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                    songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                    songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)).toUri()
                )
                songCursor.getBlob(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ARTIST))
            } while (songCursor.moveToNext())
            songCursor.close()
        }
    }

    // function to show the scanned songs to the user
    private fun updateList() {
        val sl: List<Model> = databaseHandler.displaySongs()
        songName = Array(sl.size){"null"}
        val songPath = Array(sl.size){"null"}
        val songURI = Array(sl.size){"null"}
        for ((index, i) in sl.withIndex()) {
            songName[index] = i.name
                .replace("%20", " ")
                .replace("%5B", " ")
                .replace("%5D", " ")
                .replace("%2C", " ")
                .replace("%26", " ")
                .replace("%5B", " ")
                .replace("%5D", " ")
            songPath[index] = i.path
            songURI[index] = i.songURI
        }

        // updating the list shown to the user
        val myListAdapter = MyListAdapter(this, songName)
        songView.adapter = myListAdapter
    }
}