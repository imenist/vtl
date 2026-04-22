package com.vitalo.markrun.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vitalo.markrun.data.local.db.entity.RunningRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface RunningRecordDao {
    @Insert
    suspend fun insert(record: RunningRecord): Long

    @Query("SELECT * FROM runningRecord ORDER BY startTime DESC")
    fun getAllRecords(): Flow<List<RunningRecord>>

    @Query("SELECT * FROM runningRecord WHERE id = :id")
    suspend fun getById(id: Long): RunningRecord?

    @Query("SELECT * FROM runningRecord ORDER BY startTime DESC LIMIT :limit")
    suspend fun getRecentRecords(limit: Int): List<RunningRecord>

    @Query("SELECT SUM(distance) FROM runningRecord")
    suspend fun getTotalDistance(): Double?

    @Query("SELECT SUM(duration) FROM runningRecord")
    suspend fun getTotalDuration(): Int?

    @Query("SELECT SUM(calories) FROM runningRecord")
    suspend fun getTotalCalories(): Double?

    @Query("SELECT MAX(distance) FROM runningRecord")
    suspend fun getLongestDistance(): Double?

    @Query("SELECT MAX(duration) FROM runningRecord")
    suspend fun getLongestDuration(): Int?

    @Query("DELETE FROM runningRecord WHERE id = :id")
    suspend fun deleteById(id: Long)
}
