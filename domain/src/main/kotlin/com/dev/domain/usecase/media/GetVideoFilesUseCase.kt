package com.dev.domain.usecase.media

import com.dev.domain.model.MediaFile
import com.dev.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow

class GetVideoFilesUseCase(
    private val mediaRepository: MediaRepository,
) {

    operator fun invoke(): Flow<List<MediaFile.Video>> {
        return mediaRepository.getVideoFiles()
    }
}
