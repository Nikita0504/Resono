package com.dev.resono.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dev.medialibrary.MediaLibraryGraph
import com.dev.navigation.navigateSingleTop
import com.dev.player.PlayerSheetRoute
import com.dev.resono.player.AppPlayerIntent
import com.dev.resono.player.AppPlayerViewModel
import com.dev.resono.player.PlayerSheetMode
import com.dev.tracklist.TrackListGraph
import kotlin.reflect.KClass
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavHost() {
    AppShell()
}

@Composable
fun AppShell() {
    val navController = rememberNavController()
    val playerViewModel = koinViewModel<AppPlayerViewModel>()
    val playerState by playerViewModel.playerState.collectAsStateWithLifecycle()
    val playerUiState by playerViewModel.uiState.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestinations = remember {
        listOf(
            BottomBarDestination(
                label = "Tracks",
                iconText = "T",
                route = TrackListGraph,
                isSelected = { destination ->
                    destination.hasRouteInHierarchy<TrackListGraph>()
                },
            ),
            BottomBarDestination(
                label = "Media",
                iconText = "M",
                route = MediaLibraryGraph,
                isSelected = { destination ->
                    destination.hasRouteInHierarchy<MediaLibraryGraph>()
                },
            ),
        )
    }

    var bottomBarHeightPx by remember { mutableIntStateOf(0) }
    var playerSurfaceProgress by remember { mutableFloatStateOf(0f) }
    val sheetProgress = playerSurfaceProgress.coerceIn(0f, 1f)

    val density = LocalDensity.current
    val visibleBottomBarHeightPx by remember(bottomBarHeightPx, sheetProgress) {
        derivedStateOf { (bottomBarHeightPx * (1f - sheetProgress)).coerceAtLeast(0f) }
    }
    val visibleBottomBarHeightDp = with(density) { visibleBottomBarHeightPx.toDp() }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = PlayerFeatureGraph,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = visibleBottomBarHeightDp),
        ) {
            playerFeatureNavGraph(
                onTrackSelected = { tracks, startIndex ->
                    playerViewModel.onIntent(
                        AppPlayerIntent.PlayFromTrackList(
                            tracks = tracks,
                            startIndex = startIndex,
                        ),
                    )
                },
            )
            galleryFeatureNavGraph()
        }

        PlayerSheetRoute(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f),
            playerState = playerState,
            isVisible = playerUiState.sheetMode != PlayerSheetMode.Hidden,
            isExpanded = playerUiState.sheetMode == PlayerSheetMode.Expanded,
            bottomBarHeightPx = bottomBarHeightPx,
            onExpandedChange = { expanded ->
                playerViewModel.onIntent(
                    if (expanded) AppPlayerIntent.Expand else AppPlayerIntent.Collapse,
                )
            },
            onProgressChange = { progress ->
                playerSurfaceProgress = progress
            },
            onPlayPause = {
                playerViewModel.onIntent(AppPlayerIntent.TogglePlayPause)
            },
            onNext = {
                playerViewModel.onIntent(AppPlayerIntent.SkipToNext)
            },
            onPrevious = {
                playerViewModel.onIntent(AppPlayerIntent.SkipToPrevious)
            },
            onSeekTo = { position ->
                playerViewModel.onIntent(AppPlayerIntent.SeekTo(position))
            },
        )

        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(2f)
                .onSizeChanged { size ->
                    bottomBarHeightPx = size.height
                }
                .graphicsLayer {
                    translationY = sheetProgress * bottomBarHeightPx
                    alpha = (1f - sheetProgress).coerceIn(0f, 1f)
                },
        ) {
            bottomBarDestinations.forEach { destination ->
                NavigationBarItem(
                    selected = destination.isSelected(currentDestination),
                    onClick = {
                        navController.navigateSingleTop(destination.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    },
                    icon = { Text(destination.iconText) },
                    label = { Text(destination.label) },
                )
            }
        }
    }
}

private data class BottomBarDestination(
    val label: String,
    val iconText: String,
    val route: Any,
    val isSelected: (NavDestination?) -> Boolean,
)

private inline fun <reified T : Any> NavDestination?.hasRouteInHierarchy(): Boolean {
    return this?.hierarchy?.any { destination ->
        destination.hasTypedRoute(T::class)
    } == true
}

private fun NavDestination.hasTypedRoute(routeClass: KClass<*>): Boolean {
    val targetRoute = routeClass.qualifiedName ?: return false
    val currentRoute = route ?: return false
    return currentRoute == targetRoute || currentRoute.substringBefore("?") == targetRoute
}
