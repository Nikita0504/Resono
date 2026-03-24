package com.dev.local.service

import com.dev.domain.model.MediaFile
import com.dev.local.datasource.VideoMediaStoreDataSource
import com.dev.local.mapper.VideoMediaMapper

class VideoService(
    private val dataSource: VideoMediaStoreDataSource,
    private val mapper: VideoMediaMapper,
) {

    fun getVideoFiles(): List<MediaFile.Video> {
        return dataSource.getVideoFiles().map(mapper::toDomain)
    }
}
