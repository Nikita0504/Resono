package com.dev.local.repository

import com.dev.domain.model.MediaFile
import com.dev.local.datasource.AudioMediaStoreDataSource
import com.dev.local.mapper.AudioMediaMapper

class AudioRepositoryImpl(
    private val dataSource: AudioMediaStoreDataSource,
    private val mapper: AudioMediaMapper,
) {

    fun getAudioFiles(): List<MediaFile.Audio> {
        return dataSource.getAudioFiles().map(mapper::toDomain)
    }
}
