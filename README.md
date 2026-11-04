# AURA Music

A native Android music player with a clean monochrome interface, local library tools, and online music integrations.

## Project status

AURA is a maintained fork of [vivi-music](https://github.com/vivizzz007/vivi-music). It preserves the upstream Git history and upstream remote while applying AURA branding and interface changes.

## Features

- Android-native Kotlin and Jetpack Compose interface
- Local music library, playlists, queue, playback history, and downloads
- Online search and streaming integrations inherited from upstream
- Spotify, YouTube/InnerTube, JioSaavn, Last.fm, Shazam, lyrics providers, and motion artwork integrations
- Media3 playback service, Room persistence, equalizer, and synchronized lyrics
- Monochrome AURA visual identity

Streaming integrations are intentionally retained. Availability depends on each provider and its service policies.

## Build

- JDK 21
- Android SDK configured for the project
- Run `./gradlew :app:assembleUniversalGmsDebug` for the universal GMS debug APK.

## Source and attribution

This project is based on [vivi-music by vivizzz007](https://github.com/vivizzz007/vivi-music). The original project and its contributors retain their copyright. AURA changes are maintained in this fork.

The project is licensed under GPL-3.0, subject to the additional terms and exceptions stated in the upstream `LICENSE` file. Keep the license and notices with every distribution; review the Musixmatch-specific terms before redistributing that component.

## Contributions

Issues and contributions: https://github.com/javieryohance10-blip/vivi-music
