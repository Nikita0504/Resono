package com.dev.domain.usecase.media

import com.dev.domain.repository.MediaRepository

class SetAudioPlaybackPositionUseCase(
    private val mediaRepository: MediaRepository,
) {

    suspend operator fun invoke(id: String, positionMs: Long) {
        mediaRepository.setAudioPlaybackPosition(id, positionMs)
    }
}
