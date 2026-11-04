/**
 * vivimusic Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.music.vivi.ui.screens.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavController

@Composable
fun LibraryScreen(navController: NavController) {
    var showLocalLibrary by remember { mutableStateOf(false) }
    if (showLocalLibrary) {
        LocalLibraryScreen(onBack = { showLocalLibrary = false })
    } else {
        Box(Modifier.fillMaxSize()) {
            LibraryMixScreen(navController)
            TextButton(
                onClick = { showLocalLibrary = true },
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 8.dp, end = 12.dp),
            ) {
                Text("On device")
            }
        }
    }
}
