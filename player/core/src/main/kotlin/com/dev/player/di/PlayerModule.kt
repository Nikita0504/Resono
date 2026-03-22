package com.dev.player.di

import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.dev.player.PlayerRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val playerModule = module {

    single<Player> {
        ExoPlayer.Builder(get())
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true,
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
    }

    singleOf(::PlayerRepositoryImpl)
}