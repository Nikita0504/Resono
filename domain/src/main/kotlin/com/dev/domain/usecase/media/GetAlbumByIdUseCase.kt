package com.dev.domain.usecase.media

import com.dev.domain.model.AudioAlbum
import com.dev.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow

class GetAlbumByIdUseCase(
    private val mediaRepository: MediaRepository,
) {

    operator fun invoke(id: String): Flow<AudioAlbum?> {
        return mediaRepository.getAlbumById(id)
    }
}
