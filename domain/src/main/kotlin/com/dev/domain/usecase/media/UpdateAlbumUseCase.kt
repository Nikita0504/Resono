package com.dev.domain.usecase.media

import com.dev.domain.model.AudioAlbumDraft
import com.dev.domain.repository.MediaRepository

class UpdateAlbumUseCase(
    private val mediaRepository: MediaRepository,
) {

    suspend operator fun invoke(id: String, draft: AudioAlbumDraft) {
        mediaRepository.updateAlbum(id, draft)
    }
}
