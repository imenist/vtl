package com.vitalo.markrun.ui.web

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object H5GameProgressNotifier {
    private val _progressFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val progressFlow = _progressFlow.asSharedFlow()

    fun notifyProgressChanged() {
        _progressFlow.tryEmit(Unit)
    }
}
