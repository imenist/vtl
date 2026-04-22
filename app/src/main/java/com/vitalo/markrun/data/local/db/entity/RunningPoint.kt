package com.vitalo.markrun.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "runningPoint",
    foreignKeys = [ForeignKey(
        entity = RunningRecord::class,
        parentColumns = ["id"],
        childColumns = ["recordId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["recordId", "timestamp"])]
)
data class RunningPoint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recordId: Long,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val speed: Double
)
