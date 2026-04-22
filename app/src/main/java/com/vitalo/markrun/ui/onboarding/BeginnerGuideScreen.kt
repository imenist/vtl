package com.vitalo.markrun.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vitalo.markrun.R
import com.vitalo.markrun.navigation.Screen
import com.vitalo.markrun.ui.theme.TextHint
import com.vitalo.markrun.ui.theme.TextPrimary

@Composable
fun BeginnerGuideScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 28.dp)) {
            Text(
                text = "Hello",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = TextPrimary
            )

            Text(
                text = "Welcome to MarkRun.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = TextPrimary,
                modifier = Modifier.padding(top = 9.dp)
            )

            Text(
                text = "To calculate your calorie burn and body stats more accurately, we'll ask you to enter basic information.\nThe data is used only to improve your workout insights and is securely stored in compliance with privacy regulations.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                color = TextPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(0.3f))

        Button(
            onClick = { navController.navigate(Screen.Gender.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 37.dp),
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
                viewModel.completeWithDefaults()
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.BeginnerGuide.route) { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp, bottom = 31.dp)
        ) {
            Text(
                text = "Skip",
                fontSize = 14.sp,
                color = TextHint,
            )
        }
    }
}
