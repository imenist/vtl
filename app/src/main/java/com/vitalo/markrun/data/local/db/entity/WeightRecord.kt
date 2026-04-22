package com.vitalo.markrun.data.local.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "weightRecord", indices = [Index(value = ["date"])])
data class WeightRecord(
    @PrimaryKey val id: String,
    val date: Long,
    val weight: Int
)
