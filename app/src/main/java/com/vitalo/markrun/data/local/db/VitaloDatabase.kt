package com.vitalo.markrun.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vitalo.markrun.data.local.db.dao.RunningPointDao
import com.vitalo.markrun.data.local.db.dao.RunningRecordDao
import com.vitalo.markrun.data.local.db.dao.WeightRecordDao
import com.vitalo.markrun.data.local.db.entity.RunningPoint
import com.vitalo.markrun.data.local.db.entity.RunningRecord
import com.vitalo.markrun.data.local.db.entity.WeightRecord

@Database(
    entities = [RunningRecord::class, RunningPoint::class, WeightRecord::class],
    version = 1,
    exportSchema = false
)
abstract class VitaloDatabase : RoomDatabase() {
    abstract fun runningRecordDao(): RunningRecordDao
    abstract fun runningPointDao(): RunningPointDao
    abstract fun weightRecordDao(): WeightRecordDao
}
