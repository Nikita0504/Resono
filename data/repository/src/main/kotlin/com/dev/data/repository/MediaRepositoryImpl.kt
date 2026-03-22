package com.dev.data.repository

import com.dev.domain.model.MediaFile
import com.dev.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MediaRepositoryImpl : MediaRepository {

    // TODO: Реализовать сканирование MediaStore
    override suspend fun scanLocalMedia() {
        // Здесь будет вызов MediaStoreDataSource для сканирования устройства
        // и сохранения результатов в Room через AudioDao, VideoDao, PhotoDao
    }

    override fun getAudioFiles(): Flow<List<MediaFile.Audio>> {
        // TODO: Заменить на реальный запрос к AudioDao
        return flowOf(emptyList())
    }

    override fun getVideoFiles(): Flow<List<MediaFile.Video>> {
        // TODO: Заменить на реальный запрос к VideoDao
        return flowOf(emptyList())
    }

    override fun getPhotos(): Flow<List<MediaFile.Photo>> {
        // TODO: Заменить на реальный запрос к PhotoDao
        return flowOf(emptyList())
    }

    override fun getAllMedia(): Flow<List<MediaFile>> {
        // TODO: Объединить все источники
        return flowOf(emptyList())
    }

    override suspend fun getAudioFileById(id: String): MediaFile.Audio? {
        // TODO: Заменить на реальный запрос к AudioDao
        return null
    }

    override suspend fun searchMedia(query: String): Flow<List<MediaFile>> {
        // TODO: Реализовать поиск по всем типам медиа
        return flowOf(emptyList())
    }
}
