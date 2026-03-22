package com.dev.domain.usecase.gallery

import com.dev.domain.model.MediaFile
import com.dev.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow

class SearchPhotosUseCase(
    private val galleryRepository: GalleryRepository,
) {

    operator fun invoke(query: String): Flow<List<MediaFile.Photo>> {
        return galleryRepository.searchPhotos(query)
    }
}
