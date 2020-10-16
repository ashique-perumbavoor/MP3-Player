package com.ashiquemusicplayer.mp3player.others

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ashiquemusicplayer.mp3player.R

// Class for adding songs to list view row by row
class MyListAdapter(private val context: Activity, private val name: Array<String>)
    : ArrayAdapter<String>(context, R.layout.songlistingmodel, name) {

    @SuppressLint("SetTextI18n", "ViewHolder", "InflateParams")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.songlistingmodel, null, true)
        val nameText = rowView.findViewById<TextView>(R.id.songName)
        nameText.text = name[position]
        return rowView
    }
}