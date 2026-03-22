package com.dev.domain.usecase.media

import com.dev.domain.model.MediaFile
import com.dev.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow

class GetAudioFilesUseCase(
    private val mediaRepository: MediaRepository,
) {

    operator fun invoke(): Flow<List<MediaFile.Audio>> {
        return mediaRepository.getAudioFiles()
    }
}
