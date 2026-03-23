package com.dev.local.di

import android.content.ContentResolver
import android.content.Context
import com.dev.local.datasource.AudioMediaStoreDataSource
import com.dev.local.datasource.PhotoMediaStoreDataSource
import com.dev.local.datasource.VideoMediaStoreDataSource
import com.dev.local.mapper.AudioMediaMapper
import com.dev.local.mapper.PhotoMediaMapper
import com.dev.local.mapper.VideoMediaMapper
import com.dev.local.repository.AudioRepositoryImpl
import com.dev.local.repository.PhotoRepositoryImpl
import com.dev.local.repository.VideoRepositoryImpl
import com.dev.local.scanner.MediaStoreScanner
import org.koin.dsl.module

val localDataModule = module {
    single<ContentResolver> { get<Context>().contentResolver }
    single { MediaStoreScanner(get()) }

    single { AudioMediaStoreDataSource(get()) }
    single { VideoMediaStoreDataSource(get()) }
    single { PhotoMediaStoreDataSource(get()) }

    single { AudioMediaMapper() }
    single { VideoMediaMapper() }
    single { PhotoMediaMapper() }

    single { AudioRepositoryImpl(get(), get()) }
    single { VideoRepositoryImpl(get(), get()) }
    single { PhotoRepositoryImpl(get(), get()) }
}
