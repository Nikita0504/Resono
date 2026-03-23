package com.dev.local.datasource

import android.content.ContentUris
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.dev.local.model.AudioMediaStoreRecord
import com.dev.local.scanner.MediaStoreScanner

class AudioMediaStoreDataSource(
    private val scanner: MediaStoreScanner,
) {

    companion object {
        private const val TAG = "AudioMediaStoreDS"
        private const val MIN_FILE_SIZE_BYTES = 1024L
        private val AUDIO_EXTENSIONS = setOf("mp3", "m4a", "aac", "wav", "flac", "ogg", "opus", "3gp", "amr", "webm")
    }

    fun getAudioFiles(): List<AudioMediaStoreRecord> {
        val primaryAudio = queryAudioCollection()
        val filesAudio = queryFilesCollection()

        val deduped = linkedMapOf<String, AudioMediaStoreRecord>()

        // Сначала кладем Files fallback, затем перекрываем записями из Audio коллекции (в ней обычно богаче метаданные).
        for (record in filesAudio) {
            deduped[record.identityKey()] = record
        }
        for (record in primaryAudio) {
            deduped[record.identityKey()] = record
        }

        val result = deduped.values
            .sortedByDescending { it.dateModifiedEpochSeconds ?: it.dateAddedEpochSeconds ?: 0L }

        val downloadsCount = result.count { record ->
            val path = record.relativePath.orEmpty().lowercase()
            path.contains("download")
        }
        Log.d(
            TAG,
            "scan audio: audio=${primaryAudio.size}, files=${filesAudio.size}, dedup=${result.size}, downloads=$downloadsCount",
        )
        return result
    }

    fun debugFindAudioByName(partialName: String): List<AudioMediaStoreRecord> {
        val query = partialName.trim()
        if (query.isBlank()) return emptyList()

        val merged = getAudioFiles()
        return merged.filter { record ->
            record.displayName.contains(query, ignoreCase = true) ||
                (record.title?.contains(query, ignoreCase = true) == true)
        }
    }

    private fun queryAudioCollection(): List<AudioMediaStoreRecord> {
        val selection = buildString {
            append("${MediaStore.Audio.Media.IS_RINGTONE} = 0")
            append(" AND ${MediaStore.Audio.Media.IS_ALARM} = 0")
            append(" AND ${MediaStore.Audio.Media.IS_NOTIFICATION} = 0")
            append(" AND ${MediaStore.Audio.Media.SIZE} > $MIN_FILE_SIZE_BYTES")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                append(" AND ${MediaStore.MediaColumns.IS_PENDING} = 0")
                append(" AND (")
                append("${MediaStore.MediaColumns.RELATIVE_PATH} IS NULL")
                append(" OR (")
                append("${MediaStore.MediaColumns.RELATIVE_PATH} NOT LIKE 'Alarms/%'")
                append(" AND ${MediaStore.MediaColumns.RELATIVE_PATH} NOT LIKE 'Notifications/%'")
                append(" AND ${MediaStore.MediaColumns.RELATIVE_PATH} NOT LIKE 'Ringtones/%'")
                append(")")
                append(")")
            }
        }

        return scanner.query(
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection = audioProjection(),
            selection = selection,
            sortOrder = sortOrder(),
        ) { cursor ->
            val id = cursor.long(MediaStore.MediaColumns._ID) ?: return@query null
            val albumId = cursor.long(MediaStore.Audio.Media.ALBUM_ID)
            val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
            toRecord(
                id = id,
                contentUriString = contentUri.toString(),
                albumId = albumId,
                cursor = cursor,
                fromFilesFallback = false,
            )
        }.filterNotNull()
    }

    private fun queryFilesCollection(): List<AudioMediaStoreRecord> {
        val extensionLikeParts = AUDIO_EXTENSIONS.joinToString(" OR ") {
            "LOWER(${MediaStore.MediaColumns.DISPLAY_NAME}) LIKE ?"
        }
        val selectionArgs = AUDIO_EXTENSIONS.map { "%.${it}" }.toTypedArray()

        val selection = buildString {
            append("(")
            append("${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO}")
            append(" OR ${MediaStore.MediaColumns.MIME_TYPE} LIKE 'audio/%'")
            append(" OR ($extensionLikeParts)")
            append(")")
            append(" AND ${MediaStore.MediaColumns.SIZE} > $MIN_FILE_SIZE_BYTES")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                append(" AND ${MediaStore.MediaColumns.IS_PENDING} = 0")
                append(" AND (")
                append("${MediaStore.MediaColumns.RELATIVE_PATH} IS NULL")
                append(" OR (")
                append("${MediaStore.MediaColumns.RELATIVE_PATH} NOT LIKE 'Alarms/%'")
                append(" AND ${MediaStore.MediaColumns.RELATIVE_PATH} NOT LIKE 'Notifications/%'")
                append(" AND ${MediaStore.MediaColumns.RELATIVE_PATH} NOT LIKE 'Ringtones/%'")
                append(")")
                append(")")
            }
        }

        val volumes = linkedSetOf(MediaStore.VOLUME_EXTERNAL, MediaStore.VOLUME_EXTERNAL_PRIMARY)
        return buildList {
            for (volume in volumes) {
                val filesUri = MediaStore.Files.getContentUri(volume)
                val fromVolume = runCatching {
                    scanner.query(
                        uri = filesUri,
                        projection = filesProjection(),
                        selection = selection,
                        selectionArgs = selectionArgs,
                        sortOrder = sortOrder(),
                    ) { cursor ->
                        val id = cursor.long(MediaStore.MediaColumns._ID) ?: return@query null
                        val contentUri = ContentUris.withAppendedId(filesUri, id)
                        toRecord(
                            id = id,
                            contentUriString = contentUri.toString(),
                            albumId = null,
                            cursor = cursor,
                            fromFilesFallback = true,
                        )
                    }.filterNotNull()
                }.getOrElse { error ->
                    Log.d(TAG, "files query failed for volume=$volume: ${error.message}")
                    emptyList()
                }
                addAll(fromVolume)
            }
        }
    }

    private fun audioProjection(): Array<String> {
        return buildList {
            add(MediaStore.MediaColumns._ID)
            add(MediaStore.MediaColumns.DISPLAY_NAME)
            add(MediaStore.Audio.Media.TITLE)
            add(MediaStore.MediaColumns.MIME_TYPE)
            add(MediaStore.MediaColumns.SIZE)
            add(MediaStore.Audio.Media.DURATION)
            add(MediaStore.Audio.Media.ARTIST)
            add(MediaStore.Audio.Media.ALBUM)
            add(MediaStore.Audio.Media.TRACK)
            add(MediaStore.Audio.Media.YEAR)
            add(MediaStore.MediaColumns.DATE_ADDED)
            add(MediaStore.MediaColumns.DATE_MODIFIED)
            add(MediaStore.Audio.Media.ALBUM_ID)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(MediaStore.MediaColumns.RELATIVE_PATH)
            }
        }.toTypedArray()
    }

    private fun filesProjection(): Array<String> {
        return buildList {
            add(MediaStore.MediaColumns._ID)
            add(MediaStore.MediaColumns.DISPLAY_NAME)
            add(MediaStore.MediaColumns.TITLE)
            add(MediaStore.MediaColumns.MIME_TYPE)
            add(MediaStore.MediaColumns.SIZE)
            add(MediaStore.MediaColumns.DATE_ADDED)
            add(MediaStore.MediaColumns.DATE_MODIFIED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(MediaStore.MediaColumns.RELATIVE_PATH)
            }
        }.toTypedArray()
    }

    private fun sortOrder(): String = "${MediaStore.MediaColumns.DATE_MODIFIED} DESC"

    private fun toRecord(
        id: Long,
        contentUriString: String,
        albumId: Long?,
        cursor: Cursor,
        fromFilesFallback: Boolean,
    ): AudioMediaStoreRecord? {
        val displayName = cursor.string(MediaStore.MediaColumns.DISPLAY_NAME).orEmpty()
        val title = cursor.string(MediaStore.Audio.Media.TITLE) ?: cursor.string(MediaStore.MediaColumns.TITLE)
        val mimeType = cursor.string(MediaStore.MediaColumns.MIME_TYPE)?.trim().orEmpty()
        val size = cursor.long(MediaStore.MediaColumns.SIZE) ?: 0L

        if (size <= MIN_FILE_SIZE_BYTES) return null

        if (fromFilesFallback && mimeType.startsWith("audio/").not()) {
            val ext = fileExtension(displayName)
            if (ext !in AUDIO_EXTENSIONS) return null
        }

        val albumArtUri = albumId?.let {
            ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, it).toString()
        }

        return AudioMediaStoreRecord(
            id = id,
            displayName = displayName,
            title = title,
            mimeType = if (mimeType.isNotBlank()) mimeType else "audio/*",
            sizeBytes = size,
            durationMs = cursor.long(MediaStore.Audio.Media.DURATION) ?: 0L,
            artist = cursor.string(MediaStore.Audio.Media.ARTIST),
            album = cursor.string(MediaStore.Audio.Media.ALBUM),
            trackNumber = cursor.int(MediaStore.Audio.Media.TRACK)?.rem(1000),
            year = cursor.int(MediaStore.Audio.Media.YEAR),
            dateAddedEpochSeconds = cursor.long(MediaStore.MediaColumns.DATE_ADDED),
            dateModifiedEpochSeconds = cursor.long(MediaStore.MediaColumns.DATE_MODIFIED),
            relativePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                cursor.string(MediaStore.MediaColumns.RELATIVE_PATH)
            } else {
                null
            },
            contentUriString = contentUriString,
            albumArtUriString = albumArtUri,
        )
    }

    private fun fileExtension(displayName: String): String {
        return displayName.substringAfterLast('.', "").lowercase()
    }

    private fun Cursor.string(column: String): String? = scanner.run { getStringOrNull(column) }
    private fun Cursor.long(column: String): Long? = scanner.run { getLongOrNull(column) }
    private fun Cursor.int(column: String): Int? = scanner.run { getIntOrNull(column) }

}

private fun AudioMediaStoreRecord.identityKey(): String {
    val normalizedName = displayName.trim().lowercase()
    val normalizedTitle = title?.trim()?.lowercase().orEmpty()
    return "$normalizedName|$normalizedTitle|$sizeBytes|${dateModifiedEpochSeconds ?: 0L}"
}
