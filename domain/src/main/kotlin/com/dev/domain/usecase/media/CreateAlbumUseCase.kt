package com.dev.domain.usecase.media

import com.dev.domain.model.AudioAlbumDraft
import com.dev.domain.repository.MediaRepository

class CreateAlbumUseCase(
    private val mediaRepository: MediaRepository,
) {

    suspend operator fun invoke(draft: AudioAlbumDraft): String {
        return mediaRepository.createAlbum(draft)
    }
}
