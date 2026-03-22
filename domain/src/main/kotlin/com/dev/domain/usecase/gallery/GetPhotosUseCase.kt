package com.dev.domain.usecase.gallery

import com.dev.domain.model.MediaFile
import com.dev.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow

class GetPhotosUseCase(
    private val galleryRepository: GalleryRepository,
) {

    operator fun invoke(): Flow<List<MediaFile.Photo>> {
        return galleryRepository.getPhotos()
    }
}
