package com.ashiquemusicplayer.mp3player.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.ashiquemusicplayer.mp3player.*
import com.ashiquemusicplayer.mp3player.database.FavouritesDatabase
import com.ashiquemusicplayer.mp3player.database.RecentDatabase
import com.ashiquemusicplayer.mp3player.models.RecentModel
import com.ashiquemusicplayer.mp3player.others.MyListAdapter
import kotlinx.android.synthetic.main.activity_favourites.*

class Favourites : AppCompatActivity() {

    private val favouritesDatabase = FavouritesDatabase(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        // Updating the recent database
        updateList()

        // Playing the desired song of the user
        favouriteList.setOnItemClickListener { parent, view, position, id ->
            Log.d("song info", "$parent    $view    $position    $id")
            val songInfo = favouritesDatabase.searchSong(position+1)
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
        val sl: List<RecentModel> = favouritesDatabase.displaySongs()
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
        val favouritesImage: Array<Any> = Array(sl.size){"null"}
        val myListAdapter = MyListAdapter(this, songName, favouritesImage)
        favouriteList.adapter = myListAdapter
    }
}