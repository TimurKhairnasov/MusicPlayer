package com.example.timur.musicplayer

import android.net.Uri

class Song {

    private var filePath = ""
    private var title = ""
    private var artist = ""
    private var albumPath: Uri? = null

    fun setAlbum(path: Uri) {
        albumPath = path
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setArtist(artist: String) {
        this.artist = artist
    }

    fun setFilePath(path: String) {
        filePath = path
    }

    fun getAlbum(): Uri? {
        return albumPath
    }

    fun getArtist(): String {
        return artist
    }

    fun getFilePath(): String {
        return filePath
    }

    fun getTitle(): String {
        return title
    }

}