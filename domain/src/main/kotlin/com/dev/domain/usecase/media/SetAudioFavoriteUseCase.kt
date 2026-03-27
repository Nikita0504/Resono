package com.dev.domain.usecase.media

import com.dev.domain.repository.MediaRepository

class SetAudioFavoriteUseCase(
    private val mediaRepository: MediaRepository,
) {

    suspend operator fun invoke(id: String, favorite: Boolean) {
        mediaRepository.setAudioFavorite(id, favorite)
    }
}
