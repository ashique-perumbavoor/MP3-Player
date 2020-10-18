package com.ashiquemusicplayer.mp3player.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import com.ashiquemusicplayer.mp3player.models.ImageModel
import com.ashiquemusicplayer.mp3player.models.ModelWithImage
import com.ashiquemusicplayer.mp3player.models.RecentModel
import kotlin.collections.ArrayList

class DatabaseHandler(context: Context): SQLiteOpenHelper(context, "MAIN_SONG", null, 1) {
    companion object {
        const val SONG_ID = "songID"
        const val SONG_NAME = "songName"
        const val SONG_PATH = "songPath"
        const val SONG_URI = "song"
        const val DATABASE_NAME = "songDatabase"
        const val SONG_IMAGE = "songImage"
    }

    // creating main database
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $DATABASE_NAME ( $SONG_ID INTEGER PRIMARY KEY,$SONG_NAME TEXT, $SONG_PATH TEXT, $SONG_URI BLOB, $SONG_IMAGE BLOB)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    // adding the song details to the database
    fun addSong(name: String, path: String, absoluteFile: Uri, image: ByteArray) {
        val db =this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SONG_NAME, name)
        contentValues.put(SONG_PATH, path)
        contentValues.put(SONG_URI, absoluteFile.toString())
        contentValues.put(SONG_IMAGE, image)
        db.insert(DATABASE_NAME, null, contentValues)
    }

    fun addSongWithNoImage(name: String, path: String, absoluteFile: Uri) {
        val db =this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SONG_NAME, name)
        contentValues.put(SONG_PATH, path)
        contentValues.put(SONG_URI, absoluteFile.toString())
        db.insert(DATABASE_NAME, null, contentValues)
    }

    // adding song details from database to array for showing to the user
    @SuppressLint("Recycle")
    fun displaySongs(): ArrayList<ModelWithImage> {
        val songList:ArrayList<ModelWithImage> = ArrayList()
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
                if (cursor.getBlob(cursor.getColumnIndex(SONG_IMAGE)) != null) {
                    val songName = cursor.getString(cursor.getColumnIndex(SONG_NAME))
                    val path = cursor.getString(cursor.getColumnIndex(SONG_PATH))
                    val songURI = cursor.getString(cursor.getColumnIndex(SONG_URI))
                    val songID = cursor.getString(cursor.getColumnIndex(SONG_ID))
                    val image = cursor.getBlob(cursor.getColumnIndex(SONG_IMAGE))
                    val sl= ModelWithImage(name = songName, path = path, songURI = songURI, songID = songID, image = image)
                    songList.add(sl)
                } else {
                    val songName = cursor.getString(cursor.getColumnIndex(SONG_NAME))
                    val path = cursor.getString(cursor.getColumnIndex(SONG_PATH))
                    val songURI = cursor.getString(cursor.getColumnIndex(SONG_URI))
                    val songID = cursor.getString(cursor.getColumnIndex(SONG_ID))
                    val sl= ModelWithImage(name = songName, path = path, songURI = songURI, songID = songID, image = null)
                    songList.add(sl)
                }
            } while (cursor.moveToNext())
        }
        return songList
    }

    // adding song details from database to array for showing to the user
    @SuppressLint("Recycle")
    fun displaySongsWithImage(): ArrayList<ModelWithImage> {
        val songList:ArrayList<ModelWithImage> = ArrayList()
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
                val image = cursor.getBlob(cursor.getColumnIndex(SONG_IMAGE))
                val sl= ModelWithImage(name = songName, path = path, songURI = songURI, songID = songID, image = image)
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

    @SuppressLint("Recycle")
    fun getSongName(): ArrayList<RecentModel> {
        val songList:ArrayList<RecentModel> = ArrayList()
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
                val sl= RecentModel(name = songName)
                songList.add(sl)
            } while (cursor.moveToNext())
        }
        return songList
    }

    fun getImage(name: String): ByteArray? {
        val songList:ArrayList<ImageModel> = ArrayList()
        val selectQuery = "SELECT * FROM $DATABASE_NAME"
        val db = this.readableDatabase
        val cursor: Cursor
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return null
        }
        if (cursor.moveToFirst()) {
            do {
                val songImage = cursor.getBlob(cursor.getColumnIndex(SONG_IMAGE))
                val songName = cursor.getString(cursor.getColumnIndex(SONG_NAME))
                if (songName.toString() == name) {
                    if (songImage != null) {
                        val sl= ImageModel(image = songImage)
                        songList.add(sl)
                        return songImage
                    }
                }
            } while (cursor.moveToNext())
        }
        return null
    }
}