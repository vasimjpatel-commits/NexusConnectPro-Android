NEXUSCONNECT PRO — ANDROID STUDIO PROJECT

This project wraps your supplied NexusConnect-Pro.html in an Android WebView.
It includes runtime handling for camera, microphone and location permissions.

BUILD THE APK
1. Extract this ZIP on a Windows/Mac/Linux computer.
2. Install Android Studio (https://developer.android.com/studio).
3. Open Android Studio > Open, then select the extracted NexusConnectPro-Android folder.
4. Allow Gradle sync and install the Android SDK Platform 35 if Android Studio asks.
5. Connect your OPPO phone with USB debugging enabled, or use Build > Build Bundle(s) / APK(s) > Build APK(s).
6. The debug APK is created at:
   app/build/outputs/apk/debug/app-debug.apk
7. Copy app-debug.apk to your OPPO phone and tap it to install. Allow installation from that source if Android asks.

PHONE REQUIREMENTS
- Android 7.0 (API 24) or newer.
- Internet access is required: the HTML loads Tailwind, Leaflet, PeerJS and Lucide from online CDNs.
- Allow camera, microphone and location when using those features.
- For real peer-to-peer tests, use two devices and exchange the generated PeerJS IDs.

IMPORTANT LIMITATIONS
- This is a WebView APK wrapper, not a full native Kotlin rewrite.
- Chat/video/GPS behavior is the behavior already implemented in the supplied HTML.
- The HTML's solo "Aarav" response is a demo fallback, not an AI service.
- PeerJS uses its configured signaling service; this project does not include a private signaling server or backend.
- Test camera, microphone, GPS, incoming calls, background/resume behavior and OPPO battery restrictions before distributing.
