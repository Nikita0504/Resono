package com.dev.domain.usecase.media

import com.dev.domain.model.MediaFile
import com.dev.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow

class SearchMediaUseCase(
    private val mediaRepository: MediaRepository,
) {

    operator fun invoke(query: String): Flow<List<MediaFile>> {
        return mediaRepository.searchMedia(query)
    }
}
