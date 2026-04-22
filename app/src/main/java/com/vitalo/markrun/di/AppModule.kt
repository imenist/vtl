package com.vitalo.markrun.di

import android.content.Context
import androidx.room.Room
import com.vitalo.markrun.data.local.db.VitaloDatabase
import com.vitalo.markrun.data.local.db.dao.RunningPointDao
import com.vitalo.markrun.data.local.db.dao.RunningRecordDao
import com.vitalo.markrun.data.local.db.dao.WeightRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VitaloDatabase {
        return Room.databaseBuilder(
            context,
            VitaloDatabase::class.java,
            "vitalo.db"
        ).build()
    }

    @Provides
    fun provideRunningRecordDao(db: VitaloDatabase): RunningRecordDao = db.runningRecordDao()

    @Provides
    fun provideRunningPointDao(db: VitaloDatabase): RunningPointDao = db.runningPointDao()

    @Provides
    fun provideWeightRecordDao(db: VitaloDatabase): WeightRecordDao = db.weightRecordDao()
}
