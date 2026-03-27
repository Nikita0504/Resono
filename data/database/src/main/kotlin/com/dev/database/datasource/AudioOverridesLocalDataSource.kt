package com.dev.database.datasource

import com.dev.database.dao.AudioOverridesDao
import com.dev.database.entity.AudioOverrideEntity
import com.dev.database.mapper.toModel
import com.dev.database.model.AudioOverride
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AudioOverridesLocalDataSource(
    private val audioOverridesDao: AudioOverridesDao,
) {

    fun observeAll(): Flow<Map<String, AudioOverride>> {
        return audioOverridesDao.observeAll().map { entities ->
            entities.map { it.toModel() }.associateBy { it.audioId }
        }
    }

    suspend fun getByAudioId(audioId: String): AudioOverride? {
        return audioOverridesDao.getByAudioId(audioId)?.toModel()
    }

    suspend fun getPlaybackPosition(audioId: String): Long? {
        return audioOverridesDao.getByAudioId(audioId)?.playbackPositionMs
    }

    suspend fun setHidden(audioId: String, hidden: Boolean) {
        update(audioId) { current ->
            current.copy(isHidden = hidden)
        }
    }

    suspend fun setFavorite(audioId: String, favorite: Boolean) {
        update(audioId) { current ->
            current.copy(isFavorite = favorite)
        }
    }

    suspend fun setCustomTitle(audioId: String, title: String?) {
        update(audioId) { current ->
            current.copy(customTitle = title?.trim()?.takeIf { it.isNotEmpty() })
        }
    }

    suspend fun setMetadata(
        audioId: String,
        title: String?,
        artist: String?,
        album: String?,
        albumArtUri: String?,
        trackNumber: Int?,
        year: Int?,
    ) {
        update(audioId) { current ->
            current.copy(
                customTitle = title.normalizedText(),
                customArtist = artist.normalizedText(),
                customAlbum = album.normalizedText(),
                customAlbumArtUri = albumArtUri.normalizedText(),
                customTrackNumber = trackNumber?.takeIf { it > 0 },
                customYear = year?.takeIf { it > 0 },
            )
        }
    }

    suspend fun setPlaybackPosition(audioId: String, positionMs: Long) {
        update(audioId) { current ->
            current.copy(playbackPositionMs = positionMs.takeIf { it > 0L })
        }
    }

    suspend fun clearOverride(audioId: String) {
        audioOverridesDao.deleteByAudioId(audioId)
    }

    private suspend fun update(
        audioId: String,
        transform: (AudioOverrideEntity) -> AudioOverrideEntity,
    ) {
        val current = audioOverridesDao.getByAudioId(audioId)
            ?: AudioOverrideEntity(audioId = audioId)

        val updated = transform(current)
        if (updated.isEmptyOverride()) {
            audioOverridesDao.deleteByAudioId(audioId)
        } else {
            audioOverridesDao.upsert(updated)
        }
    }
}

private fun AudioOverrideEntity.isEmptyOverride(): Boolean {
    return !isHidden &&
        !isFavorite &&
        customTitle.isNullOrBlank() &&
        customArtist.isNullOrBlank() &&
        customAlbum.isNullOrBlank() &&
        customAlbumArtUri.isNullOrBlank() &&
        customTrackNumber == null &&
        customYear == null &&
        playbackPositionMs == null
}

private fun String?.normalizedText(): String? {
    return this?.trim()?.takeIf { it.isNotEmpty() }
}
