# AURA Music migration

AURA Music is a local-audio-focused derivative of the upstream VIVI Music project.

## Source and licensing

- Upstream: https://github.com/vivizzz007/vivi-music
- This repository remains a fork; upstream history and the upstream remote must be preserved.
- The project remains under GPL-3.0. Keep the upstream LICENSE and applicable source notices intact.
- Preserve third-party notices and comply with any additional component-specific terms before redistributing builds.

## Product requirements

- Native Android application using the existing Kotlin, Jetpack Compose, Media3, Room, and dependency-injection architecture where compliant.
- Local user-owned audio only. Do not download, cache, scrape, or stream audio from third-party services.
- MediaStore and Storage Access Framework library access, folder browsing, playlists, M3U import/export, embedded and local LRC lyrics, persistent queue and playback position.
- Monochrome black, white, and charcoal visual system; simple AURA branding.
- No analytics, telemetry, remote crash reporting, or in-app OTA updater.
- Optional online lyrics must be explicitly opt-in and isolated from local playback; otherwise the app should work offline.

## Staged implementation status

- [x] Confirm upstream fork and preserve existing history.
- [x] Verify the upstream CI debug build succeeds before edits (workflow artifact: `app-universal-gms-debug`).
- [x] Change the launcher-visible app name to AURA Music.
- [ ] Replace remaining VIVI identity, icons, and visual theme.
- [ ] Remove remote-provider integrations and their manifest entry points.
- [ ] Implement and verify local library scanning, folder browsing, playlists, lyrics, and persistence.
- [ ] Audit permissions, network access, dependencies, and third-party license obligations.
- [ ] Add tests and run the full required build matrix.
- [ ] Publish source and a verified APK artifact together.

Do not describe an APK as an AURA release until the AURA-specific changes have passed CI and the artifact has been inspected.
