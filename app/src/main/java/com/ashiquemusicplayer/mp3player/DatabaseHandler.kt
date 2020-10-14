package com.ashiquemusicplayer.mp3player

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.net.URI
import kotlin.collections.ArrayList

class DatabaseHandler(context: Context): SQLiteOpenHelper(context, "MAIN_SONG", null, 1) {
    companion object {
        const val SONG_ID = "songID"
        const val SONG_NAME = "songName"
        const val SONG_PATH = "songPath"
        const val SONG_URI = "song"
        const val DATABASE_NAME = "songDatabase"
    }

    // creating main database
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $DATABASE_NAME ( $SONG_ID INTEGER PRIMARY KEY,$SONG_NAME TEXT, $SONG_PATH TEXT, $SONG_URI BLOB)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    // adding the song details to the database
    fun addSong(name: String, path: String, absoluteFile: URI) {
        val db =this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SONG_NAME, name)
        contentValues.put(SONG_PATH, path)
        contentValues.put(SONG_URI, absoluteFile.toString())
        db.insert(DATABASE_NAME, null, contentValues)
    }

    // adding song details from database to array for showing to the user
    @SuppressLint("Recycle")
    fun displaySongs(): ArrayList<Model> {
        val songList:ArrayList<Model> = ArrayList()
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
                val songName = cursor.getString(cursor.getColumnIndex(SONG_NAME))
                val path = cursor.getString(cursor.getColumnIndex(SONG_PATH))
                val songURI = cursor.getString(cursor.getColumnIndex(SONG_URI))
                val songID = cursor.getString(cursor.getColumnIndex(SONG_ID))
                val sl= Model(name = songName, path = path, songURI = songURI, songID = songID)
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
                    songIDinDB = cursor.getInt(cursor.getColumnIndex(SONG_ID))
                    songName = cursor.getString(cursor.getColumnIndex(SONG_NAME))
                    songURI = cursor.getString(cursor.getColumnIndex(SONG_URI))
                    if (songIDinDB == songPosition) {
                        return arrayOf(songName, songURI, songIDinDB.toString())
                    }
                } while (cursor.moveToNext())
            }
        }
        return null
    }
}