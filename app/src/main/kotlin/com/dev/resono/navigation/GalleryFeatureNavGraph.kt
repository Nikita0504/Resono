package com.dev.resono.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.dev.medialibrary.MediaLibraryGraph
import com.dev.medialibrary.mediaLibraryNavGraph
import kotlinx.serialization.Serializable

@Serializable
object GalleryFeatureGraph

fun NavGraphBuilder.galleryFeatureNavGraph() {
    navigation<GalleryFeatureGraph>(startDestination = MediaLibraryGraph) {
        mediaLibraryNavGraph()
    }
}
