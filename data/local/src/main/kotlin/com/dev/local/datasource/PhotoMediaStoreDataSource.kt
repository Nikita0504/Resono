package com.dev.local.datasource

import android.content.ContentUris
import android.os.Build
import android.provider.MediaStore
import com.dev.local.model.PhotoMediaStoreRecord
import com.dev.local.scanner.MediaStoreScanner

class PhotoMediaStoreDataSource(
    private val scanner: MediaStoreScanner,
) {

    fun getPhotos(): List<PhotoMediaStoreRecord> {
        val projection = buildList {
            add(MediaStore.Images.Media._ID)
            add(MediaStore.Images.Media.DISPLAY_NAME)
            add(MediaStore.Images.Media.TITLE)
            add(MediaStore.Images.Media.MIME_TYPE)
            add(MediaStore.Images.Media.SIZE)
            add(MediaStore.Images.Media.WIDTH)
            add(MediaStore.Images.Media.HEIGHT)
            add(MediaStore.Images.Media.DATE_ADDED)
            add(MediaStore.Images.Media.DATE_MODIFIED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(MediaStore.Images.Media.RELATIVE_PATH)
            }
        }.toTypedArray()

        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        return scanner.query(
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection = projection,
            sortOrder = sortOrder,
        ) { cursor ->
            val id = scanner.run { cursor.getLongOrNull(MediaStore.Images.Media._ID) } ?: 0L
            val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            PhotoMediaStoreRecord(
                id = id,
                displayName = scanner.run {
                    cursor.getStringOrNull(MediaStore.Images.Media.DISPLAY_NAME)
                } ?: "",
                title = scanner.run { cursor.getStringOrNull(MediaStore.Images.Media.TITLE) },
                mimeType = scanner.run {
                    cursor.getStringOrNull(MediaStore.Images.Media.MIME_TYPE)
                } ?: "image/*",
                sizeBytes = scanner.run { cursor.getLongOrNull(MediaStore.Images.Media.SIZE) } ?: 0L,
                width = scanner.run { cursor.getIntOrNull(MediaStore.Images.Media.WIDTH) } ?: 0,
                height = scanner.run { cursor.getIntOrNull(MediaStore.Images.Media.HEIGHT) } ?: 0,
                dateAddedEpochSeconds = scanner.run {
                    cursor.getLongOrNull(MediaStore.Images.Media.DATE_ADDED)
                },
                dateModifiedEpochSeconds = scanner.run {
                    cursor.getLongOrNull(MediaStore.Images.Media.DATE_MODIFIED)
                },
                relativePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    scanner.run { cursor.getStringOrNull(MediaStore.Images.Media.RELATIVE_PATH) }
                } else {
                    null
                },
                contentUriString = contentUri.toString(),
            )
        }
    }
}
