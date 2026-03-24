package com.dev.local.service

import com.dev.domain.model.MediaFile
import com.dev.local.datasource.PhotoMediaStoreDataSource
import com.dev.local.mapper.PhotoMediaMapper

class PhotoService(
    private val dataSource: PhotoMediaStoreDataSource,
    private val mapper: PhotoMediaMapper,
) {

    fun getPhotos(): List<MediaFile.Photo> {
        return dataSource.getPhotos().map(mapper::toDomain)
    }
}
