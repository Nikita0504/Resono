package com.dev.domain.repository

import com.dev.domain.model.MediaFile
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    fun getAudioFiles(): Flow<List<MediaFile.Audio>>

    fun getVideoFiles(): Flow<List<MediaFile.Video>>

    fun getPhotos(): Flow<List<MediaFile.Photo>>

    fun getAllMedia(): Flow<List<MediaFile>>

    suspend fun scanLocalMedia()

    suspend fun getAudioFileById(id: String): MediaFile.Audio?

    fun searchMedia(query: String): Flow<List<MediaFile>>
}
