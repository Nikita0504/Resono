package com.dev.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "audio_album_tracks",
    primaryKeys = ["album_id", "audio_id"],
    foreignKeys = [
        ForeignKey(
            entity = AudioAlbumEntity::class,
            parentColumns = ["album_id"],
            childColumns = ["album_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["album_id"]),
        Index(value = ["audio_id"]),
    ],
)
data class AudioAlbumTrackEntity(
    @ColumnInfo(name = "album_id")
    val albumId: String,
    @ColumnInfo(name = "audio_id")
    val audioId: String,
)
