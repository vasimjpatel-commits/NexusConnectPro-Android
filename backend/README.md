# NexusConnect Pro custom authentication API

Node.js 20+ / PostgreSQL service. Routes: POST /v1/auth/register, /login, /otp/request, /otp/verify, /refresh, /logout; GET /v1/me and /health.

Email passwords are bcrypt-hashed (cost 12). Access tokens are 15-minute HS256 JWTs. Refresh tokens are 48-byte random opaque values; only SHA-256 hashes are stored. Refresh rotation revokes the previous token in the same database transaction. Phone OTP uses Twilio Verify; it is not simulated, and phone login remains unavailable until valid Twilio credentials are configured.

## Local setup
1. Install Node 20+ and PostgreSQL 14+.
2. Create a database and a DB role with only the required permissions.
3. Run `cd backend && npm install`.
4. Copy `.env.example` to `.env`; configure DATABASE_URL and a cryptographically random JWT_ACCESS_SECRET of at least 32 bytes. Configure the three TWILIO_* values for SMS.
5. Run `npm run migrate`, then `npm start`.
6. Test `GET /health`. Deploy behind HTTPS; never expose PostgreSQL publicly.

## Production security
Keep .env out of Git; use hosting secret storage, TLS, database backups, least privilege, monitoring and SMS spend/fraud controls. Add email verification, account recovery, per-phone/IP OTP throttles, privacy/retention policy and key rotation before public launch. Never log credentials, OTPs or tokens.

## Important scope
This service authenticates accounts only. Existing PeerJS calls/chat/GPS remain unchanged and still use public signaling and peer IDs. This patch does NOT make P2P messages authenticated or end-to-end encrypted. Implement server-authorized friend relationships and authenticated signaling before describing chat as private.
