package com.dev.resono.di

import com.dev.resono.player.AppPlayerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AppPlayerViewModel)
}
