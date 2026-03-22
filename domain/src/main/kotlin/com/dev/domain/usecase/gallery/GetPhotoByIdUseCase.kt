package com.dev.domain.usecase.gallery

import com.dev.domain.model.MediaFile
import com.dev.domain.repository.GalleryRepository

class GetPhotoByIdUseCase(
    private val galleryRepository: GalleryRepository,
) {

    suspend operator fun invoke(id: String): MediaFile.Photo? {
        return galleryRepository.getPhotoById(id)
    }
}
