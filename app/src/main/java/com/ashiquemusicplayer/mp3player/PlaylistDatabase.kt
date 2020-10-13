package com.ashiquemusicplayer.mp3player

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class PlaylistDatabase(context: Context): SQLiteOpenHelper(context, "playlist", null, 1) {
    companion object {
        const val SONG_ID = "songID"
        const val PLAYLIST_NAME = "songName"
        const val DATABASE_NAME = "playlistDatabase"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $DATABASE_NAME ( $SONG_ID INTEGER PRIMARY KEY, $PLAYLIST_NAME TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    // adding the song details to the database
    fun addPlaylist(name: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(PLAYLIST_NAME, name)
        db.insert(DATABASE_NAME, null, contentValues)
    }

    // adding song details from database to array for showing to the user
    @SuppressLint("Recycle")
    fun displayPlaylist(): ArrayList<RecentModel> {
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
                val playListName = cursor.getString(cursor.getColumnIndex(PLAYLIST_NAME))
                val sl= RecentModel(name = playListName)
                playlistList.add(sl)
            } while (cursor.moveToNext())
        }
        return playlistList
    }

    // searching for the details of the song user requested
    @SuppressLint("CommitPrefEdits", "Recycle")
    fun searchPlaylist(name: String): Array<String>? {
        var cursor: Cursor? = null
        val selectQuery = "SELECT * FROM $DATABASE_NAME"
        val db = this.readableDatabase
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
        }
        var numberOfSongs = 0
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    numberOfSongs++
                } while (cursor.moveToNext())
            }
        }
        var songIDinDB: Int
        var songName: String
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    songIDinDB = cursor.getInt(cursor.getColumnIndex(SONG_ID))
                    songName = cursor.getString(cursor.getColumnIndex(PLAYLIST_NAME))
                    if (songName == name) {
                        return arrayOf(songName, songIDinDB.toString())
                    }
                } while (cursor.moveToNext())
            }
        }
        return null
    }

    // searching for the details of the song user requested
    @SuppressLint("CommitPrefEdits", "Recycle")
    fun searchPlaylistByID(ID: Int): Array<String>? {
        var cursor: Cursor? = null
        val selectQuery = "SELECT * FROM $DATABASE_NAME"
        val db = this.readableDatabase
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
        }
        var playlistIDinDB: Int
        var playlistName: String
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    playlistIDinDB = cursor.getInt(cursor.getColumnIndex(SONG_ID))
                    playlistName = cursor.getString(cursor.getColumnIndex(PLAYLIST_NAME))
                    if (playlistIDinDB == ID) {
                        return arrayOf(playlistName, playlistIDinDB.toString())
                    }
                } while (cursor.moveToNext())
            }
        }
        return null
    }
}