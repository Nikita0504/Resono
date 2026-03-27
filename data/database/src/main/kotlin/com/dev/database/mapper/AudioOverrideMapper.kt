package com.dev.database.mapper

import com.dev.database.entity.AudioOverrideEntity
import com.dev.database.model.AudioOverride

internal fun AudioOverrideEntity.toModel(): AudioOverride {
    return AudioOverride(
        audioId = audioId,
        isHidden = isHidden,
        isFavorite = isFavorite,
        customTitle = customTitle,
        customArtist = customArtist,
        customAlbum = customAlbum,
        customAlbumArtUri = customAlbumArtUri,
        customTrackNumber = customTrackNumber,
        customYear = customYear,
        playbackPositionMs = playbackPositionMs,
    )
}
