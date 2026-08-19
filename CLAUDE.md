# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Run JVM unit tests, all modules
./gradlew testDebugUnitTest

# Run unit tests for one module only
./gradlew :feature:task:testDebugUnitTest

# Run a single unit test class / method
./gradlew :feature:task:testDebugUnitTest --tests "com.tom.todoapp.feature.task.ExampleUnitTest"

# Run instrumented tests (app/src/androidTest) — requires a connected device/emulator
./gradlew connectedDebugAndroidTest
./gradlew :app:connectedDebugAndroidTest --tests "com.tom.todoapp.TasksScreenTest"

# Install debug build on a connected device/emulator
./gradlew installDebug
```

`JAVA_HOME` must point at a JDK 17+; if none is on `PATH`, Android Studio ships one at
`/Applications/Android Studio.app/Contents/jbr/Contents/Home` (macOS).

## Module graph

```
:app  ->  :feature:task, :feature:addedittask, :feature:taskdetail  ->  :core:data, :core:ui
```

- **`:app`** — `TodoActivity` (single activity), `TodoApplication` (Hilt entry point +
  WorkManager `Configuration.Provider`), `TodoNavGraph`/`TodoNavigationAction`/
  `TodoDestinations` (Compose Navigation routes). Also owns app-only resources (theme,
  launcher icons) and the two Hilt instrumented tests (`AppNavigationTest`,
  `TasksScreenTest`), which need `HiltTestActivity` from `app/src/debug/`.
- **`:core:data`** (`com.tom.todoapp.core.data`) — the offline-first data layer:
  `TaskRepository`/`DefaultRepository`, Room (`local/`), the fake `NetworkDataSource`
  (`remote/`), `ModelMappingExt` conversions, WorkManager sync (`SyncScheduler`,
  `SyncTasksWorker`), and their Hilt modules under `di/` (`DataModule`, `WorkerModule`,
  `CoroutineModule` — dispatcher qualifiers + `@ApplicationScope`). No Compose dependency.
- **`:core:ui`** (`com.tom.todoapp.core.ui`) — shared Compose bits every feature needs:
  `TopAppBars.kt` (all the `TopAppBar` composables), `Async.kt` (loading/success/error
  wrapper for ViewModel state), `ComposeUtils.kt`, and `TodoDestinationsArgs` (nav argument
  key constants — lives here rather than in `:app` because feature ViewModels read it and
  features can't depend on `:app`). Also owns the shared `strings.xml`/`dimens.xml` and the
  handful of drawables the top bars/filter UI reference. `app_name` and other app-only
  resources still resolve at the `:app` manifest via normal resource merging.
- **`:feature:task`**, **`:feature:addedittask`**, **`:feature:taskdetail`** — one screen +
  one `@HiltViewModel` each, package `com.tom.todoapp.feature.<name>`. Each depends only on
  `:core:data` and `:core:ui`, never on `:app` or on each other.

When adding a screen-level dependency, check whether it belongs in `:core:ui` (consumed by
2+ features) before reaching for `:app`. Nothing under `feature:*` may depend on `:app`.

## Architecture

**Data layer is offline-first**, following the classic "Architecture Components" TO-DO
sample shape: Room (`core:data`'s `local/`) is the source of truth read by
`getTasksStream()`/`getTaskStream()`; `refreshTasks()` pulls from the (fake, in-memory)
`NetworkDataSource` and overwrites the local DB. `Task` (domain), `LocalTask` (Room entity),
`NetworkTask` (remote DTO) are converted via `ModelMappingExt.kt`.

**Background sync** runs through WorkManager, wired via Hilt: `SyncScheduler`/
`WorkManagerSyncScheduler` enqueue one-time + periodic work; `SyncTasksWorker` is a
`HiltWorker`/`CoroutineWorker` that calls `taskRepository.refreshTasks()`, retrying with
backoff up to `MAX_RUN_ATTEMPTS` before `Result.failure()`. `TodoApplication` supplies
`HiltWorkerFactory` via `Configuration.Provider` and kicks off both jobs in `onCreate()`.

Coroutine flavor: Kotlin Coroutines + Flow throughout (no RxJava/LiveData). Dependency
injection: Hilt everywhere a module has `@HiltViewModel`/`@Module`/`@HiltWorker` code —
every such module applies the Hilt Gradle plugin + `ksp` for its compiler, not just `:app`.
