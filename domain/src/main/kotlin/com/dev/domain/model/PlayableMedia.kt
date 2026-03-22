package com.dev.domain.model

/**
 * Воспроизводимые медиа-файлы.
 * 
 * Используется в PlayerRepository для разделения логики:
 * - Audio и Video воспроизводятся через ExoPlayer (Media3)
 * - Photo не воспроизводятся, а отображаются через Coil/Glide
 */
sealed interface PlayableMedia {
    val mediaFile: MediaFile
    
    data class Audio(
        val file: MediaFile.Audio,
    ) : PlayableMedia {
        override val mediaFile: MediaFile = file
    }
    
    data class Video(
        val file: MediaFile.Video,
    ) : PlayableMedia {
        override val mediaFile: MediaFile = file
    }
}
