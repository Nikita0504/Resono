package com.dev.database.di

import android.content.Context
import androidx.room.Room
import com.dev.database.datasource.AudioOverridesLocalDataSource
import com.dev.database.datasource.AudioAlbumsLocalDataSource
import com.dev.database.db.AppDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            get<Context>(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME,
        ).addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .build()
    }

    single { get<AppDatabase>().audioOverridesDao() }
    single { get<AppDatabase>().audioAlbumsDao() }
    singleOf(::AudioOverridesLocalDataSource)
    singleOf(::AudioAlbumsLocalDataSource)
}
