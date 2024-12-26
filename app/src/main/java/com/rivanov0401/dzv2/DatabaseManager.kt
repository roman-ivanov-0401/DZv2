package com.rivanov0401.dzv2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class DatabaseManager(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun addPlaylist(name: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PLAYLIST_NAME, name)
        }
        return db.insert(DatabaseHelper.TABLE_PLAYLIST, null, values)
    }

    fun getPlaylists(): List<Pair<Int, String>> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_PLAYLIST,
            null,
            null,
            null,
            null,
            null,
            null
        )
        val playlists = mutableListOf<Pair<Int, String>>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLAYLIST_ID))
                val name = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLAYLIST_NAME))
                playlists.add(id to name)
            }
        }
        cursor.close()
        return playlists
    }

    fun editPlaylist(playlistId: Int, newName: String) {
        val db = dbHelper.writableDatabase
        val cv = ContentValues()
        cv.put(DatabaseHelper.COLUMN_PLAYLIST_NAME, newName)

        db.update(
            DatabaseHelper.TABLE_PLAYLIST,
            cv,
            "${DatabaseHelper.COLUMN_PLAYLIST_ID} = ?",
            arrayOf(playlistId.toString())
        )
    }

    fun deletePlaylist(playlistId: Int) {
        val db = dbHelper.writableDatabase
        db.delete(
            DatabaseHelper.TABLE_PLAYLIST,
            "${DatabaseHelper.COLUMN_PLAYLIST_ID} = ?",
            arrayOf(playlistId.toString())
        )
    }

    fun addTrack(name: String, playlistId: Int): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TRACK_NAME, name)
            put(DatabaseHelper.COLUMN_TRACK_PLAYLIST_ID, playlistId)
        }
        return db.insert(DatabaseHelper.TABLE_TRACK, null, values)
    }

    fun getTracks(playlistId: Int): List<Pair<Int, String>> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_TRACK,
            null,
            "${DatabaseHelper.COLUMN_TRACK_PLAYLIST_ID} = ?",
            arrayOf(playlistId.toString()),
            null,
            null,
            null
        )
        val tracks = mutableListOf<Pair<Int, String>>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRACK_ID))
                val name = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRACK_NAME))
                tracks.add(id to name)
            }
        }
        cursor.close()
        return tracks
    }

    fun deleteTrack(trackId: Int) {
        val db = dbHelper.writableDatabase
        db.delete(
            DatabaseHelper.TABLE_TRACK,
            "${DatabaseHelper.COLUMN_TRACK_ID} = ?",
            arrayOf(trackId.toString())
        )
    }

    fun editTrack(trackId: Int, newName: String) {
        val db = dbHelper.writableDatabase
        val cv = ContentValues()
        cv.put(DatabaseHelper.COLUMN_TRACK_NAME, newName)

        db.update(
            DatabaseHelper.TABLE_TRACK,
            cv,
            "${DatabaseHelper.COLUMN_TRACK_ID} = ?",
            arrayOf(trackId.toString())
        )
    }
}
