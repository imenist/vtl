package com.vitalo.markrun.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vitalo.markrun.navigation.Screen
import com.vitalo.markrun.ui.theme.GradientGreenStart
import com.vitalo.markrun.ui.theme.TextHint
import com.vitalo.markrun.ui.theme.TextPrimary
import com.vitalo.markrun.ui.theme.TextSecondary

@Composable
fun WeightSelectionScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val weightKg by viewModel.weightKg.collectAsState()
    var sliderValue by remember { mutableFloatStateOf(weightKg.toFloat()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(41.dp))

        Text(
            text = "What is your weight?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )

        Text(
            text = "Calories & Stride Length Calculation need it",
            fontSize = 16.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 13.dp)
        )

        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "${sliderValue.toInt()} KG",
            fontSize = 48.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                viewModel.setWeightKg(it.toInt())
            },
            valueRange = 20f..300f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color.Black,
                activeTrackColor = GradientGreenStart
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        DotIndicator(total = 4, selectedIndex = 3)

        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = {
                viewModel.completeOnboarding()
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.BeginnerGuide.route) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 10.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = "START",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = Color.White
            )
        }

        TextButton(
            onClick = {
                viewModel.completeOnboarding()
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.BeginnerGuide.route) { inclusive = true }
                }
            },
            modifier = Modifier.padding(top = 10.dp, bottom = 31.dp)
        ) {
            Text(text = "Skip this step", fontSize = 14.sp, color = TextHint)
        }
    }
}
