package com.dev.data.di

import com.dev.data.repository.MediaRepositoryImpl
import com.dev.database.di.databaseModule
import com.dev.domain.repository.MediaRepository
import com.dev.domain.usecase.media.ClearAudioOverrideUseCase
import com.dev.domain.usecase.media.CreateAlbumUseCase
import com.dev.domain.usecase.media.DeleteAlbumUseCase
import com.dev.domain.usecase.media.GetAlbumByIdUseCase
import com.dev.domain.usecase.media.GetAlbumsUseCase
import com.dev.domain.usecase.media.GetAudioFilesUseCase
import com.dev.domain.usecase.media.GetHiddenAudioFilesUseCase
import com.dev.domain.usecase.media.GetAudioPlaybackPositionUseCase
import com.dev.domain.usecase.media.GetPhotosUseCase
import com.dev.domain.usecase.media.GetVideoFilesUseCase
import com.dev.domain.usecase.media.ScanLocalMediaUseCase
import com.dev.domain.usecase.media.SetAudioCustomTitleUseCase
import com.dev.domain.usecase.media.SetAudioFavoriteUseCase
import com.dev.domain.usecase.media.SetAudioHiddenUseCase
import com.dev.domain.usecase.media.SetAudioMetadataUseCase
import com.dev.domain.usecase.media.SetAudioPlaybackPositionUseCase
import com.dev.domain.usecase.media.UpdateAlbumUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    includes(databaseModule)

    singleOf(::MediaRepositoryImpl) { bind<MediaRepository>() }

    factoryOf(::GetAudioFilesUseCase)
    factoryOf(::GetHiddenAudioFilesUseCase)
    factoryOf(::GetAlbumsUseCase)
    factoryOf(::GetAlbumByIdUseCase)
    factoryOf(::GetVideoFilesUseCase)
    factoryOf(::GetPhotosUseCase)
    factoryOf(::ScanLocalMediaUseCase)
    factoryOf(::CreateAlbumUseCase)
    factoryOf(::UpdateAlbumUseCase)
    factoryOf(::DeleteAlbumUseCase)
    factoryOf(::SetAudioHiddenUseCase)
    factoryOf(::SetAudioFavoriteUseCase)
    factoryOf(::SetAudioCustomTitleUseCase)
    factoryOf(::SetAudioMetadataUseCase)
    factoryOf(::SetAudioPlaybackPositionUseCase)
    factoryOf(::GetAudioPlaybackPositionUseCase)
    factoryOf(::ClearAudioOverrideUseCase)
}
