package com.dev.local.mapper

import com.dev.domain.model.LocalMediaData
import com.dev.domain.model.MediaFile
import com.dev.domain.model.MediaSource
import com.dev.local.model.AudioMediaStoreRecord

class AudioMediaMapper {

    fun toDomain(record: AudioMediaStoreRecord): MediaFile.Audio {
        return MediaFile.Audio(
            id = record.id.toString(),
            name = record.displayName,
            mimeType = record.mimeType,
            sizeBytes = record.sizeBytes,
            durationMs = record.durationMs,
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
            artist = record.artist.orEmpty(),
            album = record.album.orEmpty(),
            albumArtUri = record.albumArtUriString,
            trackNumber = record.trackNumber,
            year = record.year,
            title = record.title,
        )
    }
}
