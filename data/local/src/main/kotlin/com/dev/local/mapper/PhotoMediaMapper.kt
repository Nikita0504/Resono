package com.dev.local.mapper

import com.dev.domain.model.LocalMediaData
import com.dev.domain.model.MediaFile
import com.dev.domain.model.MediaSource
import com.dev.local.model.PhotoMediaStoreRecord

class PhotoMediaMapper {

    fun toDomain(record: PhotoMediaStoreRecord): MediaFile.Photo {
        return MediaFile.Photo(
            id = record.id.toString(),
            name = record.displayName,
            mimeType = record.mimeType,
            sizeBytes = record.sizeBytes,
            dateAddedEpochSeconds = record.dateAddedEpochSeconds,
            dateModifiedEpochSeconds = record.dateModifiedEpochSeconds,
            relativePath = record.relativePath,
            source = MediaSource.LocalOnly(
                local = LocalMediaData(
                    localUri = record.contentUriString,
                    fileSizeBytes = record.sizeBytes,
                    dateAddedEpochSeconds = record.dateAddedEpochSeconds,
                    dateModifiedEpochSeconds = record.dateModifiedEpochSeconds,
                    relativePath = record.relativePath,
                ),
            ),
            width = record.width,
            height = record.height,
            title = record.title,
        )
    }
}
