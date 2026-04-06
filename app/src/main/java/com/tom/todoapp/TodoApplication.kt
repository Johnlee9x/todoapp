package com.tom.todoapp

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.tom.todoapp.core.data.SyncScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Custom Application class.
 *
 * Implements [Configuration.Provider] so WorkManager uses [HiltWorkerFactory],
 * enabling Hilt injection inside CoroutineWorkers (@AssistedInject).
 *
 * NOTE: When using a custom WorkManager configuration, do NOT initialize
 * WorkManager via WorkManager.initialize() elsewhere — Hilt handles it here.
 * Also ensure the default WorkManager initializer is disabled in AndroidManifest.xml.
 */
@HiltAndroidApp
class TodoApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var syncScheduler: SyncScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        Log.i("tamld7", "onCreate: ")
        // One-time sync on startup; periodic keeps running in background every 1h.
        // Both are no-ops if already enqueued (KEEP policy).
        syncScheduler.scheduleOneTimeSync()
        syncScheduler.schedulePeriodicSync()
    }
}