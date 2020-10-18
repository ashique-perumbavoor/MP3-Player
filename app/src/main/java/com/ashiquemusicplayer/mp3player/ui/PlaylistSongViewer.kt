package com.ashiquemusicplayer.mp3player.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ashiquemusicplayer.mp3player.others.MyListAdapter
import com.ashiquemusicplayer.mp3player.R
import com.ashiquemusicplayer.mp3player.models.RecentModel
import com.ashiquemusicplayer.mp3player.database.PlaylistSongDatabase
import kotlinx.android.synthetic.main.activity_playlist_song_viewer.*

class PlaylistSongViewer : AppCompatActivity() {
    private val playlistSongDatabase = PlaylistSongDatabase(this)
    private lateinit var playlistName:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_song_viewer)

        playlistName = intent.getStringExtra("playlistName").toString()
        playListName.text = playlistName

        // Showing the songs in the playlist to the user
        updateList()

        // Playing the song requested by the user
        songList.setOnItemClickListener { parent, view, position, id ->
            Log.d("songInfo", "$parent  $view  $position  $id")
            val playlistInfo = playlistSongDatabase.searchPlaylist(playlistName, position)
            startActivity(Intent(this, CurrentPlayingActivity::class.java).putExtra("songInfo", playlistInfo))
        }

    }

    // function to show the scanned songs to the user
    private fun updateList() {
        val sl: ArrayList<RecentModel> = playlistSongDatabase.displayPlaylist(playlistName)
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
        val songImage: Array<Any> = Array(sl.size){"null"}
        val myListAdapter = MyListAdapter(this, songName, songImage)
        songList.adapter = myListAdapter
    }
}