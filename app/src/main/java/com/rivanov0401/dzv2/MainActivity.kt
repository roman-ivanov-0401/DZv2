package com.rivanov0401.dzv2

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.Manifest

class MainActivity : AppCompatActivity() {
    private lateinit var dbManager: DatabaseManager
    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var playlistsRecyclerView: RecyclerView

    private var playlists: MutableList<Pair<Int, String>> = mutableListOf()

    private val REQUEST_CODE_POST_NOTIFICATIONS = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
            }
        }


        dbManager = DatabaseManager(this)

        // Инициализация RecyclerView
        playlistsRecyclerView = findViewById(R.id.playlistsRecyclerView)
        playlistAdapter = PlaylistAdapter(playlists) { playlistId ->
            openPlaylistActivity(playlistId) // Переход в PlaylistActivity
        }
        playlistsRecyclerView.layoutManager = LinearLayoutManager(this)
        playlistsRecyclerView.adapter = playlistAdapter

        // Загрузка плейлистов
        loadPlaylists()

        // Установка слушателей для кнопок
        findViewById<Button>(R.id.addPlaylistButton).setOnClickListener {
            showAddPlaylistDialog()
        }

        findViewById<Button>(R.id.editPlaylistButton).setOnClickListener {
            showEditPlaylistDialog()
        }

        findViewById<Button>(R.id.deletePlaylistButton).setOnClickListener {
            showDeletePlaylistDialog()
        }

        // Загрузка плейлистов
        loadPlaylists()
    }

    private fun openPlaylistActivity(playlistId: Int) {
        val intent = Intent(this, PlaylistActivity::class.java)
        intent.putExtra("PLAYLIST_ID", playlistId) // Передаем ID плейлиста
        startActivity(intent)
    }

    private fun loadPlaylists() {
        playlists.clear()
        playlists.addAll(dbManager.getPlaylists())
        playlistAdapter.notifyDataSetChanged()
    }

    private fun showAddPlaylistDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_playlist, null)
        val editText = dialogView.findViewById<EditText>(R.id.playlistNameEditText)

        AlertDialog.Builder(this)
            .setTitle("Добавить плейлист")
            .setView(dialogView)
            .setPositiveButton("Создать") { _, _ ->
                val name = editText.text.toString()
                if (name.isNotBlank()) {
                    dbManager.addPlaylist(name)
                    loadPlaylists()
                    Toast.makeText(this, "Плейлист добавлен", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Имя не может быть пустым", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditPlaylistDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_playlist, null)
        val editText = dialogView.findViewById<EditText>(R.id.playlist_edit_name_input)
        val editId = dialogView.findViewById<EditText>(R.id.playlist_edit_id_input)

        AlertDialog.Builder(this)
            .setTitle("Редактировать плейлист")
            .setView(dialogView)
            .setPositiveButton("Изменить") { _, _ ->
                val id = editId.text.toString().toIntOrNull()
                val name = editText.text.toString()
                if (id != null && name.isNotBlank()) {
                    dbManager.editPlaylist(id, name)
                    loadPlaylists()
                    Toast.makeText(this, "Плейлист изменён", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Некорректные данные", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showDeletePlaylistDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_playlist, null)
        val editText = dialogView.findViewById<EditText>(R.id.playlist_delete_id_input)

        AlertDialog.Builder(this)
            .setTitle("Удалить плейлист")
            .setView(dialogView)
            .setPositiveButton("Удалить") { _, _ ->
                val id = editText.text.toString().toIntOrNull()
                if (id != null) {
                    dbManager.deletePlaylist(id)
                    loadPlaylists()
                    Toast.makeText(this, "Плейлист удалён", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Некорректный ID", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}