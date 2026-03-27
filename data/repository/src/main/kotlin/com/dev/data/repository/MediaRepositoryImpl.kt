package com.dev.data.repository

import com.dev.database.datasource.AudioAlbumsLocalDataSource
import com.dev.database.datasource.AudioOverridesLocalDataSource
import com.dev.database.model.AudioOverride
import com.dev.domain.model.AudioAlbum
import com.dev.domain.model.AudioAlbumDraft
import com.dev.domain.model.AudioMetadataPatch
import com.dev.domain.model.MediaFile
import com.dev.domain.repository.MediaRepository
import com.dev.local.service.AudioService
import com.dev.local.service.PhotoService
import com.dev.local.service.VideoService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update

class MediaRepositoryImpl(
    private val audioRepository: AudioService,
    private val videoRepository: VideoService,
    private val photoRepository: PhotoService,
    private val audioAlbumsLocalDataSource: AudioAlbumsLocalDataSource,
    private val audioOverridesLocalDataSource: AudioOverridesLocalDataSource,
) : MediaRepository {

    private val refreshState = MutableStateFlow(0L)

    override fun getAudioFiles(): Flow<List<MediaFile.Audio>> {
        val scannedAudioFlow = refreshState.mapLatest { audioRepository.getAudioFiles() }
        return combine(scannedAudioFlow, audioOverridesLocalDataSource.observeAll()) { scannedAudio, overrides ->
            mergeAudioWithOverrides(
                scannedAudio = scannedAudio,
                overrides = overrides,
                includeHidden = false,
            )
        }
    }

    override fun getHiddenAudioFiles(): Flow<List<MediaFile.Audio>> {
        val scannedAudioFlow = refreshState.mapLatest { audioRepository.getAudioFiles() }
        return combine(scannedAudioFlow, audioOverridesLocalDataSource.observeAll()) { scannedAudio, overrides ->
            mergeAudioWithOverrides(
                scannedAudio = scannedAudio,
                overrides = overrides,
                includeHidden = true,
            ).filter { it.id in overrides.keys && overrides[it.id]?.isHidden == true }
        }
    }

    override fun getAlbums(): Flow<List<AudioAlbum>> {
        return combine(
            audioAlbumsLocalDataSource.observeAlbums(),
            getAudioFiles(),
        ) { albums, audioFiles ->
            val audioById = audioFiles.associateBy { it.id }
            albums.map { album ->
                AudioAlbum(
                    id = album.id,
                    title = album.title,
                    description = album.description,
                    artworkUri = album.artworkUri,
                    trackIds = album.trackIds,
                    tracks = album.trackIds.mapNotNull(audioById::get),
                )
            }
        }
    }

    override fun getAlbumById(id: String): Flow<AudioAlbum?> {
        return combine(
            audioAlbumsLocalDataSource.observeAlbum(id),
            getAudioFiles(),
        ) { album, audioFiles ->
            album?.let {
                val audioById = audioFiles.associateBy { file -> file.id }
                AudioAlbum(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    artworkUri = it.artworkUri,
                    trackIds = it.trackIds,
                    tracks = it.trackIds.mapNotNull(audioById::get),
                )
            }
        }
    }

    override fun getVideoFiles(): Flow<List<MediaFile.Video>> {
        return refreshState.mapLatest { videoRepository.getVideoFiles() }
    }

    override fun getPhotos(): Flow<List<MediaFile.Photo>> {
        return refreshState.mapLatest { photoRepository.getPhotos() }
    }

    override suspend fun scanLocalMedia() {
        refreshState.update { it + 1L }
    }

    override fun getAllMedia(): Flow<List<MediaFile>> {
        return combine(
            getAudioFiles(),
            getVideoFiles(),
            getPhotos(),
        ) { audio, video, photos ->
            buildList {
                addAll(audio)
                addAll(video)
                addAll(photos)
            }
        }
    }

    override suspend fun getAudioFileById(id: String): MediaFile.Audio? {
        val audio = audioRepository.getAudioFiles().firstOrNull { it.id == id } ?: return null
        val audioOverride = audioOverridesLocalDataSource.getByAudioId(id) ?: return audio
        if (audioOverride.isHidden) return null

        return audio.copy(
            title = audioOverride.customTitle ?: audio.title,
            artist = audioOverride.customArtist ?: audio.artist,
            album = audioOverride.customAlbum ?: audio.album,
            albumArtUri = audioOverride.customAlbumArtUri ?: audio.albumArtUri,
            trackNumber = audioOverride.customTrackNumber ?: audio.trackNumber,
            year = audioOverride.customYear ?: audio.year,
            isFavorite = audioOverride.isFavorite,
        )
    }

    override suspend fun setAudioHidden(id: String, hidden: Boolean) {
        audioOverridesLocalDataSource.setHidden(id, hidden)
    }

    override suspend fun setAudioFavorite(id: String, favorite: Boolean) {
        audioOverridesLocalDataSource.setFavorite(id, favorite)
    }

    override suspend fun setAudioCustomTitle(id: String, title: String?) {
        audioOverridesLocalDataSource.setCustomTitle(id, title)
    }

    override suspend fun setAudioMetadata(id: String, metadata: AudioMetadataPatch) {
        audioOverridesLocalDataSource.setMetadata(
            audioId = id,
            title = metadata.title,
            artist = metadata.artist,
            album = metadata.album,
            albumArtUri = metadata.albumArtUri,
            trackNumber = metadata.trackNumber,
            year = metadata.year,
        )
    }

    override suspend fun setAudioPlaybackPosition(id: String, positionMs: Long) {
        audioOverridesLocalDataSource.setPlaybackPosition(id, positionMs)
    }

    override suspend fun getAudioPlaybackPosition(id: String): Long? {
        return audioOverridesLocalDataSource.getPlaybackPosition(id)
    }

    override suspend fun clearAudioOverride(id: String) {
        audioOverridesLocalDataSource.clearOverride(id)
    }

    override suspend fun createAlbum(draft: AudioAlbumDraft): String {
        return audioAlbumsLocalDataSource.createAlbum(
            title = draft.title,
            description = draft.description,
            artworkUri = draft.artworkUri,
            trackIds = draft.trackIds,
        )
    }

    override suspend fun updateAlbum(id: String, draft: AudioAlbumDraft) {
        audioAlbumsLocalDataSource.updateAlbum(
            albumId = id,
            title = draft.title,
            description = draft.description,
            artworkUri = draft.artworkUri,
            trackIds = draft.trackIds,
        )
    }

    override suspend fun deleteAlbum(id: String) {
        audioAlbumsLocalDataSource.deleteAlbum(id)
    }

    override fun searchMedia(query: String): Flow<List<MediaFile>> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) return getAllMedia()

        return getAllMedia().map { media ->
            media.filter { file ->
                val baseMatch = file.name.contains(normalizedQuery, ignoreCase = true) ||
                    file.mimeType.contains(normalizedQuery, ignoreCase = true)

                when (file) {
                    is MediaFile.Audio -> {
                        baseMatch ||
                            file.artist.contains(normalizedQuery, ignoreCase = true) ||
                            file.album.contains(normalizedQuery, ignoreCase = true)
                    }
                    is MediaFile.Video -> {
                        baseMatch || (file.title?.contains(normalizedQuery, ignoreCase = true) == true)
                    }
                    is MediaFile.Photo -> {
                        baseMatch || (file.title?.contains(normalizedQuery, ignoreCase = true) == true)
                    }
                }
            }
        }
    }

    private fun mergeAudioWithOverrides(
        scannedAudio: List<MediaFile.Audio>,
        overrides: Map<String, AudioOverride>,
        includeHidden: Boolean,
    ): List<MediaFile.Audio> {
        return scannedAudio.mapNotNull { audio ->
            val audioOverride = overrides[audio.id]
            if (!includeHidden && audioOverride?.isHidden == true) return@mapNotNull null

            audio.copy(
                title = audioOverride?.customTitle ?: audio.title,
                artist = audioOverride?.customArtist ?: audio.artist,
                album = audioOverride?.customAlbum ?: audio.album,
                albumArtUri = audioOverride?.customAlbumArtUri ?: audio.albumArtUri,
                trackNumber = audioOverride?.customTrackNumber ?: audio.trackNumber,
                year = audioOverride?.customYear ?: audio.year,
                isFavorite = audioOverride?.isFavorite ?: audio.isFavorite,
            )
        }
    }
}
