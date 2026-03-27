package com.dev.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dev.theme.ResonoTheme

@Composable
fun ResonoSurfaceCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
            )
            .padding(ResonoTheme.dimens.cardPadding),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = content,
    )
}

@Composable
fun ResonoSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (action != null) {
            Spacer(modifier = Modifier.width(12.dp))
            Row(content = action)
        }
    }
}

@Composable
fun ResonoEmptyState(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun ResonoFormSection(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    ResonoSurfaceCard(modifier = modifier) {
        if (!title.isNullOrBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        content()
    }
}

@Composable
fun ResonoArtwork(
    artworkUri: String?,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        if (artworkUri.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "♪",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        } else {
            AsyncImage(
                model = artworkUri,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
fun ResonoCoverPickerSection(
    artworkUri: String,
    onPickCover: () -> Unit,
    onClearCover: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Cover",
) {
    ResonoFormSection(
        modifier = modifier,
        title = title,
    ) {
        ResonoArtwork(
            artworkUri = artworkUri.ifBlank { null },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onPickCover) {
                Text("Pick cover")
            }
            TextButton(onClick = onClearCover) {
                Text("Clear")
            }
        }
    }
}

@Composable
fun ResonoMediaListItem(
    title: String,
    subtitle: String,
    meta: String,
    artworkUri: String?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    ResonoSurfaceCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ResonoArtwork(
                artworkUri = artworkUri,
                modifier = Modifier.size(ResonoTheme.dimens.artworkSize),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (meta.isNotBlank()) {
                    Text(
                        text = meta,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (trailing != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailing()
            }
        }
    }
}

@Composable
fun ResonoMetadataEditorDialog(
    title: String,
    titleDraft: String,
    onTitleDraftChange: (String) -> Unit,
    artistDraft: String,
    onArtistDraftChange: (String) -> Unit,
    albumDraft: String,
    onAlbumDraftChange: (String) -> Unit,
    artworkUriDraft: String,
    onPickCover: () -> Unit,
    onClearCover: () -> Unit,
    trackNumberDraft: String,
    onTrackNumberDraftChange: (String) -> Unit,
    yearDraft: String,
    onYearDraftChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onReset: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            ResonoMetadataEditorFields(
                titleDraft = titleDraft,
                onTitleDraftChange = onTitleDraftChange,
                artistDraft = artistDraft,
                onArtistDraftChange = onArtistDraftChange,
                albumDraft = albumDraft,
                onAlbumDraftChange = onAlbumDraftChange,
                artworkUriDraft = artworkUriDraft,
                onPickCover = onPickCover,
                onClearCover = onClearCover,
                trackNumberDraft = trackNumberDraft,
                onTrackNumberDraftChange = onTrackNumberDraftChange,
                yearDraft = yearDraft,
                onYearDraftChange = onYearDraftChange,
            )
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onReset) {
                Text("Reset all")
            }
        },
    )
}

@Composable
fun ResonoMetadataEditorScreen(
    title: String,
    titleDraft: String,
    onTitleDraftChange: (String) -> Unit,
    artistDraft: String,
    onArtistDraftChange: (String) -> Unit,
    albumDraft: String,
    onAlbumDraftChange: (String) -> Unit,
    artworkUriDraft: String,
    onPickCover: () -> Unit,
    onClearCover: () -> Unit,
    trackNumberDraft: String,
    onTrackNumberDraftChange: (String) -> Unit,
    yearDraft: String,
    onYearDraftChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ResonoSectionHeader(
            title = title,
            action = {
                TextButton(onClick = onBack) {
                    Text("Back")
                }
                TextButton(onClick = onReset) {
                    Text("Reset")
                }
                TextButton(onClick = onSave) {
                    Text("Save")
                }
            },
        )
        ResonoMetadataEditorFields(
            titleDraft = titleDraft,
            onTitleDraftChange = onTitleDraftChange,
            artistDraft = artistDraft,
            onArtistDraftChange = onArtistDraftChange,
            albumDraft = albumDraft,
            onAlbumDraftChange = onAlbumDraftChange,
            artworkUriDraft = artworkUriDraft,
            onPickCover = onPickCover,
            onClearCover = onClearCover,
            trackNumberDraft = trackNumberDraft,
            onTrackNumberDraftChange = onTrackNumberDraftChange,
            yearDraft = yearDraft,
            onYearDraftChange = onYearDraftChange,
        )
    }
}

@Composable
private fun ResonoMetadataEditorFields(
    titleDraft: String,
    onTitleDraftChange: (String) -> Unit,
    artistDraft: String,
    onArtistDraftChange: (String) -> Unit,
    albumDraft: String,
    onAlbumDraftChange: (String) -> Unit,
    artworkUriDraft: String,
    onPickCover: () -> Unit,
    onClearCover: () -> Unit,
    trackNumberDraft: String,
    onTrackNumberDraftChange: (String) -> Unit,
    yearDraft: String,
    onYearDraftChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ResonoCoverPickerSection(
            artworkUri = artworkUriDraft,
            onPickCover = onPickCover,
            onClearCover = onClearCover,
        )
        ResonoFormSection(title = "Main") {
            OutlinedTextField(
                value = titleDraft,
                onValueChange = onTitleDraftChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Title") },
            )
            OutlinedTextField(
                value = artistDraft,
                onValueChange = onArtistDraftChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Artist") },
            )
            OutlinedTextField(
                value = albumDraft,
                onValueChange = onAlbumDraftChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Album") },
            )
        }
        ResonoFormSection(title = "Additional") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = trackNumberDraft,
                    onValueChange = onTrackNumberDraftChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Track #") },
                )
                OutlinedTextField(
                    value = yearDraft,
                    onValueChange = onYearDraftChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Year") },
                )
            }
        }
    }
}
