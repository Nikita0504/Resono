package com.dev.player.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.dev.domain.repository.PlaybackVisualizerRepository
import com.dev.domain.usecase.player.ObserveCurrentWaveformUseCase
import com.dev.player.PlayerRepositoryImpl
import com.dev.player.visualizer.Media3PlaybackVisualizerTap
import com.dev.player.visualizer.PlaybackVisualizerRepositoryImpl
import com.dev.player.visualizer.VisualizerRenderersFactory
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@androidx.annotation.OptIn(UnstableApi::class)
private fun provideExoPlayer(
    context: Context,
    visualizerTap: Media3PlaybackVisualizerTap,
): ExoPlayer {
    val renderersFactory = VisualizerRenderersFactory(context, visualizerTap)

    return ExoPlayer.Builder(context, renderersFactory)
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

@SuppressLint("UnsafeOptInUsageError")
val playerModule = module {
    singleOf(::Media3PlaybackVisualizerTap)
    single { provideExoPlayer(get(), get()) }

    singleOf(::PlayerRepositoryImpl) { bind<com.dev.domain.repository.PlayerRepository>() }
    singleOf(::PlaybackVisualizerRepositoryImpl) { bind<PlaybackVisualizerRepository>() }

    factoryOf(::ObserveCurrentWaveformUseCase)
}

