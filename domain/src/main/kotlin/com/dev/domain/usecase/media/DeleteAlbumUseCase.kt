package com.dev.domain.usecase.media

import com.dev.domain.repository.MediaRepository

class DeleteAlbumUseCase(
    private val mediaRepository: MediaRepository,
) {

    suspend operator fun invoke(id: String) {
        mediaRepository.deleteAlbum(id)
    }
}
