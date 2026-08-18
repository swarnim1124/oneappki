package com.xsc.oneapp.core.offline

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class SyncQueueWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncQueueDao: SyncQueueDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val pendingItems = syncQueueDao.getPendingItems()
        if (pendingItems.isEmpty()) {
            return@withContext Result.success()
        }

        var hasFailures = false

        for (item in pendingItems) {
            try {
                // Update status to IN_PROGRESS
                syncQueueDao.updateItem(item.copy(status = "IN_PROGRESS"))

                // Simulate execution/dispatching of queued request payload to network API
                val isSuccess = processSyncItem(item)

                if (isSuccess) {
                    syncQueueDao.updateItem(item.copy(status = "COMPLETED", updatedAtTimestamp = System.currentTimeMillis()))
                } else {
                    val newRetryCount = item.retryCount + 1
                    if (newRetryCount >= item.maxRetries) {
                        syncQueueDao.updateItem(item.copy(status = "FAILED", errorMessage = "Max retries exceeded", retryCount = newRetryCount))
                    } else {
                        syncQueueDao.updateItem(item.copy(status = "PENDING", retryCount = newRetryCount))
                        hasFailures = true
                    }
                }
            } catch (e: Exception) {
                val newRetryCount = item.retryCount + 1
                syncQueueDao.updateItem(
                    item.copy(
                        status = if (newRetryCount >= item.maxRetries) "FAILED" else "PENDING",
                        errorMessage = e.localizedMessage,
                        retryCount = newRetryCount
                    )
                )
                hasFailures = true
            }
        }

        if (hasFailures) {
            Result.retry()
        } else {
            Result.success()
        }
    }

    private fun processSyncItem(item: SyncQueueEntity): Boolean {
        // In real backend operation, dispatch endpoint + payloadJson via APIClient
        return item.endpoint.isNotBlank()
    }
}
