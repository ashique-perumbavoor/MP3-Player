package com.ashiquemusicplayer.mp3player

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context): SQLiteOpenHelper(context, "MAIN_SONG", null, 1) {
    companion object {
        const val SONG_NAME = "song_Name"
        const val SONG_PATH = "song_Path"
        const val DATABASE_NAME = "song_Database"
    }

    // creating database
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $DATABASE_NAME ($SONG_NAME TEXT, $SONG_PATH TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    // adding the song details to the database
    fun addSong(name: String, path: String) {
        val db =this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SONG_NAME, name)
        contentValues.put(SONG_PATH, path)
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
                val sl= Model(name = songName, path = path)
                songList.add(sl)
            } while (cursor.moveToNext())
        }
        return songList
    }
}