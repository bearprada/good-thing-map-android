# Good Thing Map Android

Good Thing Map is an Android application for discovering and sharing positive places and stories. It supports category browsing, nearby searches, place details, comments, likes, image selection, Facebook sharing, and location-aware distance display.

## Build stack

- Android Gradle Plugin: 8.13.2
- Gradle wrapper: 8.13
- Kotlin: 2.2.10
- Compile SDK / target SDK: API 35
- Minimum SDK: API 15
- Java/Kotlin JVM target: 1.8

API 35 is the newest standard Android SDK platform available to the project’s build tools. A locally installed API 37 extension-level package is stored as `android-37.0`, which is not resolvable as a normal `android-37` compile platform.

The project currently uses the legacy Android Support Library because the existing UI and dependencies have not yet been migrated to AndroidX.

## Architecture

The main Home and Good List flows use MVVM:

```text
Activity -> ViewModel -> GoodThingService -> Retrofit/RxJava
    ^           |
    +--- LiveData state
```

- `HomeViewModel` loads and exposes the top story.
- `GoodListViewModel` loads, sorts, and exposes place results.
- Activities observe ViewModel state and keep navigation/rendering concerns in the UI layer.
- RxJava subscriptions are disposed when ViewModels are cleared.
- Schedulers and distance calculation are injectable for deterministic tests.

The repository is intentionally in a mixed Java/Kotlin state while the migration proceeds. Existing Java model and UI classes remain Kotlin-interoperable.

## Requirements

- JDK 21 or a compatible JDK supported by the Android Gradle Plugin.
- Android SDK with platform API 35 installed.
- An Android emulator or device for instrumentation tests.

The Gradle wrapper downloads the required Gradle distribution automatically on first use.

## Build and test

Run the complete local build:

```bash
./gradlew clean build
```

Run the JVM ViewModel tests:

```bash
./gradlew :app:testDebugUnitTest
```

The ViewModel test suite covers top-story success state, good-list distance sorting, and network error state.

Run instrumentation tests on a connected emulator or device:

```bash
adb devices
ANDROID_SERIAL=emulator-5554 ./gradlew connectedDebugAndroidTest
```

Test reports are generated under `app/build/reports/` and `app/build/outputs/androidTest-results/`.

## Outputs

After a successful build:

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Unsigned release APK: `app/build/outputs/apk/release/app-release-unsigned.apk`

The release build enables R8 shrinking and resource shrinking. Release signing is not configured in this repository.

## Development notes

- Keep API calls in repositories/ViewModels rather than activities.
- Expose UI state through observable types and dispose subscriptions with the ViewModel lifecycle.
- Add JVM tests for state transformations and error paths before changing UI behavior.
- Run both JVM and emulator tests before submitting a pull request.
- Do not commit local IDE metadata, generated build outputs, or `.serena/` workspace files.

## Repository history

The build tooling and SDK upgrade, Kotlin/MVVM migration, TDD coverage, and emulator test setup are documented in the project pull requests and commits on GitHub.
