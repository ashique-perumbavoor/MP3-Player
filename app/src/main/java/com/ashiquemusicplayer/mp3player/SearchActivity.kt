package com.ashiquemusicplayer.mp3player

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val databaseHandler = DatabaseHandler(this)
        val sl: List<RecentModel> = databaseHandler.getSongName()
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
        val myListAdapter = MyListAdapter(this, songName)
        searchList.adapter = myListAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                myListAdapter.filter.filter(newText)
                return false
            }
        }
        )

        // Playing the desired song of the user
        searchList.setOnItemClickListener { parent, view, position, id ->
            Log.d("song info", "$parent    $view    $position    $id")
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
    }
}