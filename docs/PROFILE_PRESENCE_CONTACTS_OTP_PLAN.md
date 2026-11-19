# NexusConnect Pro: profiles, presence, contact sync and OTP sign-in

## Product scope

Add these account and discovery features while retaining existing chat, video-call and GPS during migration:

- Sign in with a phone number OR email address using a one-time code (OTP).
- Keep existing email/password login during transition.
- User profile with display name and optional profile photo.
- Online now, last seen timestamp and privacy controls.
- Permission-gated contact discovery with confirmation before upload and an option to revoke sync.
- Preserve simple branding: Vasim / NexusConnect Pro / Designed by Vasim J Patel.

## Repository baseline

The custom-authentication branch currently contains a Node.js/Express + PostgreSQL API, email/password registration/login, Twilio Verify phone OTP, short-lived JWT access tokens, rotating refresh tokens, and an Android WebView app with chat/video/GPS behind authentication.

Not yet implemented: email OTP, profile-photo storage, contact discovery, presence/last-seen, server-authorized friend relationships or authenticated PeerJS signaling. This document is a technical plan, not a claim these features are already working.

## Sign-in flow

1. First screen offers Continue with phone and Continue with email.
2. User enters an E.164 phone number or email address.
3. API sends a short-lived, single-use OTP through the configured SMS or email provider.
4. User enters the code; API verifies it, applies attempt limits and issues the existing access/refresh token pair.
5. For a new verified identifier, ask for a display name and create the account.
6. Linking an identifier to an existing account requires a separate secure confirmation; never silently merge accounts.
7. Keep email/password login until OTP migration and recovery are tested.

### API additions

- POST /v1/auth/email-otp/request
- POST /v1/auth/email-otp/verify
- Retain POST /v1/auth/otp/request and /otp/verify for phone SMS.
- Apply throttles by IP, identifier and device; uniform responses to reduce account enumeration; OTP expiry, one-time consumption, bounded attempts, resend cooldown and abuse monitoring.
- Email OTP requires a configured transactional email provider and verified sender domain. Never log OTP values.

## Profile and photo

- GET /v1/profile/:userId returns only profile fields permitted by privacy settings.
- PATCH /v1/me/profile updates display name and settings.
- POST /v1/me/avatar/upload-url returns a short-lived, scoped object-storage upload URL.
- POST /v1/me/avatar/complete validates object metadata and sets avatar key.
- DELETE /v1/me/avatar removes avatar.
- Use private object storage and short-lived signed URLs or authenticated image proxy. Validate actual image bytes/MIME, size and dimensions; re-encode images and strip EXIF/GPS metadata. Do not store arbitrary user-supplied URLs.

## Online and last-seen presence

- Use authenticated WebSocket connections (or equivalent realtime gateway).
- Client sends heartbeats while app is active; server expires presence after missed heartbeats.
- Keep transient online state in Redis with TTL; persist last_seen_at only when a user becomes offline.
- Never trust client-supplied online flags or timestamps.
- Privacy options: Everyone, Contacts, Nobody; separate show-online and last-seen settings. Enforce block rules server-side.
- Do not use background GPS or continuous battery-heavy polling. Presence is not location sharing.

## Contact sync and discovery

1. Explain why permission is needed and request Android READ_CONTACTS only after the user taps Find contacts.
2. Preview the data and ask for confirmation before any upload.
3. Read only required phone/email identifiers; normalize phone numbers to E.164 and normalize email.
4. Upload only after explicit opt-in over HTTPS. Do not upload names, notes, postal addresses, photos or entire Android contact records.
5. Match against accounts that opted into discoverability. Return only matched account IDs and profile fields allowed by privacy settings.
6. Offer sync off/revoke and server-side deletion of retained sync data; publish retention/deletion policy.
7. Prevent account enumeration with strict batch limits, rate limits and abuse detection. For stronger privacy, use a reviewed private-set-intersection/contact-discovery design instead of storing raw address books.

Suggested endpoints:
- POST /v1/contacts/match — authenticated, consented batch lookup with strict size limits.
- DELETE /v1/contacts/sync — revoke and delete retained sync data.
- GET /v1/contacts/matches — retrieve current matches.

## Database additions

Add user_profiles (user_id, display_name, avatar_object_key, discoverable, created_at, updated_at); user_privacy (user_id, last_seen_visibility, online_visibility, discoverability, updated_at); user_blocks (owner_user_id, blocked_user_id, created_at); and contact_sync_consents (user_id, granted_at, revoked_at, policy_version).

If contact matching needs server-side retention, store normalized identifiers only under a reviewed keyed-hash or PSI design, with retention limits and deletion on revoke. Never put raw contact lists in logs. Keep online state in Redis with TTL, not a permanent database online=true field.

## Android implementation

- Profile screen with avatar picker using Android Photo Picker (avoid broad storage permission).
- Contact-sync screen with rationale, runtime READ_CONTACTS permission, preview, explicit confirmation, progress, results and revoke action.
- Shared OTP screen for phone/email with resend cooldown, validation and accessible errors.
- Presence repository opens an authenticated realtime connection only while app is active and closes it according to lifecycle/logout policy.
- Cache profile metadata safely; keep refresh tokens in Android Keystore-backed encrypted storage.
- Keep camera/microphone/location permissions separate and request only when the user invokes those features.

## Chat and call security dependency

Profiles and presence do not secure the current PeerJS P2P layer. Before claiming private/authenticated chat:
- Establish server-authorized friend relationships and block enforcement.
- Issue short-lived, single-use call/signaling grants after checking both users may connect.
- Bind peer IDs to authenticated accounts; random PeerJS IDs are not identities.
- Protect message delivery and media signaling with authorization and abuse controls.
- Independently design and review end-to-end encryption if the product claims E2EE. WebRTC alone is not proof of E2EE chat.

## Delivery phases and acceptance criteria

1. Account OTP: phone and email OTP work with real providers; expiry, replay, throttling and recovery tests pass.
2. Profiles: user can set/update/remove avatar and display name; users cannot edit another profile.
3. Presence: online follows authenticated heartbeats; last-seen settings and block rules are enforced server-side.
4. Contacts: permission is optional; nothing uploads before consent; only minimal identifiers are sent; discoverability is respected; revoke deletes retained sync data.
5. Integration: existing chat, calls and GPS remain working; logout closes realtime connections and revokes session.
6. Release: backend tests, Android build, privacy/security review, provider configuration and on-device tests pass.

## Deployment requirements

- SMS provider: Twilio Verify credentials and verified service.
- Email provider: transactional SMTP/API credentials, verified sender domain, SPF/DKIM/DMARC.
- PostgreSQL migrations and backups.
- Redis with authentication/TLS for presence.
- Private S3-compatible object storage (or equivalent) with signed uploads.
- Production HTTPS, secret manager, rate limiting, monitoring, retention policy and privacy notice.

Never commit provider credentials, OTP secrets, database passwords or production tokens.
