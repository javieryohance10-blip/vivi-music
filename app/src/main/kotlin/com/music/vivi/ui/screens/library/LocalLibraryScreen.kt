/**
 * vivimusic Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.music.vivi.ui.screens.library

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.content.ContentUris
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.music.vivi.LocalPlayerConnection
import com.music.vivi.playback.queues.ListQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private enum class LocalSection(val label: String) {
    SONGS("Songs"), ALBUMS("Albums"), ARTISTS("Artists"), FOLDERS("Folders")
}

private data class LocalTrack(
    val uri: Uri,
    val title: String,
    val artist: String,
    val album: String,
    val folder: String,
)

private data class LocalGroup(val name: String, val tracks: List<LocalTrack>)

@Composable
fun LocalLibraryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    val playerConnection = LocalPlayerConnection.current
    val permission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
    var granted by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) }
    var requestCount by rememberSaveable { mutableIntStateOf(0) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    var tracks by remember { mutableStateOf<List<LocalTrack>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var refresh by remember { mutableIntStateOf(0) }
    var section by rememberSaveable { mutableStateOf(LocalSection.SONGS) }
    var selectedGroup by rememberSaveable { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { allowed ->
        granted = allowed
        if (!allowed && requestCount >= 2 && activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            showSettings = true
        }
    }

    LaunchedEffect(granted, refresh) {
        if (granted) {
            loading = true
            error = null
            try {
                tracks = withContext(Dispatchers.IO) { readLocalTracks(context) }
            } catch (e: Exception) {
                error = e.localizedMessage ?: "Could not read audio files."
            } finally {
                loading = false
            }
        }
    }

    val groups = remember(tracks, section) {
        when (section) {
            LocalSection.ALBUMS -> tracks.groupBy { it.album.ifBlank { "Unknown album" } }
            LocalSection.ARTISTS -> tracks.groupBy { it.artist.ifBlank { "Unknown artist" } }
            LocalSection.FOLDERS -> tracks.groupBy { it.folder.ifBlank { "Unknown folder" } }
            LocalSection.SONGS -> emptyMap()
        }.toSortedMap(String.CASE_INSENSITIVE_ORDER).map { LocalGroup(it.key, it.value) }
    }
    val visibleTracks = remember(tracks, section, selectedGroup) {
        if (selectedGroup == null) tracks else tracks.filter {
            when (section) {
                LocalSection.ALBUMS -> (it.album.ifBlank { "Unknown album" }) == selectedGroup
                LocalSection.ARTISTS -> (it.artist.ifBlank { "Unknown artist" }) == selectedGroup
                LocalSection.FOLDERS -> (it.folder.ifBlank { "Unknown folder" }) == selectedGroup
                LocalSection.SONGS -> true
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = if (selectedGroup != null) ({ selectedGroup = null }) else onBack) { Text("Back") }
            Text("On-device music", style = MaterialTheme.typography.titleLarge)
        }
        if (!granted) {
            Text("Allow AURA to read audio files on this device. This permission is used only to display and play your local music.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 16.dp))
            if (showSettings) {
                Text("Audio access is disabled. Enable Music and audio permission in app settings.", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = { granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED; if (granted) showSettings = false }) { Text("Check permission") }
                Button(onClick = {
                    context.startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)))
                }) { Text("Open app settings") }
            } else {
                if (requestCount > 0) Text("Permission was denied. You can try again or enable it in app settings.", style = MaterialTheme.typography.bodySmall)
                Button(onClick = { requestCount++; permissionLauncher.launch(permission) }) {
                    Text(if (requestCount == 0) "Allow access" else "Try again")
                }
            }
        } else {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LocalSection.entries.forEach { item ->
                    FilterChip(selected = section == item, onClick = { section = item; selectedGroup = null }, label = { Text(item.label) })
                }
            }
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally).padding(24.dp))
                error != null -> Column {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = { refresh++ }) { Text("Retry") }
                }
                tracks.isEmpty() -> Text("No music files found on this device.", modifier = Modifier.padding(vertical = 24.dp))
                selectedGroup == null && section != LocalSection.SONGS -> LazyColumn {
                    items(groups, key = { it.name }) { group ->
                        Column(Modifier.fillMaxWidth().clickable { selectedGroup = group.name }.padding(vertical = 14.dp)) {
                            Text(group.name, style = MaterialTheme.typography.titleMedium)
                            Text("${group.tracks.size} songs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
                else -> LazyColumn {
                    items(visibleTracks, key = { it.uri.toString() }) { track ->
                        Column(Modifier.fillMaxWidth().clickable {
                            val items = visibleTracks.map { song ->
                                MediaItem.Builder().setMediaId(song.uri.toString()).setUri(song.uri).setMediaMetadata(
                                    MediaMetadata.Builder().setTitle(song.title).setArtist(song.artist).setAlbumTitle(song.album).build()
                                ).build()
                            }
                            playerConnection?.playQueue(ListQueue(title = "On-device music", items = items, startIndex = visibleTracks.indexOf(track)))
                        }.padding(vertical = 12.dp)) {
                            Text(track.title, style = MaterialTheme.typography.titleMedium)
                            Text("${track.artist} • ${track.album}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }
    }
}

private fun readLocalTracks(context: Context): List<LocalTrack> {
    val resolver = context.contentResolver
    val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val pathColumn = if (Build.VERSION.SDK_INT >= 29) MediaStore.MediaColumns.RELATIVE_PATH else MediaStore.MediaColumns.DATA
    val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, pathColumn)
    return resolver.query(collection, projection, "${MediaStore.Audio.Media.IS_MUSIC} != 0", null, "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC")?.use { cursor ->
        val id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val title = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val album = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val path = cursor.getColumnIndexOrThrow(pathColumn)
        buildList {
            while (cursor.moveToNext()) {
                val rawPath = cursor.getString(path).orEmpty()
                val folder = if (Build.VERSION.SDK_INT >= 29) rawPath.trimEnd('/').substringAfterLast('/', rawPath) else File(rawPath).parentFile?.name.orEmpty()
                add(LocalTrack(
                    uri = ContentUris.withAppendedId(collection, cursor.getLong(id)),
                    title = cursor.getString(title).orEmpty().ifBlank { "Unknown title" },
                    artist = cursor.getString(artist).orEmpty().ifBlank { "Unknown artist" },
                    album = cursor.getString(album).orEmpty().ifBlank { "Unknown album" },
                    folder = folder.ifBlank { "Unknown folder" },
                ))
            }
        }
    } ?: emptyList()
}
