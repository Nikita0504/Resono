package com.dev.domain.usecase.media

import com.dev.domain.repository.MediaRepository

class SetAudioHiddenUseCase(
    private val mediaRepository: MediaRepository,
) {

    suspend operator fun invoke(id: String, hidden: Boolean) {
        mediaRepository.setAudioHidden(id, hidden)
    }
}
