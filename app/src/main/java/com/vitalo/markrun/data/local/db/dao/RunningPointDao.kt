package com.vitalo.markrun.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vitalo.markrun.data.local.db.entity.RunningPoint

@Dao
interface RunningPointDao {
    @Insert
    suspend fun insert(point: RunningPoint)

    @Insert
    suspend fun insertAll(points: List<RunningPoint>)

    @Query("SELECT * FROM runningPoint WHERE recordId = :recordId ORDER BY timestamp ASC")
    suspend fun getPointsByRecordId(recordId: Long): List<RunningPoint>
}
