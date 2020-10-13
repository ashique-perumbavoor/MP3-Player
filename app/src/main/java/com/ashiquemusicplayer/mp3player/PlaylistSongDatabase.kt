package com.ashiquemusicplayer.mp3player

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class PlaylistSongDatabase(context: Context): SQLiteOpenHelper(context, "Playlist Songs", null, 1) {
    companion object {
        const val DATABASE_NAME = "playlistSongs"
        const val SONG_ID = "songID"
        const val SONG_NAME = "songName"
        const val SONG_URI = "songUri"
        const val PLAYLIST_NAME = "playlistName"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $DATABASE_NAME ($SONG_NAME TEXT, $SONG_URI BLOB,$PLAYLIST_NAME TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun addSong(listName: String, songName: String, songUri: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SONG_NAME, songName)
        contentValues.put(SONG_URI, songUri)
        contentValues.put(PLAYLIST_NAME, listName)
        db.insert(DATABASE_NAME, null, contentValues)
    }

    // searching for the details of the song user requested
    @SuppressLint("CommitPrefEdits", "Recycle")
    fun searchPlaylist(name: String, id: Int): Array<String>? {
        var cursor: Cursor? = null
        val selectQuery = "SELECT * FROM $DATABASE_NAME"
        val db = this.readableDatabase
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
        }
        var songIDinDB: Int
        var songName: String
        var songUri: String
        var playlistName: String
        var count = 0
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    songIDinDB = cursor.getInt(cursor.getColumnIndex(SONG_ID))
                    songName = cursor.getString(cursor.getColumnIndex(SONG_NAME))
                    songUri = cursor.getString(cursor.getColumnIndex(SONG_URI))
                    playlistName = cursor.getString(cursor.getColumnIndex(PLAYLIST_NAME))
                    if (playlistName == name) {
                        if (count == id) {
                            return arrayOf(songName, songUri, songIDinDB.toString())
                        }
                        count++
                    }
                } while (cursor.moveToNext())
            }
        }
        return null
    }

    // adding song details from database to array for showing to the user
    @SuppressLint("Recycle")
    fun displayPlaylist(playlistName: String?): ArrayList<RecentModel> {
        val playlistList:ArrayList<RecentModel> = ArrayList()
        val selectQuery = "SELECT * FROM $DATABASE_NAME"
        val db = this.readableDatabase
        val cursor: Cursor
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                val playName = cursor.getString(cursor.getColumnIndex(PLAYLIST_NAME))
                val songName = cursor.getString(cursor.getColumnIndex(SONG_NAME))
                if (playName == playlistName) {
                    val sl= RecentModel(name = songName)
                    playlistList.add(sl)
                }
            } while (cursor.moveToNext())
        }
        return playlistList
    }
}