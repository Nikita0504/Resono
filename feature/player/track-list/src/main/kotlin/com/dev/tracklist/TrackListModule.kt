package com.dev.tracklist

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val trackListModule = module {
    viewModelOf(::AudioListViewModel)
}
