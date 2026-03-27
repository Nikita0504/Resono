package com.dev.domain.usecase.media

import com.dev.domain.model.AudioMetadataPatch
import com.dev.domain.repository.MediaRepository

class SetAudioMetadataUseCase(
    private val mediaRepository: MediaRepository,
) {

    suspend operator fun invoke(id: String, metadata: AudioMetadataPatch) {
        mediaRepository.setAudioMetadata(id, metadata)
    }
}
