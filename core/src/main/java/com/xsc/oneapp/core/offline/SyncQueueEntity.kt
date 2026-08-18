package com.xsc.oneapp.core.offline

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val endpoint: String,
    val method: String,
    val payloadJson: String,
    val status: String = "PENDING", // PENDING, IN_PROGRESS, FAILED, COMPLETED
    val retryCount: Int = 0,
    val maxRetries: Int = 5,
    val errorMessage: String? = null,
    val createdAtTimestamp: Long = System.currentTimeMillis(),
    val updatedAtTimestamp: Long = System.currentTimeMillis()
)
