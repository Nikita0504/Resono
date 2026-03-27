package com.dev.domain.usecase.media

import com.dev.domain.repository.MediaRepository

class SetAudioCustomTitleUseCase(
    private val mediaRepository: MediaRepository,
) {

    suspend operator fun invoke(id: String, title: String?) {
        mediaRepository.setAudioCustomTitle(id, title)
    }
}
