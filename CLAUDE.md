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

## Conventions

- **ViewModel shape**: private `_field: MutableStateFlow<T>` per piece of state, combined via
  `combine(...)` into one public `val uiState: StateFlow<XxxUiState>`, built with
  `.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000L), initialValue = ...)`.
  Never expose a `MutableStateFlow` publicly — the screen only ever reads `uiState`. See
  `TaskViewModel`/`AddEditTaskViewModel` for the reference shape.
- **UiState naming**: one `data class <Feature>UiState` per screen, holding plain view data
  (no `Flow`/`LiveData` fields inside it).
- **One-shot user messages**: transient messages (errors, "task not found", validation
  failures) go through a `userMsg: Int?` field on the `UiState` — a `@StringRes` id, never a
  raw `String` (keeps the ViewModel free of `Context`/`Resources`). The screen calls a
  `xxxMsgShown()` method to reset it back to `null` after displaying it, so it doesn't
  re-fire on rotation/recomposition.
- **Loading state**: `Async<T>` (`core:ui`) wraps repository `Flow`s that can fail
  (`Async.Loading` / `Async.Success` / `Async.Error`) before folding the result into the
  screen's `UiState`. `_isLoading` is a separate flag for user-triggered loading (e.g. a
  save-in-progress), not the same channel as `Async`.
- **Repository access**: features call `TaskRepository`, never Room DAOs or
  `NetworkDataSource` directly — those stay internal to `:core:data`.
- Debug `Log.i("tamld7", ...)` calls scattered across `TodoNavGraph.kt`,
  `TodoNavigationAction.kt`, `TodoApplication.kt`, and `TaskViewModel.kt` are leftover
  scaffolding from earlier development, not a logging convention to copy — don't add new
  ones with that tag, and feel free to remove existing ones when touching those files.

## Business rules

- **Task validity**: a task needs a non-empty `title` *and* a non-empty `description` — see
  `Task.isEmpty`. `AddEditTaskViewModel.saveTask()` enforces the same rule client-side before
  calling `createTask`/`updateTask` and surfaces `R.string.empty_task_message` on failure. If
  you change what makes a task valid, update both places (there is no shared validator).
- **Display fallback**: `Task.titleForList` falls back to `description` when `title` is
  empty — this is why an "empty" task (failing `isEmpty`) can still exist transiently
  mid-edit without crashing the list UI.
- **Active vs. completed**: `Task.isActive == !isCompleted`; there is no third state. The
  task list filter (`TasksFilterType.ALL_TASKS` / `ACTIVE_TASKS` / `COMPLETED_TASK`) is pure
  client-side filtering over the same `getTasksStream()` — it does not change what's queried
  from Room.
- **Sync overwrites local**: `refreshTasks()` treats the network as the source of truth *for
  that call* and overwrites local Room rows — there is no merge/conflict resolution. A local
  edit made concurrently with a sync can be lost. Don't assume optimistic local writes are
  safe from being clobbered by the next periodic `SyncTasksWorker` run.

## Rules agents must follow here

- `feature:*` modules must never depend on `:app` or on each other — only on `:core:data`
  and `:core:ui`. This is **not enforced by Gradle today**, only by convention; a build that
  compiles is not proof the rule was respected, so check imports by hand when adding
  cross-feature navigation or shared state.
- `:core:data` must stay Compose-free — no `androidx.compose.*` import there, even
  transitively through a new dependency.
- Don't add a new local persistence path that bypasses Room, and don't call
  `NetworkDataSource` from outside `:core:data`.

## Stale leftovers — don't treat these as real

- **`TodoDestinations.STATISTIC_ROUTE`** and `TodoNavigationAction.navigateToStatistic()`
  are dead: the composable registered for that route in `TodoNavGraph.kt` is an empty `{}`
  block, and there is no `feature:statistics` module. This is inherited from the original
  (pre-modularization) Architecture-Samples TODO template, which had a real Statistics
  screen — it was never carried into this app. If a task calls for a statistics feature,
  build it as a new `feature:statistics` module from scratch (following the `feature:task`
  shape); don't assume the existing route/nav-action already wires up to something.
