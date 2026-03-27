package com.dev.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_albums")
data class AudioAlbumEntity(
    @PrimaryKey
    @ColumnInfo(name = "album_id")
    val albumId: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String? = null,
    @ColumnInfo(name = "artwork_uri")
    val artworkUri: String? = null,
)
