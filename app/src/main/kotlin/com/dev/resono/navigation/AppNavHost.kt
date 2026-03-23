package com.dev.resono.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dev.albumlist.albumListNavGraph
import com.dev.medialibrary.mediaLibraryNavGraph
import com.dev.player.PlayerGraph
import com.dev.player.playerNavGraph
import com.dev.tracklist.TrackListGraph
import com.dev.tracklist.trackListNavGraph

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TrackListGraph,
    ) {
        playerNavGraph()
        albumListNavGraph()
        trackListNavGraph()
        mediaLibraryNavGraph()
    }
}
