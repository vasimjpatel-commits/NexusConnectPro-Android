# NexusConnect Pro — Native Android Implementation Plan

**Target:** Fully native Kotlin Android application  
**Preserve:** Custom email/password + phone OTP authentication, chat, voice/video calling, live GPS  
**APK aspiration:** Approximately 90 MB only if real, shipped native capabilities justify it  
**Status:** Planning document. This is not a claim that the native migration or 90 MB build has been completed.

## 1. Current repository baseline

The current app is a Kotlin Activity hosting `app/src/main/assets/index.html` in a WebView. The HTML supplies the chat interface, PeerJS connections, video-call UI, GPS/map UI and loads Tailwind, Leaflet, PeerJS and Lucide from CDNs. The Android host requests camera, microphone and location permissions.

The custom authentication work adds:
- Native email/password registration and login.
- Phone OTP through the backend's Twilio Verify integration.
- A custom Node.js/PostgreSQL API.
- Short-lived access JWTs and rotating refresh tokens.
- Encrypted local token storage.
- A native authentication Activity and sign-out route.

The existing P2P layer is still PeerJS-based. App login does not prove that a PeerJS ID belongs to the signed-in account, authorize friend relationships, or provide end-to-end encryption. The native migration must close that identity/signaling gap before describing conversations as private or authenticated.

## 2. Target architecture

Move the user-facing application to native Kotlin. Keep the custom backend as the account authority and evolve it into the authorization and messaging authority.

### Android modules / packages

- `:app`: application shell, navigation, dependency wiring and lifecycle.
- `:core:network`: Retrofit/OkHttp client, API models, TLS configuration, bearer-token interceptor and single-flight refresh handling.
- `:core:auth`: registration, email login, OTP, session restoration, logout and account state.
- `:core:database`: Room entities, DAOs, migrations and encrypted-at-rest policy for locally cached sensitive data.
- `:core:designsystem`: Compose theme, typography, reusable controls and accessibility.
- `:feature:chat`: one-to-one chat, groups, delivery/read states, local history and attachment messages.
- `:feature:calls`: native WebRTC audio/video calls, call lifecycle, device selection and call notifications.
- `:feature:media`: camera/gallery/document selection, upload/download, thumbnails and playback.
- `:feature:location`: foreground location sharing, permission handling, map display and sharing expiry.
- `:feature:contacts`: explicit contact permission, normalized phone matching and user-controlled discovery.
- `:feature:profile`: profile, avatar, privacy and account settings.
- `:feature:notifications`: FCM push notifications and notification routing.

Use Jetpack Compose for new screens, Navigation Compose for navigation, Room for local data, WorkManager for reliable deferred uploads/sync, CameraX for camera capture, Android Photo Picker / Storage Access Framework for user-selected media, and Media3 for playback. Use a maintained native WebRTC implementation for calls. Select exact dependency versions only after compatibility/build verification.

### Backend responsibilities

Retain the custom auth API and PostgreSQL. Extend it with:
- User profiles and verified email/phone state.
- Device/session management and refresh-token revocation.
- Server-authorized friend requests and contact discovery.
- Authenticated WebSocket or equivalent message transport.
- Message persistence, delivery acknowledgements and synchronization.
- Upload authorization and short-lived media download URLs.
- Call signaling that binds a verified app user and device to a call.
- Push notification token registration and revocation.

The server must validate access JWT issuer, audience, signature and expiry on every protected operation. Never trust a user ID, peer ID, sender ID or role supplied only by the client.

## 3. Feature preservation and replacement map

| Existing capability | Native destination | Migration requirement |
|---|---|---|
| Email/password and phone OTP | Native Compose auth screens + existing API | Preserve endpoints and token rotation; improve refresh race handling |
| Chat UI and PeerJS data messages | Native Compose chat + authenticated backend transport | Preserve conversations only after server-side user/peer identity mapping |
| PeerJS video calls | Native WebRTC call feature + authenticated signaling | Add call invitations, authorization, TURN, lifecycle and call notifications |
| GPS sharing / Leaflet map | Location feature + native map SDK | Keep explicit consent, foreground-service rules and sharing expiry |
| Image/file sharing | Media feature | Add authenticated upload, size/type validation and progress/resume |
| Sign out | Native session manager | Revoke refresh session and clear local credentials/cache according to policy |

