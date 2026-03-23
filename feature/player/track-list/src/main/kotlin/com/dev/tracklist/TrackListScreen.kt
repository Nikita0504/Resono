package com.dev.tracklist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    photos: List<MediaFile.Photo>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
    ) {
        item(key = "header") {
            Text(
                text = "Local photo files: ${photos.size}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(4.dp),
            )
        }

        if (photos.isEmpty()) {
            item(key = "empty") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "Photo list is empty")
                }
            }
            return@LazyColumn
        }

        items(photos, key = { it.id }) { photo ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                ) {
                    Text(
                        text = photo.title ?: photo.name,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "resolution: ${photo.width}x${photo.height}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "size: ${photo.sizeBytes.toSizeLabel()} • mime: ${photo.mimeType}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = "uri: ${photo.localUri.orEmpty()}",
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
