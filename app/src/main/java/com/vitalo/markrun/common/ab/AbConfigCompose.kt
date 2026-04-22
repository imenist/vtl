package com.vitalo.markrun.common.ab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Observer
import com.vitalo.markrun.common.ab.impl.WithDrawConfig

@Composable
fun rememberWithDrawConfig(): WithDrawConfig {
    val configState = remember { mutableStateOf(AbConfigDataRepo.getWithDrawConfig()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    
    DisposableEffect(lifecycleOwner) {
        val observer = Observer<AbConfigResponse> {
            configState.value = AbConfigDataRepo.getWithDrawConfig()
        }
        AbConfigDataRepo.getObservableData().observe(lifecycleOwner, observer)
        onDispose {
            AbConfigDataRepo.getObservableData().removeObserver(observer)
        }
    }
    
    return configState.value
}
