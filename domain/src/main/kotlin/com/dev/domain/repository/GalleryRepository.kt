package com.dev.domain.repository

import com.dev.domain.model.MediaFile
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {
    fun getPhotos(): Flow<List<MediaFile.Photo>>

    suspend fun getPhotoById(id: String): MediaFile.Photo?

    suspend fun getPhotosByAlbum(albumName: String): Flow<List<MediaFile.Photo>>

    suspend fun searchPhotos(query: String): Flow<List<MediaFile.Photo>>
}
