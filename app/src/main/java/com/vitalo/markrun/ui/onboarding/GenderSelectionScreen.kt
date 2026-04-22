package com.vitalo.markrun.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vitalo.markrun.navigation.Screen
import com.vitalo.markrun.service.Gender
import com.vitalo.markrun.ui.theme.TextHint
import com.vitalo.markrun.ui.theme.TextPrimary
import com.vitalo.markrun.ui.theme.TextSecondary

@Composable
fun GenderSelectionScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val selectedGender by viewModel.gender.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(88.dp))

        Text(
            text = "What's your gender?",
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

        Spacer(modifier = Modifier.height(96.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            GenderCard(
                label = "Female",
                isSelected = selectedGender == Gender.FEMALE,
                onClick = { viewModel.setGender(Gender.FEMALE) },
                modifier = Modifier.weight(1f)
            )
            GenderCard(
                label = "Male",
                isSelected = selectedGender == Gender.MALE,
                onClick = { viewModel.setGender(Gender.MALE) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        DotIndicator(total = 4, selectedIndex = 0)

        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = {
                if (selectedGender == null) viewModel.setGender(Gender.FEMALE)
                navController.navigate(Screen.Birthday.route)
            },
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
            onClick = {
                if (selectedGender == null) viewModel.setGender(Gender.FEMALE)
                navController.navigate(Screen.Birthday.route)
            },
            modifier = Modifier.padding(top = 10.dp, bottom = 31.dp)
        ) {
            Text(text = "Skip this step", fontSize = 14.sp, color = TextHint)
        }
    }
}

@Composable
private fun GenderCard(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color.Black else Color.LightGray
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) Color(0xFFF5F5F5) else Color.White
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(
                    id = android.R.drawable.ic_menu_myplaces
                ),
                contentDescription = label,
                modifier = Modifier.size(48.dp),
                tint = if (isSelected) Color.Black else Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}
