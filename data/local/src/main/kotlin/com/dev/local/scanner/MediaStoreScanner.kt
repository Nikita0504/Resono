package com.dev.local.scanner

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri

class MediaStoreScanner(
    private val contentResolver: ContentResolver,
) {

    fun <T> query(
        uri: Uri,
        projection: Array<String>,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = null,
        map: (Cursor) -> T,
    ): List<T> {
        val result = mutableListOf<T>()
        contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            while (cursor.moveToNext()) {
                result += map(cursor)
            }
        }
        return result
    }

    fun Cursor.getStringOrNull(columnName: String): String? {
        val index = getColumnIndex(columnName)
        if (index == -1 || isNull(index)) return null
        return getString(index)
    }

    fun Cursor.getLongOrNull(columnName: String): Long? {
        val index = getColumnIndex(columnName)
        if (index == -1 || isNull(index)) return null
        return getLong(index)
    }

    fun Cursor.getIntOrNull(columnName: String): Int? {
        val index = getColumnIndex(columnName)
        if (index == -1 || isNull(index)) return null
        return getInt(index)
    }
}
