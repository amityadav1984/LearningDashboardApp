# Learning Dashboard (Android)

Jetpack Compose app with clean architecture, mock APIs, offline cache, and lesson progress updates.

**Demo login:** `student@demo.com` / `password123`  
Force auth error: `fail@demo.com` / any password ≥ 6 chars.

## 1. Architecture

**UI → ViewModel → Repository → Mock API + Local JSON cache**

MVVM + repository keeps Compose screens thin, concentrates UI state in ViewModels, and isolates domain models from Android/IO details. A small `AppContainer` wires dependencies explicitly (easy to swap for Hilt later) without over-engineering a sample app.

## 2. Offline Support

After the first successful fetch, courses/lessons are saved to `courses_cache.json` in the app `filesDir`. `NetworkMonitor` detects airplane mode; when offline (or the mock API fails), the repository returns cache (`fromCache = true`) and the UI shows “Showing offline data”. Lesson completion writes the file immediately; remote refreshes merge local completions so progress is not wiped.

## 3. Security

Store auth tokens in **Android Keystore-backed** storage (EncryptedSharedPreferences / encrypted DataStore), never plain prefs, logs, or the APK. Use short-lived access tokens + refresh tokens, HTTPS only, and certificate pinning in production.

## 4. Scale (1M users, hundreds of courses)

1. Real paginated API + CDN for media; drop full-list mock payloads.  
2. WorkManager sync / offline write queue instead of eager full replace.  
3. Crash/analytics + remote config/feature flags.  
4. Modularize features and introduce Hilt.  
5. Server-authoritative progress with conflict resolution.

## 5. Second Platform (iOS / macOS)

Same boundaries: SwiftUI → Observable ViewModels → Repository → URLSession + SwiftData/Core Data. Share progress/validation rules via Kotlin Multiplatform if useful; otherwise port repository contracts and keep UI/storage native.

## Run & test

Open the `LearningDashboardApp` folder in Android Studio, sync Gradle, run on a device/emulator.

```bash
./gradlew :app:testDebugUnitTest
```

**Offline check:** login → load dashboard → enable airplane mode → Refresh still shows courses → open a course → mark lessons complete (progress updates from cache).
