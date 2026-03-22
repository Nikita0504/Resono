package com.dev.data.di

import com.dev.data.repository.MediaRepositoryImpl
import com.dev.domain.repository.MediaRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<MediaRepository> { MediaRepositoryImpl() }
}
