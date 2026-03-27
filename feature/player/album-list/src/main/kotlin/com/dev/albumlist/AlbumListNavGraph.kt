package com.dev.albumlist

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.dev.domain.model.MediaFile
import kotlinx.serialization.Serializable

@Serializable
object AlbumListGraph

@Serializable
internal object AlbumListHomeRoute

@Serializable
internal data class AlbumEditorRouteDef(
    val albumId: String? = null,
)

fun NavGraphBuilder.albumListNavGraph(
    navController: NavHostController,
    onTrackSelected: (tracks: List<MediaFile.Audio>, startIndex: Int) -> Unit,
) {
    navigation<AlbumListGraph>(startDestination = AlbumListHomeRoute) {
        composable<AlbumListHomeRoute> {
            AlbumListRoute(
                onCreateAlbum = {
                    navController.navigate(AlbumEditorRouteDef())
                },
                onOpenAlbum = { albumId ->
                    navController.navigate(AlbumEditorRouteDef(albumId = albumId))
                },
                onPlayAlbum = onTrackSelected,
            )
        }
        composable<AlbumEditorRouteDef> {
            AlbumEditorRoute(
                onClose = { navController.popBackStack() },
                onPlayAlbum = onTrackSelected,
            )
        }
    }
}
