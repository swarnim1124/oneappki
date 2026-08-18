package com.xsc.oneapp.core.offline

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SyncQueueEntity::class],
    version = 1,
    exportSchema = false
)
abstract class OneAppDatabase : RoomDatabase() {
    abstract fun syncQueueDao(): SyncQueueDao
}
