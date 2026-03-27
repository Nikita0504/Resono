package com.dev.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "audio_overrides",
    indices = [
        Index(value = ["audio_id"]),
        Index(value = ["is_hidden"]),
        Index(value = ["is_favorite"]),
    ],
)
data class AudioOverrideEntity(
    @PrimaryKey
    @ColumnInfo(name = "audio_id")
    val audioId: String,
    @ColumnInfo(name = "is_hidden", defaultValue = "0")
    val isHidden: Boolean = false,
    @ColumnInfo(name = "is_favorite", defaultValue = "0")
    val isFavorite: Boolean = false,
    @ColumnInfo(name = "custom_title")
    val customTitle: String? = null,
    @ColumnInfo(name = "custom_artist")
    val customArtist: String? = null,
    @ColumnInfo(name = "custom_album")
    val customAlbum: String? = null,
    @ColumnInfo(name = "custom_album_art_uri")
    val customAlbumArtUri: String? = null,
    @ColumnInfo(name = "custom_track_number")
    val customTrackNumber: Int? = null,
    @ColumnInfo(name = "custom_year")
    val customYear: Int? = null,
    @ColumnInfo(name = "playback_position_ms")
    val playbackPositionMs: Long? = null,
)