Do not remove the WebView implementation until each replacement feature passes its acceptance tests. During migration, keep the old screen behind a developer-only migration flag, not as a permanent mixed production navigation path.

## 4. Authentication and security design

1. Use the existing custom API for registration, email/password login, OTP request/verification, refresh and logout.
2. Store refresh credentials using Android Keystore-backed encrypted storage. Keep access tokens in memory where practical; do not put tokens in URLs, logs, analytics, clipboard or WebView JavaScript.
3. Use a single-flight refresh mechanism so concurrent 401 responses do not rotate the same refresh token multiple times.
4. On refresh failure, clear the local session and return to sign-in.
5. Add server-side email verification, account recovery, OTP per-phone/IP abuse controls, device/session listing and account deletion before public release.
6. Use HTTPS only; keep signing secrets, Twilio credentials and database credentials in server secret storage, never in Git or the APK.
7. Require authenticated friend relationships and server-side authorization for every message, attachment, location share and call signal.
8. Define and implement an end-to-end encryption protocol only with a reviewed design and audited libraries. TLS or WebRTC transport encryption alone must not be advertised as end-to-end encrypted chat.
9. Sanitize and validate all untrusted content. Render message text as text, not HTML. Validate attachment MIME types, sizes and ownership server-side.
10. Add privacy controls for contacts, location, read receipts, online status, notifications and media retention.

## 5. Phased implementation

### Phase 0 — Stabilize the current baseline
- Build and install the current authentication branch.
- Verify backend registration, email login, OTP, refresh rotation and logout.
- Record current APK/AAB sizes and current feature behavior.
- Fix build/runtime defects before adding modules.
- Add automated API tests and a repeatable CI Android build.

**Exit criteria:** clean debug and release builds; successful auth flows; baseline chat/video/GPS regression checklist documented.

### Phase 1 — Native foundation
- Convert the Android app to a modular Gradle structure.
- Add Compose, Navigation, dependency injection, Retrofit/OkHttp, Room and WorkManager.
- Add design system, app state, error/loading states and accessibility.
- Move authentication UI to Compose while preserving API contract and encrypted session storage.

**Exit criteria:** native launcher, navigation and auth work without opening the WebView.

### Phase 2 — Native profiles and chat
- Add profiles, avatars, friend requests and server-authorized identity mapping.
- Implement message API/WebSocket, persistence, pagination, retry and delivery/read state.
- Add one-to-one and group chat, blocking/reporting and offline synchronization.
- Replace the PeerJS data-channel chat path only after backend messaging is live.

**Exit criteria:** two test accounts can exchange messages; server rejects unauthorized senders/recipients; offline/online sync is reliable.

### Phase 3 — Media and attachments
- Add Android Photo Picker, CameraX capture, document picker and upload queue.
- Add server-authorized upload/download, progress, cancellation, retry and thumbnails.
- Add image viewer, video player (Media3), audio playback and document preview.
- Add quotas, malware scanning strategy, file-size/type validation and retention rules.

**Exit criteria:** supported media can be sent, received, resumed and safely opened on two devices.

### Phase 4 — Native voice/video calling
- Integrate native WebRTC with authenticated call signaling.
- Add microphone/camera selection, speaker/Bluetooth routing, call states, missed calls and notifications.
- Deploy STUN/TURN infrastructure and test restrictive mobile networks.
- Implement background/foreground call behavior according to Android foreground-service and notification requirements.

**Exit criteria:** account-authorized calls work on separate networks, recover from interruptions and release camera/microphone resources correctly.

### Phase 5 — Native GPS and contacts
- Replace Leaflet-in-WebView with a native map component.
- Add opt-in live location sharing, visible sharing state, expiry and stop-sharing controls.
- Use location only while the user has granted the relevant permission and sharing is active.
- Add contact discovery only with explicit permission, clear disclosure and server-side privacy controls.

**Exit criteria:** location is not collected in the background without a valid user-visible feature/permission basis; users can stop and expire sharing.

