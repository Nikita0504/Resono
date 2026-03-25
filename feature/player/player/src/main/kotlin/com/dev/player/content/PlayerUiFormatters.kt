package com.dev.player.content

import com.dev.domain.model.MediaFile

internal fun MediaFile.bestTitle(): String = when (this) {
    is MediaFile.Audio -> title?.takeIf { it.isNotBlank() } ?: name
    is MediaFile.Video -> title?.takeIf { it.isNotBlank() } ?: name
    is MediaFile.Photo -> title?.takeIf { it.isNotBlank() } ?: name
}

internal fun MediaFile.bestSubtitle(): String = when (this) {
    is MediaFile.Audio -> {
        val artistPart = artist.nullIfMissing()
        val albumPart = album.nullIfMissing()
        when {
            artistPart != null && albumPart != null -> "$artistPart • $albumPart"
            artistPart != null -> artistPart
            albumPart != null -> albumPart
            else -> "Unknown artist"
        }
    }

    is MediaFile.Video -> "Video"
    is MediaFile.Photo -> "Photo"
}

internal fun MediaFile.bestArtworkModel(): Any? {
    val candidates = when (this) {
        is MediaFile.Audio -> listOfNotNull(
            // Only dedicated artwork metadata is valid for audio tracks.
            albumArtUri.nullIfMissing(),
        )

        is MediaFile.Video -> listOfNotNull(
            thumbnailUri.nullIfMissing(),
        )

        is MediaFile.Photo -> listOfNotNull(
            localUri.nullIfMissing(),
            remoteUrl.nullIfMissing(),
        )
    }

    return candidates
        .sortedBy { it.artworkSourcePriority() }
        .firstOrNull()
}

internal fun Long.toDurationLabel(): String {
    if (this <= 0L) return "00:00"
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

internal fun String.limitChars(maxChars: Int): String {
    if (length <= maxChars) return this
    return take(maxChars - 1).trimEnd() + "…"
}

private fun String?.nullIfMissing(): String? {
    if (this.isNullOrBlank()) return null
    if (equals("null", ignoreCase = true)) return null
    return this
}

private fun String.artworkSourcePriority(): Int = when {
    startsWith("content://", ignoreCase = true) -> 0
    startsWith("file://", ignoreCase = true) -> 0
    startsWith("android.resource://", ignoreCase = true) -> 0
    startsWith("/", ignoreCase = false) -> 0
    startsWith("http://", ignoreCase = true) -> 2
    startsWith("https://", ignoreCase = true) -> 2
    else -> 1
}
