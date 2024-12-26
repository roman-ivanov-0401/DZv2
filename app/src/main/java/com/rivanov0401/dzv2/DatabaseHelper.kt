package com.rivanov0401.dzv2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "audioPlayer.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_PLAYLIST = "Playlist"
        const val COLUMN_PLAYLIST_ID = "id"
        const val COLUMN_PLAYLIST_NAME = "name"

        const val TABLE_TRACK = "Track"
        const val COLUMN_TRACK_ID = "id"
        const val COLUMN_TRACK_NAME = "name"
        const val COLUMN_TRACK_PLAYLIST_ID = "playlist_id"

        private const val CREATE_PLAYLIST_TABLE = """
            CREATE TABLE $TABLE_PLAYLIST (
                $COLUMN_PLAYLIST_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PLAYLIST_NAME TEXT NOT NULL
            );
        """

        private const val CREATE_TRACK_TABLE = """
            CREATE TABLE $TABLE_TRACK (
                $COLUMN_TRACK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TRACK_NAME TEXT NOT NULL,
                $COLUMN_TRACK_PLAYLIST_ID INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_TRACK_PLAYLIST_ID) REFERENCES $TABLE_PLAYLIST($COLUMN_PLAYLIST_ID)
                ON DELETE CASCADE
            );
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_PLAYLIST_TABLE)
        db?.execSQL(CREATE_TRACK_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TRACK")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYLIST")
        onCreate(db)
    }
}
