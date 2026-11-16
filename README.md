# NexusConnect Pro — Android Studio Project

This project wraps your NexusConnect-Pro HTML app in an Android WebView.
It includes runtime handling for camera, microphone and location permissions.

## Quick start (build the APK)

1. Clone this repository **or** download the `NexusConnectPro-Android-Studio-Project.zip` from this repo.
2. Install [Android Studio](https://developer.android.com/studio).
3. Open Android Studio → **Open** → select the project folder (the one that contains `settings.gradle.kts`).
4. Allow Gradle sync and install Android SDK Platform 35 if prompted.
5. Build → Build Bundle(s) / APK(s) → **Build APK(s)**.
6. The debug APK appears at:
   `app/build/outputs/apk/debug/app-debug.apk`
7. Copy the APK to your phone and install it (allow unknown sources if asked).

## Requirements
- Android 7.0 (API 24) or newer
- Internet access (Tailwind, Leaflet, PeerJS, Lucide load from CDNs)
- Camera / Microphone / Location permissions when using those features

## Notes
- This is a **WebView wrapper**, not a full native rewrite.
- Chat, video call and GPS logic live in `app/src/main/assets/index.html`.
- PeerJS uses its public signaling service.
- Test thoroughly on target devices (especially OPPO battery / background restrictions) before distributing.

The complete source is also available as `NexusConnectPro-Android-Studio-Project.zip` in this repository.
