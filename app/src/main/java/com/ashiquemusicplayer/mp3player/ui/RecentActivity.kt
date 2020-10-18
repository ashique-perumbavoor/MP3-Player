package com.ashiquemusicplayer.mp3player.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.ashiquemusicplayer.mp3player.others.MyListAdapter
import com.ashiquemusicplayer.mp3player.R
import com.ashiquemusicplayer.mp3player.models.RecentModel
import com.ashiquemusicplayer.mp3player.database.RecentDatabase
import kotlinx.android.synthetic.main.activity_recent.*

class RecentActivity : AppCompatActivity() {

    private val recentDatabase = RecentDatabase(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent)

        // Updating the recently played music
        updateList()

        // Playing the desired song of the user
        recentList.setOnItemClickListener { parent, view, position, id ->
            Log.d("song info", "$parent    $view    $position    $id")
            val songInfo = recentDatabase.searchSong(position+1)
            if (songInfo != null) {
                startActivity(Intent(this, CurrentPlayingActivity::class.java).putExtra("songInfo",songInfo))
                val recentDatabase = RecentDatabase(this)
                recentDatabase.addSong(songInfo[0], songInfo[1].toUri())
            } else {
                Toast.makeText(this, "Error in accessing the media file", Toast.LENGTH_LONG).show()
            }
        }
    }

    // function to show the scanned songs to the user
    private fun updateList() {
        val sl: List<RecentModel> = recentDatabase.displaySongs()
        val songName = Array(sl.size){"null"}
        for ((index, i) in sl.withIndex()) {
            songName[index] = i.name
                .replace("%20", " ")
                .replace("%5B", " ")
                .replace("%5D", " ")
                .replace("%2C", " ")
                .replace("%26", " ")
                .replace("%5B", " ")
                .replace("%5D", " ")
        }
        // updating the list shown to the user
        val songImage:Array<Any> = Array(sl.size){"null"}
        val myListAdapter = MyListAdapter(this, songName, songImage)
        recentList.adapter = myListAdapter
    }
}