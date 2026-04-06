package com.tom.todoapp.data

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * CoroutineWorker that syncs tasks from network to local DB.
 *
 * Clean Architecture: Worker lives in the data/infrastructure layer.
 * TaskRepository (domain interface) is injected — Worker has no direct
 * dependency on Room/Retrofit, satisfying dependency inversion.
 *
 * Edge cases handled:
 * - Network/IO exception → Result.retry() (WorkManager applies exponential backoff)
 * - runAttemptCount > MAX_RUN_ATTEMPTS → Result.failure() to stop infinite retries
 * - Cancellation is handled automatically by CoroutineWorker (coroutine is cancelled)
 */
@HiltWorker
class SyncTasksWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val taskRepository: TaskRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Log.i(TAG, "doWork: attempt #$runAttemptCount")

        if (runAttemptCount >= MAX_RUN_ATTEMPTS) {
            Log.w(TAG, "doWork: max retries reached, stopping")
            return Result.failure()
        }

        return try {
            taskRepository.refreshTasks()
            Log.i(TAG, "doWork: sync succeeded")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "doWork: sync failed — ${e.message}", e)
            // Retry with exponential backoff (configured in SyncScheduler)
            Result.retry()
        }
    }

    companion object {
        const val TAG = "SyncTasksWorker"
        const val WORK_NAME_PERIODIC = "SyncTasksWorker_Periodic"
        const val WORK_NAME_ONE_TIME = "SyncTasksWorker_OneTime"
        private const val MAX_RUN_ATTEMPTS = 3
    }
}
