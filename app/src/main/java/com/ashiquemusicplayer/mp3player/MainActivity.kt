package com.ashiquemusicplayer.mp3player

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    // list of permission we need
    private val permissions = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    // object of DatabaseHandler class
    private val databaseHandler = DatabaseHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // deleting current database if exist
        deleteDatabase(databaseHandler.databaseName)
        // scanning for songs in the device and storing details in a database
        getSongs()
        // showing the songs that scanned to the user
        updateList()
    }

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
        ) != PackageManager.PERMISSION_GRANTED
    }

    // requesting for permission
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 0)
    }

    // function to scan and store song details in a database
    private fun getSongs(){
        val fileList: ArrayList<HashMap<String, String>> = ArrayList()
        val rootFolder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
            ""
        )
        val files = rootFolder.listFiles()
        if (files == null) {
            Toast.makeText(this, "Couldn't find any song", Toast.LENGTH_LONG).show()
        } else {
            for (file in files) {
                if (file.name.endsWith(".mp3") ||
                    file.name.endsWith(".MP3") ||
                    file.name.endsWith(".M4A") ||
                    file.name.endsWith(".m4a") ||
                    file.name.endsWith(".AAC") ||
                    file.name.endsWith(".aac")) {
                    val song: HashMap<String, String> = HashMap()
                    song["file_path"] = file.absolutePath
                    song["file_name"] = file.name
                    fileList.add(song)
                    databaseHandler.addSong(file.name, file.path)
                }
            }
        }
    }

    // function to show the scanned songs to the user
    private fun updateList() {
        val sl: List<Model> = databaseHandler.displaySongs()
        val songName = Array(sl.size){"null"}
        for ((index, i) in sl.withIndex()) {
            songName[index] = i.name
            Log.d("hello", songName[index])
        }
        // updating the list shown to the user
        val myListAdapter = MyListAdapter(this, songName)
        songView.adapter = myListAdapter
    }
}