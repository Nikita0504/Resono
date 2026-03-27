package com.dev.albumlist

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val albumListModule = module {
    viewModelOf(::AlbumListViewModel)
    viewModelOf(::AlbumEditorViewModel)
}
