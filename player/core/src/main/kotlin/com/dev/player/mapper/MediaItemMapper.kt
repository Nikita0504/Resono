package com.dev.player.mapper

import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.dev.domain.model.MediaSource
import com.dev.domain.model.PlayableMedia

object MediaItemMapper {

    fun PlayableMedia.toMediaItem(): MediaItem? {
        val uri = resolveUri() ?: return null

        return MediaItem.Builder()
            .setMediaId(mediaFile.id)
            .setUri(uri)
            .setMediaMetadata(buildMetadata())
            .build()
    }

    private fun PlayableMedia.resolveUri(): String? = when (val s = mediaFile.source) {
        is MediaSource.LocalOnly -> s.local.localUri
        is MediaSource.Synced -> s.local.localUri
        is MediaSource.RemoteOnly -> s.remote.remoteUrl
    }

    private fun PlayableMedia.buildMetadata(): MediaMetadata {
        val resolvedTitle = when (val file = mediaFile) {
            is com.dev.domain.model.MediaFile.Audio -> file.title?.takeIf { it.isNotBlank() } ?: file.name
            is com.dev.domain.model.MediaFile.Video -> file.title?.takeIf { it.isNotBlank() } ?: file.name
            is com.dev.domain.model.MediaFile.Photo -> file.title?.takeIf { it.isNotBlank() } ?: file.name
        }
        val builder = MediaMetadata.Builder()
            .setTitle(resolvedTitle)
            .setIsBrowsable(false)
            .setIsPlayable(true)

        when (this) {
            is PlayableMedia.Audio -> {
                builder
                    .setArtist(file.artist)
                    .setAlbumTitle(file.album)
                    .setTrackNumber(file.trackNumber)
                    .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                    .setArtworkUri(file.albumArtUri?.toUri())
            }

            is PlayableMedia.Video -> {
                builder
                    .setMediaType(MediaMetadata.MEDIA_TYPE_VIDEO)
                    .setArtworkUri(file.thumbnailUri?.toUri())
            }
        }

        return builder.build()
    }
}
