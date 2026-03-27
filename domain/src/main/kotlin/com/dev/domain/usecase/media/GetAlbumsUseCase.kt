package com.dev.domain.usecase.media

import com.dev.domain.model.AudioAlbum
import com.dev.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow

class GetAlbumsUseCase(
    private val mediaRepository: MediaRepository,
) {

    operator fun invoke(): Flow<List<AudioAlbum>> {
        return mediaRepository.getAlbums()
    }
}
