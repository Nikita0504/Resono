package com.dev.local.di

import android.content.ContentResolver
import android.content.Context
import com.dev.local.datasource.AudioMediaStoreDataSource
import com.dev.local.datasource.PhotoMediaStoreDataSource
import com.dev.local.datasource.VideoMediaStoreDataSource
import com.dev.local.mapper.AudioMediaMapper
import com.dev.local.mapper.PhotoMediaMapper
import com.dev.local.mapper.VideoMediaMapper
import com.dev.local.service.AudioService
import com.dev.local.service.PhotoService
import com.dev.local.service.VideoService
import com.dev.local.scanner.MediaStoreScanner
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val localDataModule = module {
    single<ContentResolver> { get<Context>().contentResolver }
    singleOf(::MediaStoreScanner)

    singleOf(::AudioMediaStoreDataSource)
    singleOf(::VideoMediaStoreDataSource)
    singleOf(::PhotoMediaStoreDataSource)

    singleOf(::AudioMediaMapper)
    singleOf(::VideoMediaMapper)
    singleOf(::PhotoMediaMapper)

    singleOf(::AudioService)
    singleOf(::VideoService)
    singleOf(::PhotoService)
}
