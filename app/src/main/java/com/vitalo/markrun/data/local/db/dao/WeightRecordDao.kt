package com.vitalo.markrun.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vitalo.markrun.data.local.db.entity.WeightRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: WeightRecord)

    @Query("SELECT * FROM weightRecord ORDER BY date DESC")
    fun getAllRecords(): Flow<List<WeightRecord>>

    @Query("SELECT * FROM weightRecord ORDER BY date DESC LIMIT 1")
    suspend fun getLatestRecord(): WeightRecord?

    @Query("DELETE FROM weightRecord WHERE id = :id")
    suspend fun deleteById(id: String)
}
