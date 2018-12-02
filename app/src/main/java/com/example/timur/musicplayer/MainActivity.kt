package com.example.timur.musicplayer

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.drawee.backends.pipeline.Fresco
import com.vanniktech.rxpermission.Permission
import com.vanniktech.rxpermission.RealRxPermission
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_song.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var i = 0

        Timber.plant(Timber.DebugTree())

        Fresco.initialize(this)

        // это нужно для первой версии - взять ОДНУ песню
        val songPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            .absolutePath

        Timber.d("Timber - songPath = $songPath")

        // достаем список песен
        val songList = getSongs()

        // если список не пустой
        if (songList.isNotEmpty()) {

            songList.forEach { song ->

                // создаем view с песней
                val view = LayoutInflater.from(this).inflate(R.layout.item_song, null)

                // забиваем данными песни
                view.song_album.setImageURI(song.getAlbum(), this)
                view.song_title.text = song.getTitle()
                view.song_artist.text = song.getArtist()

                // ставим слушаетель нажатия
                view.setOnClickListener {

                    GlobalScope.launch(Dispatchers.Default) {

                        // разрешения
                        val isGranted = RealRxPermission.getInstance(baseContext)
                            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .await()
                            .state() == Permission.State.GRANTED

                        if (isGranted) {
                            // запускаем activity
                            val intent = Intent(baseContext, MusicPlayerActivity::class.java)

                            // забиваем интент данными песни
                            intent.putExtra("filePath", song.getFilePath())
                            intent.putExtra("album", song.getAlbum().toString())
                            intent.putExtra("title", song.getTitle())
                            intent.putExtra("artist", song.getArtist())

                            startActivity(intent)
                        }
                    }
                }

                // добавляем в список на экране
                list.addView(view)
            }
        }

        i++
    }

    private fun getSongs(): List<Song> {

        // список песен
        val listOfSongs = mutableListOf<Song>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursorCols = arrayOf(
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val where = MediaStore.Audio.Media.IS_MUSIC + "=1"
        val cursor = contentResolver.query(
            uri,
            cursorCols, where, null, null
        )

        while (cursor?.moveToNext() == true) {

            val artist = cursor.getString(
                cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            )
            val title = cursor.getString(
                cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            )
            val filePath = cursor.getString(
                cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            )
            val albumId = cursor.getLong(
                cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            )

            Timber.d("Timber $artist - $title")
            // обложка
            val sArtworkUri = Uri
                .parse("content://media/external/audio/albumart")
            val albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId)

            // создаем песню
            val song = Song()
            song.setArtist(artist)
            song.setAlbum(albumArtUri)
            song.setFilePath(filePath)
            song.setTitle(title)

            // добавляем в список
            listOfSongs.add(song)
        }

        cursor?.close()

        return listOfSongs
    }
}
