package com.rivanov0401.dzv2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PlaylistActivity: AppCompatActivity() {
    private lateinit var dbManager: DatabaseManager
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var tracksRecyclerView: RecyclerView
    private var tracks: MutableList<Pair<Int, String>> = mutableListOf()
    private var isPlaying = false

    private var playlistId: Int = -1
    private var selectedUri: Uri? = null

    companion object {
        private const val REQUEST_ADD_TRACK = 1
        private const val REQUEST_EDIT_TRACK = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        dbManager = DatabaseManager(this)

        playlistId = intent.getIntExtra("PLAYLIST_ID", -1)

        if (playlistId == -1) {
            Toast.makeText(this, "Плейлист не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tracksRecyclerView = findViewById(R.id.tracksRecyclerView)
        trackAdapter = TrackAdapter(tracks) { trackId, trackName -> onTrackClick(trackId, trackName) }
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = trackAdapter

        findViewById<Button>(R.id.addTrackButton).setOnClickListener { showAddTrackDialog() }
        findViewById<Button>(R.id.editTrackButton).setOnClickListener { showEditTrackDialog() }
        findViewById<Button>(R.id.deleteTrackButton).setOnClickListener { showDeleteTrackDialog() }
        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }

        loadTracks()
    }

    private fun loadTracks() {
        tracks.clear()
        tracks.addAll(dbManager.getTracks(playlistId))
        trackAdapter.notifyDataSetChanged()
    }

    private fun showAddTrackDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_track, null)
        val nameButton = dialogView.findViewById<Button>(R.id.add_track_name_input)

        nameButton.setOnClickListener{
            showFileSelector(REQUEST_ADD_TRACK, null)
        }

        AlertDialog.Builder(this)
            .setTitle("Добавить песню")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val trackName = selectedUri.toString()

                if (trackName.isNotBlank()) {
                    dbManager.addTrack(trackName, playlistId)
                    loadTracks()
                    Toast.makeText(this, "Песня добавлена", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Имя не может быть пустым", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditTrackDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_track, null)
        val editButton = dialogView.findViewById<Button>(R.id.edit_track_id_input)
        val editId = dialogView.findViewById<EditText>(R.id.edit_track_id_input)

        editButton.setOnClickListener{
            showFileSelector(REQUEST_EDIT_TRACK, null)
        }

        AlertDialog.Builder(this)
            .setTitle("Редактировать песню")
            .setView(dialogView)
            .setPositiveButton("Изменить") { _, _ ->
                val trackId = editId.text.toString().toIntOrNull()
                val trackName = selectedUri.toString()
                if (trackId != null && trackName.isNotBlank()) {
                    dbManager.deleteTrack(trackId)
                    dbManager.editTrack(trackId, trackName)
                    loadTracks()
                    Toast.makeText(this, "Песня изменена", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Некорректные данные", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showDeleteTrackDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_track, null)
        val editText = dialogView.findViewById<EditText>(R.id.trackIdEditText)

        AlertDialog.Builder(this)
            .setTitle("Удалить песню")
            .setView(dialogView)
            .setPositiveButton("Удалить") { _, _ ->
                val trackId = editText.text.toString().toIntOrNull()
                if (trackId != null) {
                    dbManager.deleteTrack(trackId)
                    loadTracks()
                    Toast.makeText(this, "Песня удалена", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Некорректный ID", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun onTrackClick(trackId: Int, trackName: String) {
        if (isPlaying) {
            pauseAudio() // Пауза
            isPlaying = false
        } else {
            playAudio(trackName)
            isPlaying = true
        }
    }

    private fun showFileSelector(requestCode: Int, trackId: Int?) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/*"
        }

        if(trackId != null) {
            intent.putExtra("track_id", trackId)
        }

        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                selectedUri = uri
            }
        }
    }

    private fun playAudio(uri: String) {
        val intent = Intent(this, AudioService::class.java).apply {
            action = AudioService.ACTION_PLAY
            putExtra(AudioService.EXTRA_AUDIO_URI, uri)
        }
        startService(intent)
    }

    private fun pauseAudio() {
        val intent = Intent(this, AudioService::class.java).apply {
            action = AudioService.ACTION_PAUSE
        }
        startService(intent)
    }

    private fun stopAudio() {
        val intent = Intent(this, AudioService::class.java).apply {
            action = AudioService.ACTION_STOP
        }
        startService(intent)
    }


}