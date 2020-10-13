package com.ashiquemusicplayer.mp3player

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.android.synthetic.main.add_playlist_view.*

class Playlist : AppCompatActivity() {
    lateinit var  playlistName: String
    private val playlistDatabase = PlaylistDatabase(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        // Updating list of playlist
        updateList()

        // Actions for adding playlist
        add.setOnClickListener {
            val inflater = layoutInflater
            val playlistAdder = R.layout.add_playlist_view
            val inflateView = inflater.inflate(playlistAdder, null)
            val name = inflateView.findViewById<EditText>(R.id.playlistName)
            inflateView.findViewById<EditText>(R.id.playlistName)
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Create Playlist")
            alertDialog.setView(inflateView)
            alertDialog.setNegativeButton("Cancel") { _, _ ->}
            alertDialog.setPositiveButton("Create") { _, _ ->
                playlistName = name.text.toString()
                val result = playlistDatabase.searchPlaylist(playlistName)
                if (result == null) {
                    playlistDatabase.addPlaylist(playlistName)
                    updateList()
                } else {
                    Toast.makeText(this, "You cannot make two playlist with same name", Toast.LENGTH_LONG).show()
                }
            }
            alertDialog.create().show()
        }

        playlistList.setOnItemClickListener { parent, view, position, id ->
            Log.d("Playlist info", "$parent  $view  $position  $id")
            val playlistInfo = playlistDatabase.searchPlaylistByID(position + 1)
            startActivity(Intent(this, PlaylistSongViewer::class.java).putExtra("playlistName", playlistInfo?.get(0).toString()))
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
        playlistList.adapter = myListAdapter
    }
}