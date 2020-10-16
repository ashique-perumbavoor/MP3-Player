package com.ashiquemusicplayer.mp3player.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.ashiquemusicplayer.mp3player.*
import com.ashiquemusicplayer.mp3player.database.PlaylistDatabase
import com.ashiquemusicplayer.mp3player.database.PlaylistSongDatabase
import com.ashiquemusicplayer.mp3player.models.RecentModel
import com.ashiquemusicplayer.mp3player.others.MyListAdapter
import kotlinx.android.synthetic.main.activity_choose_playlist.*

class ChoosePlaylist : AppCompatActivity() {

    private val playlistDatabase = PlaylistDatabase(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_playlist)

        // Showing playlist to the user
        updateList()

        // Getting song information for operations
        val songInfo = intent.getStringArrayExtra("songInfo")

        // Setting listener for playlist options
        choosePlaylist.setOnItemClickListener { parent, view, position, id ->
            Log.d("playlist info", "$parent    $view    $position    $id")
            val playlistInfo = playlistDatabase.searchPlaylistByID(position + 1)
            if (playlistInfo != null) {
                val playlistSongDatabase = PlaylistSongDatabase(this)
                songInfo?.get(0)?.let {
                    playlistSongDatabase.addSong(playlistInfo[0], it, songInfo[1], songInfo[2])
                }
                Toast.makeText(this, "Song added to the playlist", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Function to update playlist list
    private fun updateList() {
        val sl: ArrayList<RecentModel> = playlistDatabase.displayPlaylist()
        val playlistName = Array(sl.size){"null"}
        for ((index, i) in sl.withIndex()) {
            playlistName[index] = i.name
        }

        // updating the list shown to the user
        val myListAdapter = MyListAdapter(this, playlistName)
        choosePlaylist.adapter = myListAdapter
    }
}