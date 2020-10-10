package com.ashiquemusicplayer.mp3player

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class RecentDatabase (context: Context):SQLiteOpenHelper(context, "recent", null, 1) {
    companion object {
        const val SONG_ID = "songID"
        const val SONG_NAME = "songName"
        const val SONG_PATH = "songPath"
        const val SONG_URI = "song"
        const val DATABASE_NAME = "recentDatabase"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $DATABASE_NAME ( $SONG_ID INTEGER PRIMARY KEY, $SONG_NAME TEXT, $SONG_PATH TEXT, $SONG_URI BLOB)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    // adding the song details to the database
    fun addSong(name: String, absoluteFile: Uri) {
        val db =this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DatabaseHandler.SONG_NAME, name)
        contentValues.put(DatabaseHandler.SONG_URI, absoluteFile.toString())
        db.insert(DatabaseHandler.DATABASE_NAME, null, contentValues)
    }

    // adding song details from database to array for showing to the user
    @SuppressLint("Recycle")
    fun displaySongs(): ArrayList<RecentModel> {
        val songList:ArrayList<RecentModel> = ArrayList()
        val selectQuery = "SELECT * FROM ${DatabaseHandler.DATABASE_NAME}"
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
                val songName = cursor.getString(cursor.getColumnIndex(DatabaseHandler.SONG_NAME))
                val sl= RecentModel(name = songName)
                songList.add(sl)
            } while (cursor.moveToNext())
        }
        return songList
    }

    // searching for the details of the song user requested
    @SuppressLint("CommitPrefEdits", "Recycle")
    fun searchSong(songID: Int): Array<String>? {
        var songPosition = songID
        var cursor: Cursor? = null
        val selectQuery = "SELECT * FROM ${DatabaseHandler.DATABASE_NAME}"
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
        if (songPosition > numberOfSongs) {
            songPosition = 1
        } else if (songPosition < 1) {
            songPosition = numberOfSongs
        }
        var songIDinDB: Int
        var songName: String
        var songURI: String
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    songIDinDB = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.SONG_ID))
                    songName = cursor.getString(cursor.getColumnIndex(DatabaseHandler.SONG_NAME))
                    songURI = cursor.getString(cursor.getColumnIndex(DatabaseHandler.SONG_URI))
                    if (songIDinDB == songPosition) {
                        return arrayOf(songName, songURI, songIDinDB.toString())
                    }
                } while (cursor.moveToNext())
            }
        }
        return null
    }
}