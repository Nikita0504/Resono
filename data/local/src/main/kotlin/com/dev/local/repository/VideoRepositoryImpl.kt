package com.dev.local.repository

import com.dev.domain.model.MediaFile
import com.dev.local.datasource.VideoMediaStoreDataSource
import com.dev.local.mapper.VideoMediaMapper

class VideoRepositoryImpl(
    private val dataSource: VideoMediaStoreDataSource,
    private val mapper: VideoMediaMapper,
) {

    fun getVideoFiles(): List<MediaFile.Video> {
        return dataSource.getVideoFiles().map(mapper::toDomain)
    }
}
