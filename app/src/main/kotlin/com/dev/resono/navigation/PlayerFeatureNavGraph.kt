package com.dev.resono.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.dev.albumlist.albumListNavGraph
import com.dev.domain.model.MediaFile
import com.dev.tracklist.TrackListGraph
import com.dev.tracklist.trackListNavGraph
import kotlinx.serialization.Serializable

@Serializable
object PlayerFeatureGraph

fun NavGraphBuilder.playerFeatureNavGraph(
    onTrackSelected: (tracks: List<MediaFile.Audio>, startIndex: Int) -> Unit,
) {
    navigation<PlayerFeatureGraph>(startDestination = TrackListGraph) {
        trackListNavGraph(onTrackSelected = onTrackSelected)
        albumListNavGraph()
    }
}
