package com.tom.todoapp.core.data.di

import com.tom.todoapp.core.data.SyncScheduler
import com.tom.todoapp.core.data.WorkManagerSyncScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds WorkManager-related implementations.
 * HiltWorkerFactory is automatically provided by the hilt-work artifact
 * and bound to WorkManager in TodoApplication.getWorkManagerConfiguration().
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerModule {

    @Singleton
    @Binds
    abstract fun bindSyncScheduler(impl: WorkManagerSyncScheduler): SyncScheduler
}
