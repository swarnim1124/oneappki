package com.xsc.oneapp.core.offline

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncQueueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueueItem(item: SyncQueueEntity): Long

    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY createdAtTimestamp ASC")
    suspend fun getPendingItems(): List<SyncQueueEntity>

    @Query("SELECT * FROM sync_queue ORDER BY createdAtTimestamp DESC")
    fun observeAllItems(): Flow<List<SyncQueueEntity>>

    @Update
    suspend fun updateItem(item: SyncQueueEntity)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteItem(id: Long)

    @Query("DELETE FROM sync_queue WHERE status = 'COMPLETED'")
    suspend fun clearCompletedItems()
}
