package com.dev.domain.usecase.media

import com.dev.domain.repository.MediaRepository

class GetAudioPlaybackPositionUseCase(
    private val mediaRepository: MediaRepository,
) {

    suspend operator fun invoke(id: String): Long? {
        return mediaRepository.getAudioPlaybackPosition(id)
    }
}
