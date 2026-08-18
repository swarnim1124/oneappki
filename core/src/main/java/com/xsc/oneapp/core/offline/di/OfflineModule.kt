package com.xsc.oneapp.core.offline.di

import android.content.Context
import androidx.room.Room
import com.xsc.oneapp.core.offline.OneAppDatabase
import com.xsc.oneapp.core.offline.SyncQueueDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OfflineModule {

    @Provides
    @Singleton
    fun provideOneAppDatabase(
        @ApplicationContext context: Context
    ): OneAppDatabase {
        return Room.databaseBuilder(
            context,
            OneAppDatabase::class.java,
            "oneapp_offline.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideSyncQueueDao(
        database: OneAppDatabase
    ): SyncQueueDao {
        return database.syncQueueDao()
    }
}
