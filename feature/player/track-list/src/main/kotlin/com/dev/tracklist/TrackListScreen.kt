package com.dev.tracklist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dev.domain.model.MediaFile

@Composable
fun TrackListScreen(
    tracks: List<MediaFile.Audio>,
    onTrackClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
    ) {
        item(key = "header") {
            Text(
                text = "Local tracks: ${tracks.size}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(4.dp),
            )
        }

        if (tracks.isEmpty()) {
            item(key = "empty") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "Track list is empty")
                }
            }
            return@LazyColumn
        }

        itemsIndexed(tracks, key = { _, track -> track.id }) { index, track ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onTrackClick(index) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                ) {
                    Text(
                        text = track.title ?: track.name,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "${track.artist} • ${track.album}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "duration: ${track.durationMs.toDurationLabel()} • size: ${track.sizeBytes.toSizeLabel()}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = "uri: ${track.localUri.orEmpty()}",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

private fun Long.toSizeLabel(): String {
    if (this <= 0L) return "0 B"
    val kilobytes = this / 1024.0
    if (kilobytes < 1024.0) return "${"%.1f".format(kilobytes)} KB"
    val megabytes = kilobytes / 1024.0
    return "${"%.1f".format(megabytes)} MB"
}

private fun Long.toDurationLabel(): String {
    if (this <= 0L) return "00:00"
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
