# NexusConnect Pro — Android Studio Project

This project wraps the NexusConnect-Pro HTML app (chat, video call & live GPS) inside an Android WebView with proper runtime permissions for camera, microphone and location.

## Repository Contents

- Proper Android Studio project structure
  - `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`
  - `app/build.gradle.kts`
  - `app/src/main/AndroidManifest.xml`
  - `app/src/main/java/com/nexusconnect/pro/MainActivity.kt`
- `index.html` — the full web app
- `NexusConnectPro-Android-Studio-Project.zip` — complete original package (includes assets)

## How to build the APK

1. Clone this repo **or** download & extract the ZIP from this repository.
2. Open **Android Studio** → Open → select the folder that contains `settings.gradle.kts`.
3. Let Gradle sync. Install Android SDK Platform 35 if prompted.
4. **Important** – make sure the HTML is in the assets folder:
   ```
   mkdir -p app/src/main/assets
   cp index.html app/src/main/assets/
   ```
   (If you extracted the original ZIP this step is already done.)
5. Build → Build Bundle(s) / APK(s) → **Build APK(s)**.
6. The debug APK is created at:
   `app/build/outputs/apk/debug/app-debug.apk`
7. Copy the APK to your phone and install it (allow installation from that source).

## Requirements
- Android 7.0 (API 24) or newer
- Internet connection (Tailwind, Leaflet, PeerJS, Lucide load from CDNs)
- Grant Camera, Microphone and Location when the app asks

## Notes
- This is a WebView wrapper, not a full native rewrite.
- Peer-to-peer features use PeerJS public signaling.
- Test thoroughly on your target device (especially battery / background restrictions on OPPO / ColorOS).

**Repo**: https://github.com/vasimjpatel-commits/NexusConnectPro-Android
