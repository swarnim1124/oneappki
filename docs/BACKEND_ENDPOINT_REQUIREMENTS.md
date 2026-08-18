# Backend / External Requirements

This file is referenced by comments throughout the codebase ("see Backend Endpoint
Requirements") but did not exist on disk before this pass - it's created here to
match what those comments already expect, and to consolidate every place the Android
client is blocked on something only the backend, Google Cloud Console, or Google Play
Console can supply. Nothing in this list should be faked client-side; each item below
is a real, wired integration point waiting on the external piece named.

## 1. Token refresh contract (blocks: session refresh)

`sdk/XscNetworkSDK/src/main/java/com/xsc/sdk/network/internal/TokenAuthenticator.kt`
(`performRefresh()`) is real retry/mutex/backoff logic wired to `okhttp3.Authenticator`,
but the actual dispatcher `mod`/`subMod`/`action` for exchanging a refresh token has
never been confirmed against the live backend (`m_AAA`'s `session` action handles
login; nothing has been confirmed for refresh). Until this is defined, expired access
tokens force re-login rather than silently refreshing - the safe failure mode, but a
real UX cost.

**Needed from backend:** the exact `mod`/`subMod`/`action`/`actionType` and
request/response shape for refreshing an access token from a refresh token.

## 2. Activity / notification feed (blocks: Dashboard notifications)

No dispatcher module exists for an activity or notification feed (`m_notification`,
`m_activity`, or equivalent) - confirmed by cross-referencing every `*Endpoint.kt` in
this codebase against the live dispatcher. `feature/dashboard`'s
`NotificationRepository`/`NotificationRepositoryImpl` are real and wired end to end;
until a contract exists, they honestly return an empty list rather than fabricated
content.

**Needed from backend:** a `view` action returning notification rows
(id/title/message/timestamp/category/isRead), filtered server-side to the caller's
JWT, plus a write action to mark rows read.

## 3. reCAPTCHA execution (blocks: bot-check on login)

`sdk/XscNetworkSDK/src/main/java/com/xsc/sdk/network/recaptcha/RecaptchaManager.kt`
always returns an empty token - the real `com.google.android.gms.recaptcha.Recaptcha`
client call was never implemented, and even once it is, it needs a real reCAPTCHA
site key.

**Needed:** a production reCAPTCHA (Enterprise or v3) site key for this app's package
name, provisioned in Google Cloud Console, supplied at build time via
`-PrecaptchaSiteKey=...` (see `sdk/XscNetworkSDK/build.gradle.kts`).

## 4. Certificate pinning values (blocks: cert pinning going live)

`OkHttp CertificatePinner` wiring in `NetworkModule` is real and correct, gated on
`NetworkConfig.CERTIFICATE_PINS` being non-empty. There is no hardcoded default -
only whoever controls the production TLS certificate can supply its real SHA-256 SPKI
pin(s); a wrong or guessed pin doesn't warn, it just breaks connectivity.

**Needed:** the production API host's certificate chain SPKI pins (leaf + at least
one backup, e.g. the issuing intermediate), supplied at build time via
`-PcertificatePins="host|sha256/AAAA=,sha256/BBBB="` (see
`sdk/XscNetworkSDK/build.gradle.kts` for the exact format).

## 5. Firebase project registration (blocks: Crashlytics, FCM)

`app/build.gradle.kts` has the Google Services / Crashlytics Gradle plugins present
but commented out: `app/google-services.json` registers package `swarnim.oneapp.com`,
while this app's real `applicationId` is `com.xsc.oneapp` - the plugin fails the build
with "No matching client found" until this is fixed. `CrashReporter.kt` (`:core`)
already calls Crashlytics defensively and needs no further code changes once this is on.

**Needed:** in the Firebase console, register a new Android app under package
`com.xsc.oneapp`, download the resulting `google-services.json`, replace the one in
`app/`, then uncomment the two plugin lines in `app/build.gradle.kts`.

## 6. Play Integrity API (blocks: Phase 6 security hardening)

Not yet integrated (see the architecture audit). Play Integrity requires the app to
be registered in Google Play Console under this `applicationId`, plus a Google Cloud
project number linked to that Play Console listing.

**Needed:** Play Console app registration + linked Google Cloud project number, and a
server-side endpoint that verifies the integrity token (verification must happen on
the backend, never trusted client-side alone).

## 7. Release signing key

`app/build.gradle.kts` reads real signing config from `keystore.properties` (see
`keystore.properties.example`) or `RELEASE_*` environment variables - this is wired
correctly, but no actual keystore/credentials are checked into the repo (by design).

**Needed:** a real upload/release keystore and its credentials, held by whoever owns
releases for this app, supplied via `keystore.properties` (gitignored) or CI secrets.

## 8. Course / room / faculty name resolution (blocks: readable Timetable labels)

`feature/timetable`'s `TimetableEntry.courseId`/`roomId`/`facultyId` are raw
foreign-key ids - `m_timetable` does not resolve them to display names anywhere in
the confirmed contract (`courseLabel()`/`roomLabel()`/`facultyLabel()` in
`TimetableFormatting.kt` render `"Course #<id>"` etc. as the honest fallback). This
also blocks a fully-named Dashboard "Next Class" card
(`TimetableDashboardStatProvider`) beyond the id-labelled version it ships today.

**Needed:** either a name-resolution endpoint (course/room/faculty master lists), or
the relevant name fields joined directly onto `timetable:view`'s response rows.

## 9. Per-feature permission-string matrices (blocks: application-wide permission gating)

`feature/timetable`'s `TimetablePermissions` (contract v2 §9) is the only confirmed
RBAC permission-string matrix in this codebase - every other feature
(attendance/exam/fee/curriculum/profile) has no documented `mod.subMod.action`
permission strings to check against, confirmed by the same audit that found
Timetable's. `core/permissions` (architecture audit Phase 2) and
`core/navigation`'s `AppDestination.requiredPermission` are real, generic,
already-wired infrastructure for gating a screen or a navigation destination on one
of these strings - `RootNavHost.kt`'s Timetable route is gated on
`TimetablePermissions.TIMETABLE_VIEW` today (see
docs/PRODUCTION_READINESS.md Risk #5) as the one feature that has a real string to
gate on. Every other feature's `NavigationContribution` deliberately ships with
`requiredPermission = null` rather than a guessed string - inventing one would be
exactly the "hardcoded role checks instead of backend-driven permissions"
anti-pattern the architecture spec rules out.

**Needed from backend:** the same shape of RBAC matrix Timetable's contract v2 §9
documents, for each of attendance, exam, fee, curriculum and profile - the concrete
`mod.subMod.action` permission string(s) gating each screen/action, per role.
