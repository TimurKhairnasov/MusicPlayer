package com.example.timur.musicplayer

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_music_player.*
import timber.log.Timber

class MusicPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        // переменная для определения состояния плеера
        var isPlaying = false

        // получаем данные о песне
        val songPath = intent.getStringExtra("filePath")
        val title = intent.getStringExtra("title")
        val artist = intent.getStringExtra("artist")
        val album = intent.getStringExtra("album")

        // забиваем данные о песне
        album_cover.setImageURI(album)
        song.text = title
        singer.text = artist

        // создаем плеер
        val mediaPlayer = MediaPlayer()

        // подготавливаем
        mediaPlayer.setDataSource(songPath)
        mediaPlayer.prepare()

        // нажимаем на play/pause
        play.setOnClickListener {

            // пауза
            if (isPlaying) {
                mediaPlayer.pause()
                play.setImageResource(R.drawable.ic_play_arrow_black_24dp)

                isPlaying = false
            } else {

                // воспроизведение
                mediaPlayer.start()
                play.setImageResource(R.drawable.ic_pause_black_24dp)

                isPlaying = true
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // возвращаемся назад
        val intent = Intent(baseContext, MainActivity::class.java)

        startActivity(intent)
    }

}