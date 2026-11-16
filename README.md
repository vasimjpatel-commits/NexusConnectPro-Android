# NexusConnect Pro — Android Studio Project

This project wraps the NexusConnect-Pro HTML app (chat, video call & live GPS) inside an Android WebView with proper runtime permissions for camera, microphone and location.

## Project is complete and ready to open

- `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/nexusconnect/pro/MainActivity.kt`
- `app/src/main/assets/index.html`  ← full web app
- `NexusConnectPro-Android-Studio-Project.zip` (original package)

## How to build the APK

1. Clone this repository (or download the ZIP).
2. Open **Android Studio** → **Open** → select the folder that contains `settings.gradle.kts`.
3. Let Gradle sync. Install Android SDK Platform 35 if prompted.
4. Build → Build Bundle(s) / APK(s) → **Build APK(s)**.
5. The debug APK is created at:
   `app/build/outputs/apk/debug/app-debug.apk`
6. Copy the APK to your phone and install it (allow installation from that source).

## Requirements
- Android 7.0 (API 24) or newer
- Internet connection (Tailwind, Leaflet, PeerJS, Lucide load from CDNs)
- Grant Camera, Microphone and Location when the app asks

## Notes
- This is a WebView wrapper, not a full native rewrite.
- Peer-to-peer features use PeerJS public signaling.
- Test thoroughly on your target device (especially battery / background restrictions on OPPO / ColorOS).

**Repo**: https://github.com/vasimjpatel-commits/NexusConnectPro-Android
