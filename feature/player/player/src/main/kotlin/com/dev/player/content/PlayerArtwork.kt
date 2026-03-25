package com.dev.player.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import coil3.compose.SubcomposeAsyncImage

@Composable
internal fun PlayerArtwork(
    modifier: Modifier = Modifier,
    progress: Float,
    artworkModel: Any?,
) {
    val shape = RoundedCornerShape(lerp(8.dp, 20.dp, progress))

    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        if (artworkModel == null) {
            ArtworkFallback()
        } else {
            SubcomposeAsyncImage(
                model = artworkModel,
                contentDescription = "Track artwork",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    ArtworkLoading()
                },
                error = {
                    ArtworkFallback()
                },
            )
        }
    }
}

@Composable
private fun ArtworkLoading() {
    ArtworkCenteredContainer {
        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
    }
}

@Composable
private fun ArtworkFallback() {
    ArtworkCenteredContainer {
        Text(
            text = "♪",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun ArtworkCenteredContainer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
