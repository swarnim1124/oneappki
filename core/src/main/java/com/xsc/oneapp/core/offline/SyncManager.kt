package com.xsc.oneapp.core.offline

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val syncQueueDao: SyncQueueDao
) {
    private val workManager by lazy { WorkManager.getInstance(context) }

    suspend fun enqueueOfflineRequest(
        endpoint: String,
        method: String,
        payloadJson: String
    ): Long {
        val entity = SyncQueueEntity(
            endpoint = endpoint,
            method = method,
            payloadJson = payloadJson
        )
        val id = syncQueueDao.enqueueItem(entity)
        scheduleSyncWorker()
        return id
    }

    fun observePendingSyncQueue(): Flow<List<SyncQueueEntity>> {
        return syncQueueDao.observeAllItems()
    }

    fun scheduleSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncQueueWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "oneapp_offline_sync_work",
            ExistingWorkPolicy.KEEP,
            syncWorkRequest
        )
    }
}
