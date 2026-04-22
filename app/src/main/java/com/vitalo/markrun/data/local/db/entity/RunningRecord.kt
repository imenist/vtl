package com.vitalo.markrun.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "runningRecord")
data class RunningRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val startTime: Long,
    val endTime: Long,
    val duration: Int,
    val distance: Double,
    val speed: Double,
    val calories: Double,
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
    val lowIntensityTime: Int,
    val moderateIntensityTime: Int,
    val highIntensityTime: Int,
    val imagePath: String?,
    val fragmentsCount: Int = 0,
    val coins: Int = 0,
    val adCoins: Int? = null
) {
    val pace: Double
        get() = if (distance > 0) (duration / 60.0) / (distance / 1000.0) else 0.0

    val paceString: String
        get() {
            val minutes = pace.toInt()
            val seconds = ((pace - minutes) * 60).toInt()
            return "${minutes}'${String.format("%02d", seconds)}\""
        }
}
