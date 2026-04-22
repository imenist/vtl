package com.vitalo.markrun.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun CommonLottieView(
    animationName: String,
    loop: Boolean = true,
    speed: Float = 1f,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("lottie/$animationName/data.json"),
        imageAssetsFolder = "lottie/$animationName/images/"
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = if (loop) LottieConstants.IterateForever else 1,
        speed = speed
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}
