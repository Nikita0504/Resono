package com.dev.data.repository

import com.dev.domain.model.MediaFile
import com.dev.domain.repository.MediaRepository
import com.dev.local.repository.AudioRepositoryImpl
import com.dev.local.repository.PhotoRepositoryImpl
import com.dev.local.repository.VideoRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update

class MediaRepositoryImpl(
    private val audioRepository: AudioRepositoryImpl,
    private val videoRepository: VideoRepositoryImpl,
    private val photoRepository: PhotoRepositoryImpl,
) : MediaRepository {

    private val refreshState = MutableStateFlow(0L)

    override fun getAudioFiles(): Flow<List<MediaFile.Audio>> {
        return refreshState.mapLatest { audioRepository.getAudioFiles() }
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
        return audioRepository.getAudioFiles().firstOrNull { it.id == id }
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
}
