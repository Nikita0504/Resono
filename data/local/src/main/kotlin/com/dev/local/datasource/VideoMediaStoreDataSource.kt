package com.dev.local.datasource

import android.content.ContentUris
import android.os.Build
import android.provider.MediaStore
import com.dev.local.model.VideoMediaStoreRecord
import com.dev.local.scanner.MediaStoreScanner

class VideoMediaStoreDataSource(
    private val scanner: MediaStoreScanner,
) {

    fun getVideoFiles(): List<VideoMediaStoreRecord> {
        val projection = buildList {
            add(MediaStore.Video.Media._ID)
            add(MediaStore.Video.Media.DISPLAY_NAME)
            add(MediaStore.Video.Media.TITLE)
            add(MediaStore.Video.Media.MIME_TYPE)
            add(MediaStore.Video.Media.SIZE)
            add(MediaStore.Video.Media.DURATION)
            add(MediaStore.Video.Media.WIDTH)
            add(MediaStore.Video.Media.HEIGHT)
            add(MediaStore.Video.Media.DATE_ADDED)
            add(MediaStore.Video.Media.DATE_MODIFIED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(MediaStore.Video.Media.RELATIVE_PATH)
            }
        }.toTypedArray()

        val sortOrder = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"

        return scanner.query(
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection = projection,
            sortOrder = sortOrder,
        ) { cursor ->
            val id = scanner.run { cursor.getLongOrNull(MediaStore.Video.Media._ID) } ?: 0L
            val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)

            VideoMediaStoreRecord(
                id = id,
                displayName = scanner.run {
                    cursor.getStringOrNull(MediaStore.Video.Media.DISPLAY_NAME)
                } ?: "",
                title = scanner.run { cursor.getStringOrNull(MediaStore.Video.Media.TITLE) },
                mimeType = scanner.run {
                    cursor.getStringOrNull(MediaStore.Video.Media.MIME_TYPE)
                } ?: "video/*",
                sizeBytes = scanner.run { cursor.getLongOrNull(MediaStore.Video.Media.SIZE) } ?: 0L,
                durationMs = scanner.run { cursor.getLongOrNull(MediaStore.Video.Media.DURATION) } ?: 0L,
                width = scanner.run { cursor.getIntOrNull(MediaStore.Video.Media.WIDTH) } ?: 0,
                height = scanner.run { cursor.getIntOrNull(MediaStore.Video.Media.HEIGHT) } ?: 0,
                dateAddedEpochSeconds = scanner.run {
                    cursor.getLongOrNull(MediaStore.Video.Media.DATE_ADDED)
                },
                dateModifiedEpochSeconds = scanner.run {
                    cursor.getLongOrNull(MediaStore.Video.Media.DATE_MODIFIED)
                },
                relativePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    scanner.run { cursor.getStringOrNull(MediaStore.Video.Media.RELATIVE_PATH) }
                } else {
                    null
                },
                contentUriString = contentUri.toString(),
            )
        }
    }
}
