package com.dev.domain.repository

import com.dev.domain.model.AudioAlbum
import com.dev.domain.model.AudioAlbumDraft
import com.dev.domain.model.MediaFile
import com.dev.domain.model.AudioMetadataPatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface MediaRepository {

    fun getAudioFiles(): Flow<List<MediaFile.Audio>>

    fun getHiddenAudioFiles(): Flow<List<MediaFile.Audio>>

    fun getAlbums(): Flow<List<AudioAlbum>>

    fun getAlbumById(id: String): Flow<AudioAlbum?>

    fun getVideoFiles(): Flow<List<MediaFile.Video>>

    fun getPhotos(): Flow<List<MediaFile.Photo>>

    suspend fun scanLocalMedia()

    fun getAllMedia(): Flow<List<MediaFile>> = flowOf(emptyList())

    suspend fun getAudioFileById(id: String): MediaFile.Audio?

    suspend fun setAudioHidden(id: String, hidden: Boolean)

    suspend fun setAudioFavorite(id: String, favorite: Boolean)

    suspend fun setAudioCustomTitle(id: String, title: String?)

    suspend fun setAudioMetadata(id: String, metadata: AudioMetadataPatch)

    suspend fun setAudioPlaybackPosition(id: String, positionMs: Long)

    suspend fun getAudioPlaybackPosition(id: String): Long?

    suspend fun clearAudioOverride(id: String)

    suspend fun createAlbum(draft: AudioAlbumDraft): String

    suspend fun updateAlbum(id: String, draft: AudioAlbumDraft)

    suspend fun deleteAlbum(id: String)

    fun searchMedia(query: String): Flow<List<MediaFile>> = flowOf(emptyList())
}
