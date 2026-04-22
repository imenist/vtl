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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.vitalo.markrun.ui.theme.TextHint
import com.vitalo.markrun.ui.theme.TextPrimary
import com.vitalo.markrun.ui.theme.TextSecondary
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdaySelectionScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val defaultDate = Calendar.getInstance().apply { set(1999, 0, 1) }.timeInMillis
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = defaultDate)

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { viewModel.setBirthday(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(41.dp))

        Text(
            text = "What's your date of birth?",
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

        DatePicker(
            state = datePickerState,
            modifier = Modifier.weight(1f),
            title = null,
            headline = null,
            showModeToggle = false,
            colors = DatePickerDefaults.colors(
                containerColor = Color.Transparent
            )
        )

        DotIndicator(total = 4, selectedIndex = 1)

        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = { navController.navigate(Screen.Height.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 10.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(
                text = "Next",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = Color.White
            )
        }

        TextButton(
            onClick = { navController.navigate(Screen.Height.route) },
            modifier = Modifier.padding(top = 10.dp, bottom = 31.dp)
        ) {
            Text(text = "Skip this step", fontSize = 14.sp, color = TextHint)
        }
    }
}
