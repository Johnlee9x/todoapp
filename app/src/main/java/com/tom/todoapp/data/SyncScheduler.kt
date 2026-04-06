package com.tom.todoapp.data

/**
 * Domain-layer contract for scheduling background sync.
 * Kept in the domain layer so ViewModel / use-cases can depend on the
 * abstraction without knowing WorkManager details.
 */
interface SyncScheduler {
    /**
     * Enqueue a one-time sync. Safe to call multiple times — duplicate
     * requests are deduplicated by WorkManager using [WORK_NAME_ONE_TIME].
     */
    fun scheduleOneTimeSync()

    /**
     * Enqueue a periodic sync. Uses [ExistingPeriodicWorkPolicy.KEEP] so
     * re-enrolling (e.g. on every app launch) does not reset the timer.
     */
    fun schedulePeriodicSync()

    /**
     * Cancel all scheduled sync work (useful in tests or sign-out flows).
     */
    fun cancelAll()
}
