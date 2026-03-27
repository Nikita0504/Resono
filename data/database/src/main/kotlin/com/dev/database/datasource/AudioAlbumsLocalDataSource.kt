package com.dev.database.datasource

import com.dev.database.dao.AudioAlbumsDao
import com.dev.database.entity.AudioAlbumEntity
import com.dev.database.model.AudioAlbumRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.UUID

class AudioAlbumsLocalDataSource(
    private val audioAlbumsDao: AudioAlbumsDao,
) {

    fun observeAlbums(): Flow<List<AudioAlbumRecord>> {
        return combine(
            audioAlbumsDao.observeAlbums(),
            audioAlbumsDao.observeAlbumTracks(),
        ) { albums, tracks ->
            albums.map { album ->
                album.toRecord(
                    trackIds = tracks.filter { it.albumId == album.albumId }.map { it.audioId },
                )
            }
        }
    }

    fun observeAlbum(albumId: String): Flow<AudioAlbumRecord?> {
        return combine(
            audioAlbumsDao.observeAlbum(albumId),
            audioAlbumsDao.observeAlbumTracks(),
        ) { album, tracks ->
            album?.toRecord(
                trackIds = tracks.filter { it.albumId == albumId }.map { it.audioId },
            )
        }
    }

    suspend fun createAlbum(
        title: String,
        description: String?,
        artworkUri: String?,
        trackIds: List<String>,
    ): String {
        val albumId = UUID.randomUUID().toString()
        upsertAlbum(
            albumId = albumId,
            title = title,
            description = description,
            artworkUri = artworkUri,
            trackIds = trackIds,
        )
        return albumId
    }

    suspend fun updateAlbum(
        albumId: String,
        title: String,
        description: String?,
        artworkUri: String?,
        trackIds: List<String>,
    ) {
        upsertAlbum(
            albumId = albumId,
            title = title,
            description = description,
            artworkUri = artworkUri,
            trackIds = trackIds,
        )
    }

    suspend fun deleteAlbum(albumId: String) {
        audioAlbumsDao.deleteAlbum(albumId)
    }

    private suspend fun upsertAlbum(
        albumId: String,
        title: String,
        description: String?,
        artworkUri: String?,
        trackIds: List<String>,
    ) {
        audioAlbumsDao.upsertAlbum(
            AudioAlbumEntity(
                albumId = albumId,
                title = title.trim(),
                description = description?.trim()?.takeIf { it.isNotEmpty() },
                artworkUri = artworkUri?.trim()?.takeIf { it.isNotEmpty() },
            ),
        )
        audioAlbumsDao.replaceAlbumTracks(albumId, trackIds)
    }
}

private fun AudioAlbumEntity.toRecord(trackIds: List<String>): AudioAlbumRecord {
    return AudioAlbumRecord(
        id = albumId,
        title = title,
        description = description,
        artworkUri = artworkUri,
        trackIds = trackIds,
    )
}
