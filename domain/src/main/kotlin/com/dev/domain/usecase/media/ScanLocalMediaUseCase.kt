package com.dev.domain.usecase.media

import com.dev.domain.repository.MediaRepository

class ScanLocalMediaUseCase(
    private val mediaRepository: MediaRepository,
) {

    suspend operator fun invoke() {
        mediaRepository.scanLocalMedia()
    }
}
