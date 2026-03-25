package com.dev.resono

import android.app.Application
import com.dev.data.di.repositoryModule
import com.dev.logger.loggerModule
import com.dev.local.di.localDataModule
import com.dev.player.di.playerModule
import com.dev.resono.di.appModule
import com.dev.tracklist.trackListModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(listOf(
                loggerModule,
                playerModule,
                localDataModule,
                repositoryModule,
                trackListModule,
                appModule,
            ))
        }
    }
}
