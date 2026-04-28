package com.vitalo.markrun.data

import com.vitalo.markrun.data.remote.model.MountAction
import com.vitalo.markrun.data.remote.model.Training

object LessonDataStore {
    var currentActions: List<MountAction> = emptyList()
    var currentTraining: Training? = null
}