### Phase 6 — Hardening, release and size verification
- Add unit, API integration, UI, instrumentation, security and network-failure tests.
- Test supported Android versions and actual OPPO hardware.
- Enable R8 shrinking/obfuscation, resource shrinking and release signing.
- Build ABI-specific APKs and an AAB; inspect native libraries and assets using APK Analyzer.
- Verify privacy policy, data safety declarations, permissions, signing key backup and staged rollout.

**Exit criteria:** release candidate passes regression/security checks; artifact sizes are measured and explained; no artificial padding or unused SDKs.

## 6. Illustrative 90 MB size budget

This is a planning envelope for a possible universal APK, not a prediction or a requirement. Actual size must be measured after choosing versions, ABIs and resources. A Play-delivered device-specific APK may be substantially smaller.

| Packaged content | Illustrative compressed contribution |
|---|---:|
| Kotlin/Compose app code and resources | 8 MB |
| Native WebRTC binaries across included ABIs | 25 MB |
| Media playback / processing native components | 12 MB |
| Maps and required offline map resources | 8 MB |
| Original bundled sticker / UI media packs | 12 MB |
| Fonts, sounds and other app-owned resources | 5 MB |
| Other required native/runtime dependencies | 10 MB |
| Growth / packaging variance allowance | 10 MB |
| **Illustrative total** | **90 MB** |

Do not bundle a whole-world map, unused codecs, multiple ABI copies in a device-specific APK, large stock media libraries or duplicate resources merely to hit the number. Prefer downloadable/on-demand media and map packs when appropriate. If the complete, optimized app is 45 MB or 120 MB, ship the real measured size rather than padding or disabling useful optimization.

## 7. Build and artifact strategy

- Use JDK 17 and the Android Gradle Plugin / Kotlin versions selected by the project after a successful compatibility check.
- Keep compile/target SDK aligned with current Google Play requirements at release time.
- Produce a universal APK for direct internal testing only if needed.
- Produce ABI-specific APKs for diagnostics and an Android App Bundle (AAB) for Google Play.
- Use `splits.abi` only when producing APKs; use Play's app bundle delivery for device targeting.
- Enable R8 and resource shrinking in release after keep rules and WebRTC reflection/native loading have been tested.
- Keep debug symbols / native symbol files for crash diagnosis.
- Use reproducible CI builds and protect signing credentials with GitHub Actions secrets or a secure signing service.

## 8. Required test matrix

- Authentication: new email registration, wrong password, OTP expired/invalid, resend abuse limits, refresh rotation, revoked session, logout offline/online.
- Identity: spoofed sender, unauthorized recipient, blocked user, stale device token, revoked account.
- Messaging: reconnect, duplicate send, ordering, offline queue, pagination, attachment access control.
- Calls: permission denial, incoming call while app backgrounded, Bluetooth, network handoff, TURN-only network, camera/mic interruption.
- GPS: permission denial, approximate location, sharing expiry, stop-sharing, app process death.
- Media: large file, unsupported MIME, interrupted upload, low storage, malicious filename and unauthorized download.
- Performance: cold start, memory pressure, battery use, low-end device, thermal throttling.
- Release: signed APK/AAB install/update, backup/restore behavior, Play pre-launch checks.

## 9. Current known gaps and cautions

- The current Android UI is still WebView-based; the plan above is not yet implemented.
- The existing HTML loads Tailwind, Leaflet, PeerJS and Lucide from public CDNs. Native migration should remove runtime CDN dependence for core application functionality.
- Existing PeerJS peer IDs are not authenticated account identities. Do not claim account-secure messaging until the server authorizes peer connections and message routing.
- The auth backend and Android integration need real deployment configuration and build/test validation. Example API host and Twilio values are placeholders.
- A 90 MB artifact is not proof of quality, security or feature completeness.

## 10. Definition of done

NexusConnect Pro is considered migrated when all primary screens are native Kotlin, the old WebView is no longer required for core chat/call/GPS functions, authentication remains functional, messages and calls are server-authorized, media/location controls are tested, and a signed release AAB plus tested device APKs are produced. The final artifact size is reported from build outputs, not targeted by padding.
