# Production Readiness Log

Reconstructed from in-code references (`PRODUCTION_READINESS_AUDIT.md`,
`PRODUCTION_READINESS_FINAL.md`) that several files point to but that were not
present on disk - these were evidently scratch documents from an earlier working
session that never got committed. This file consolidates what those comments already
describe (all in the codebase today, in past tense - the fixes are already in place)
so the numbered references resolve to something real. Kept as one file rather than
two since there's no way to recover which item was originally in which document.

## Critical

- **C-1 / H-8 - Firebase silently misconfigured.** `app/google-services.json`
  registers package `swarnim.oneapp.com` against an `applicationId` of
  `com.xsc.oneapp`; applying the Google Services/Crashlytics plugins with that
  mismatch fails the build outright. Both plugins are commented out in
  `app/build.gradle.kts` until this is fixed - see
  `docs/BACKEND_ENDPOINT_REQUIREMENTS.md` §5. `CrashReporter.kt` (`:core`) already
  guards every call so it's safe to enable with no further code changes once the
  package is fixed.
- **C-2 - Release builds were unsignable.** `config/signing.gradle.kts` was a dead
  stub nobody applied, and `isMinifyEnabled` was `false` outright, so
  `assembleRelease` produced an unsignable, unshrunk, unobfuscated APK. Fixed:
  `app/build.gradle.kts` now reads real signing config from `keystore.properties`
  (gitignored) or `RELEASE_*` env vars, and `isMinifyEnabled`/`isShrinkResources` are
  both `true` for `release`, with Gson/Retrofit R8 keep rules in `proguard-rules.pro`.
- **C-5 - Release could silently ship pointed at the dev backend.** A plain
  `./gradlew assembleRelease` used the same default `BASE_URL` as debug. Fixed:
  `sdk/XscNetworkSDK/build.gradle.kts` now throws a `GradleException` if a release
  variant is being assembled without an explicit `-PbaseUrl=...`.

## High

- **H-8 - Zero crash visibility.** Every ViewModel's catch block either swallowed
  failures or decided independently whether to report them. Fixed:
  `core/src/main/java/com/xsc/oneapp/core/result/UiStateCatching.kt` is the single
  funnel every ViewModel's error handling goes through, reporting unexpected
  failures to `CrashReporter` (expected, handled failures - `APIError.BusinessError`/
  `NetworkError` - are not treated as crashes).

## Risks (numbered as referenced in code; original risk-register document not recovered)

- **Risk #4 - Sessions died the moment the access token expired.**
  `TokenManager.refreshToken` was written on login and read nowhere - there was no
  refresh path in the request pipeline at all, so every session ended at token expiry
  with no way back short of a manual re-login; a forced logout (e.g. from a failed
  refresh) also left the user stranded on their current screen instead of being
  routed to Login. Partially fixed: `TokenAuthenticator` (`sdk/XscNetworkSDK`) now
  has real single-flight refresh/retry plumbing wired into the OkHttp pipeline, and
  `RootNavHost` reactively observes `SessionManager.isAuthenticated` and force-routes
  to Login on a `true -> false` transition. Still blocked on the refresh-token
  dispatcher *contract* itself - see `docs/BACKEND_ENDPOINT_REQUIREMENTS.md` §1.
- **Risk #5 - Permissions were captured but never checked.** `SessionManager
  .hasPermission()` was implemented and unit-tested but not consulted by any screen -
  every screen was reachable to every signed-in user regardless of their backend-
  assigned permissions, with the backend as the only real gate. Partially fixed:
  `feature/timetable` gates its dashboard contributions
  (`TimetableDashboardStatProvider`, `TimetableTimelineProvider`) on
  `timetable.timetable.view`, and - as of the architecture audit's Phase 2
  (centralized Permission Engine, `core/permissions` + `core/navigation`) -
  `RootNavHost.kt`'s Timetable *route itself* is now gated the same way via
  `PermissionGate`, popping back rather than entering the screen if the permission
  isn't held. Every other feature's navigation entry (`core/navigation`'s
  `NavigationContribution`, one per feature) is real, DI-wired infrastructure ready
  to gate the same way, but still ungated - not a shortcut, a real constraint: no
  other feature has a confirmed backend permission-string matrix to gate on yet, and
  inventing one would itself violate the architecture spec's rule against hardcoded
  permission checks. See docs/BACKEND_ENDPOINT_REQUIREMENTS.md #9.
- **Risk #10 - A corrupted Keystore entry crashed the app on every launch.** The
  `EncryptedSharedPreferences` construction ran uncaught inside a Hilt `@Provides`
  method - a stale/corrupted Android Keystore alias failed DI graph construction
  entirely, crashing on every single launch with no recovery short of the user
  manually clearing app data. Fixed: `AuthModule.kt` now deletes the stale prefs file
  and Keystore alias and retries once, falling back to a plaintext `SharedPreferences`
  store if that also fails, rather than crash-looping (a documented
  availability-over-confidentiality tradeoff for this one recovery path).
