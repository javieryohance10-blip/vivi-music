# AURA Music migration

AURA Music is a native Android music player derived from the upstream VIVI Music project. It plays the user's own local audio files and keeps the upstream online provider integrations.

## Source and licensing

- Upstream: https://github.com/vivizzz007/vivi-music
- This repository remains a fork; upstream history and the upstream remote must be preserved.
- The project remains under GPL-3.0. Keep the upstream LICENSE and applicable source notices intact.
- Preserve third-party notices and comply with any additional component-specific terms before redistributing builds.

## Scope decision (2026-11-04)

The owner decided that the streaming and online-provider modules are **kept**. Do not remove them or their manifest entry points:
`:innertube`, `:jiosaavn`, `:spotify`, `:lyricsProvider`, `:lastfm`, `:shazamkit`, `:kizzy`, `:artistvideo`, `:Motionartwork`.

This replaces the earlier "local-only, remove remote providers" requirement. AURA is a hybrid: local files and the existing online sources live side by side.

## Product requirements

- Native Android application using the existing Kotlin, Jetpack Compose, Media3, Room, and dependency-injection architecture where compliant.
- Local library: MediaStore and Storage Access Framework access, folder browsing, playlists, M3U import/export, embedded and local LRC lyrics, persistent queue and playback position. Local playback must work fully offline.
- Existing online sources keep working as upstream implements them.
- Monochrome black, white, and charcoal visual system; simple AURA branding.
- No analytics, telemetry, remote crash reporting, or in-app OTA updater that points at the upstream project.
- Distribute through GitHub Releases with the corresponding source. The streaming integrations use unofficial APIs, so the app is not intended for Google Play.

## Staged implementation status

- [x] Confirm upstream fork and preserve existing history.
- [x] Verify the upstream CI debug build succeeds before edits (workflow artifact: `app-universal-gms-debug`).
- [x] Change the launcher-visible app name to AURA Music.
- [ ] Replace remaining VIVI identity, icons, and visual theme.
- [x] Decision: keep remote-provider integrations and their manifest entry points.
- [ ] Implement and verify local library scanning, folder browsing, playlists, lyrics, and persistence alongside the existing online sources.
- [ ] Remove telemetry, remote crash reporting, and the upstream OTA updater.
- [ ] Audit permissions, network access, dependencies, and third-party license obligations.
- [ ] Add tests and run the full required build matrix.
- [ ] Publish source and a verified APK artifact together.

Do not describe an APK as an AURA release until the AURA-specific changes have passed CI and the artifact has been inspected.
