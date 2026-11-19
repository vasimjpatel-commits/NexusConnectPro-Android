# Build and test NexusConnect Pro

This branch is the current **debug-build candidate**, not a production release. It includes the existing WebView chat/video/GPS app behind the custom account-authentication screen. The Android build workflow packages a debug APK and uploads it as a GitHub Actions artifact.

## Build the APK in GitHub

1. Open the repository and select branch `testable/android-debug-build`.
2. Open **Actions** → **Build NexusConnect Pro debug APK**.
3. Select **Run workflow** and run it on this branch.
4. Wait for the workflow to finish. A green result means Gradle compiled and packaged the APK.
5. Open the completed run and download the `NexusConnect-Pro-debug` artifact.
6. Extract the ZIP and install `app-debug.apk` on the OPPO phone. Allow installation from the browser/file manager if Android asks.

The artifact is a debug APK. It is not Play Store signed.

## Important: configure authentication before signing in

The current Android build uses `AUTH_BASE_URL=https://api.example.com` as a safe placeholder. It will compile, but login/register and phone OTP cannot work until a real backend is deployed.

1. Deploy the Node.js API and PostgreSQL by following [backend/README.md](../backend/README.md).
2. Configure a strong `JWT_ACCESS_SECRET`, `DATABASE_URL`, and Twilio Verify credentials in the hosting provider's secret settings. Do not commit secrets to GitHub.
3. Run the database migration and verify `GET https://YOUR_API_HOST/health` returns `{"ok":true}`.
4. Change `AUTH_BASE_URL` in `app/build.gradle.kts` to your deployed HTTPS API URL (no trailing slash), commit the change, and rerun the workflow.
5. SMS OTP works only when Twilio Verify is configured and funded. Email OTP is not implemented in this build.

Do not use `localhost` as the API URL on a physical phone; it refers to the phone itself. Do not enable cleartext HTTP for a production server.

## What this build currently includes

- Email/password registration and sign-in.
- Phone-number SMS OTP via Twilio Verify.
- Encrypted local refresh-token storage and refresh on launch.
- Existing WebView chat, video-call and GPS screens after authentication.
- Camera, microphone and location permissions requested when those features are used.

## Not yet implemented / not yet verified

- Email OTP, profile-photo upload, online/last-seen presence and contact sync are not runtime features yet. PR #6 is a design specification only.
- This is still a WebView app, not the planned full native Kotlin rewrite.
- Existing PeerJS messaging/calls do not have server-authorized friend relationships or independently reviewed end-to-end encryption.
- A successful GitHub build does not prove OTP delivery, camera/microphone, GPS, chat or calls work on the OPPO phone. Test each on-device.

## Test checklist

- [ ] Install and launch the APK.
- [ ] Register a new email account.
- [ ] Sign out and sign back in.
- [ ] Force-stop and relaunch; confirm session restoration.
- [ ] Request and verify a real SMS OTP.
- [ ] Test chat between two devices/accounts.
- [ ] Test video call with camera and microphone permission.
- [ ] Test GPS permission and live location behavior.
- [ ] Sign out and confirm the login screen returns.
