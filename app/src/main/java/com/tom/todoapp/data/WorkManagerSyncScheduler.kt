package com.tom.todoapp.data

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WorkManager-backed implementation of [SyncScheduler].
 * Lives in the data layer — ViewModel and domain code depend only on the interface.
 *
 * Edge cases handled:
 * - Network unavailable: Constraints.CONNECTED defers work until network is available.
 * - App killed mid-sync: WorkManager re-runs the worker on next opportunity.
 * - Duplicate periodic enqueue: ExistingPeriodicWorkPolicy.KEEP preserves existing schedule.
 * - Duplicate one-time enqueue: ExistingWorkPolicy.KEEP avoids redundant runs.
 * - Repeated failure: SyncTasksWorker returns Result.failure() after MAX_RUN_ATTEMPTS;
 *   backoff is exponential to avoid hammering the network.
 */
@Singleton
class WorkManagerSyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : SyncScheduler {

    private val workManager = WorkManager.getInstance(context)

    private val networkConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    override fun scheduleOneTimeSync() {
        Log.i(TAG, "scheduleOneTimeSync: enqueuing")
        val request = OneTimeWorkRequestBuilder<SyncTasksWorker>()
            .setConstraints(networkConstraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .addTag(SyncTasksWorker.TAG)
            .build()

        workManager.enqueueUniqueWork(
            SyncTasksWorker.WORK_NAME_ONE_TIME,
            ExistingWorkPolicy.KEEP, // Don't restart if already queued/running
            request,
        )
    }

    override fun schedulePeriodicSync() {
        Log.i(TAG, "schedulePeriodicSync: enqueuing periodic work (interval = 1h)")
        val request = PeriodicWorkRequestBuilder<SyncTasksWorker>(1, TimeUnit.HOURS)
            .setConstraints(networkConstraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .addTag(SyncTasksWorker.TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            SyncTasksWorker.WORK_NAME_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP, // Preserve existing schedule on re-enqueue
            request,
        )
    }

    override fun cancelAll() {
        Log.i(TAG, "cancelAll: cancelling all sync work")
        workManager.cancelAllWorkByTag(SyncTasksWorker.TAG)
    }

    companion object {
        private const val TAG = "WorkManagerSyncScheduler"
    }
}
