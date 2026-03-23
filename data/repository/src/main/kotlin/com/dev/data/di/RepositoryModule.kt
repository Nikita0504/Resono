package com.dev.data.di

import com.dev.data.repository.WaveformRepositoryImpl
import com.dev.data.repository.MediaRepositoryImpl
import com.dev.domain.repository.MediaRepository
import com.dev.domain.repository.WaveformRepository
import com.dev.domain.usecase.media.GetAudioFilesUseCase
import com.dev.domain.usecase.media.GetPhotosUseCase
import com.dev.domain.usecase.media.GetVideoFilesUseCase
import com.dev.domain.usecase.media.ScanLocalMediaUseCase
import org.koin.dsl.module

val repositoryModule = module {
    single<MediaRepository> { MediaRepositoryImpl(get(), get(), get()) }
    single<WaveformRepository> { WaveformRepositoryImpl() }

    factory { GetAudioFilesUseCase(get()) }
    factory { GetVideoFilesUseCase(get()) }
    factory { GetPhotosUseCase(get()) }
    factory { ScanLocalMediaUseCase(get()) }
}
