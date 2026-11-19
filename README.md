# NexusConnect Pro — Android Studio Project

NexusConnect Pro keeps the existing WebView application for P2P chat, video calling and live GPS, and now adds a custom account-authentication gateway.

## Authentication added

- Email/password registration and login.
- Phone-number SMS OTP login through Twilio Verify.
- Short-lived signed access JWTs and rotating opaque refresh tokens.
- Refresh-token hashes stored server-side; passwords stored as bcrypt hashes.
- Android tokens stored using AndroidX EncryptedSharedPreferences.
- Sign-out revokes the refresh token and clears local encrypted storage.
- WebView chat/video/GPS screens open only after the authentication screen.

**Important scope:** this authenticates the app account, not each PeerJS peer. The existing app still uses public PeerJS signaling and does not gain authenticated private messaging or end-to-end encryption from this change. See `backend/README.md`.

## Repository layout

- `app/src/main/java/com/nexusconnect/pro/AuthActivity.kt` — email and OTP UI.
- `AuthApi.kt` — HTTPS API client.
- `AuthTokenStore.kt` — encrypted on-device token storage.
- `MainActivity.kt` — existing WebView host plus sign-out handling.
- `app/src/main/assets/index.html` — existing chat/video/GPS interface with sign-out control.
- `backend/` — Node.js authentication API, PostgreSQL migration and setup guide.

## Configure the backend first

Follow [backend/README.md](backend/README.md). You need Node.js 20+, PostgreSQL, a publicly reachable HTTPS API, and Twilio Verify credentials for SMS OTP. Copy `backend/.env.example` to `.env` on the server only. Never commit real secrets.

The API endpoints are:
- `POST /v1/auth/register`
- `POST /v1/auth/login`
- `POST /v1/auth/otp/request`
- `POST /v1/auth/otp/verify`
- `POST /v1/auth/refresh`
- `POST /v1/auth/logout`
- `GET /v1/me`

## Build the Android APK

1. Open the project in Android Studio (JDK 17; Android SDK Platform 35).
2. Open `app/build.gradle.kts` and replace `https://api.example.com` in `AUTH_BASE_URL` with your deployed HTTPS API base URL (no trailing slash).
3. Sync Gradle. The AndroidX Security Crypto dependency will be downloaded.
4. Build → Build Bundle(s) / APK(s) → Build APK(s).
5. Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.
6. Install on your Android device. Launch NexusConnect Pro; choose Email or Phone OTP. After successful sign-in, the existing chat/video/GPS screen opens.

Do not use a localhost URL for a physical phone. For local testing, use a reachable LAN HTTPS endpoint or a properly configured development tunnel. Cleartext HTTP is disabled by the Android manifest.

## Test checklist

- Register with a new email, sign out, sign in again.
- Request and verify an SMS OTP using a real configured Twilio Verify service.
- Force-stop and relaunch: a valid refresh token should restore the session.
- Revoke a refresh token in the database and verify that session restore is rejected.
- Sign out while online and offline; local tokens must be cleared in either case.
- Verify camera, microphone and location permissions and existing P2P features after login.

## Before public release

Add email verification and recovery, robust OTP abuse controls, account deletion, privacy/retention terms, monitoring, database backups and signing-key rotation. Review the existing HTML's handling of untrusted chat messages (avoid assigning message content to `innerHTML`). Have the backend authorize friend relationships and signaling before claiming peer identity is verified or chats are private.
