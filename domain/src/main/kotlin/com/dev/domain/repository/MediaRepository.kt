package com.dev.domain.repository

import com.dev.domain.model.MediaFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface MediaRepository {

    fun getAudioFiles(): Flow<List<MediaFile.Audio>>

    fun getVideoFiles(): Flow<List<MediaFile.Video>>

    fun getPhotos(): Flow<List<MediaFile.Photo>>

    suspend fun scanLocalMedia()

    fun getAllMedia(): Flow<List<MediaFile>> = flowOf(emptyList())

    suspend fun getAudioFileById(id: String): MediaFile.Audio?

    fun searchMedia(query: String): Flow<List<MediaFile>> = flowOf(emptyList())
}
