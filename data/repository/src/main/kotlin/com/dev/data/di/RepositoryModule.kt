package com.dev.data.di

import com.dev.data.repository.MediaRepositoryImpl
import com.dev.domain.repository.MediaRepository
import com.dev.domain.usecase.media.GetAudioFilesUseCase
import com.dev.domain.usecase.media.GetPhotosUseCase
import com.dev.domain.usecase.media.GetVideoFilesUseCase
import com.dev.domain.usecase.media.ScanLocalMediaUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::MediaRepositoryImpl) { bind<MediaRepository>() }

    factoryOf(::GetAudioFilesUseCase)
    factoryOf(::GetVideoFilesUseCase)
    factoryOf(::GetPhotosUseCase)
    factoryOf(::ScanLocalMediaUseCase)
}
