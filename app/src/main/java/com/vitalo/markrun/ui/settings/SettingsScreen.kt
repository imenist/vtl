package com.vitalo.markrun.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vitalo.markrun.R
import com.vitalo.markrun.data.local.prefs.AppPreferences
import com.vitalo.markrun.ui.mine.SectionHeader

private const val PRIVACY_POLICY_URL = "https://www.mark-run.com/privacy"
private const val USER_AGREEMENT_URL = "https://www.mark-run.com/terms"

@Composable
fun SettingsScreen(
    navController: NavController,
    appPreferences: AppPreferences? = null
) {
    var selectedUnit by remember {
        mutableStateOf(appPreferences?.getString(AppPreferences.KEY_SELECTED_DISTANCE_UNIT) ?: "KM")
    }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showUnitMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val blurRadius = if (showFeedbackDialog) 5.dp else 0.dp

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .blur(blurRadius)
        ) {
            // Header
            SettingsHeaderView(onTapBack = { navController.popBackStack() })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(23.dp))

                // Unit Settings
                SectionHeader(title = "Unit Settings")

                Spacer(modifier = Modifier.height(10.dp))

                // Metric & Imperial Units
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF4F3EE))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Metric & Imperial Units",
                        fontSize = 14.sp,
                        color = Color(0xFF0D120E),
                        modifier = Modifier.weight(1f)
                    )

                    Box {
                        Text(
                            text = selectedUnit,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF78D400),
                            modifier = Modifier
                                .clickable { showUnitMenu = true }
                                .padding(4.dp)
                        )

                        DropdownMenu(
                            expanded = showUnitMenu,
                            onDismissRequest = { showUnitMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("KM") },
                                onClick = {
                                    selectedUnit = "KM"
                                    appPreferences?.setString(AppPreferences.KEY_SELECTED_DISTANCE_UNIT, "KM")
                                    showUnitMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("MILE") },
                                onClick = {
                                    selectedUnit = "MILE"
                                    appPreferences?.setString(AppPreferences.KEY_SELECTED_DISTANCE_UNIT, "MILE")
                                    showUnitMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Support Us
                SectionHeader(title = "Support Us")

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF4F3EE))
                ) {
                    SettingOptionItem(
                        title = "Feedback",
                        onClick = { showFeedbackDialog = true }
                    )
                    HorizontalDivider(
                        color = Color(0xFFE0E0E0),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SettingOptionItem(
                        title = "Rate Us",
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=${context.packageName}")
                            )
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                                    )
                                )
                            }
                        }
                    )
                    HorizontalDivider(
                        color = Color(0xFFE0E0E0),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SettingOptionItem(
                        title = "Privacy Policy",
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL)))
                        }
                    )
                    HorizontalDivider(
                        color = Color(0xFFE0E0E0),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SettingOptionItem(
                        title = "User Agreement",
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(USER_AGREEMENT_URL)))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (showFeedbackDialog) {
            FeedbackDialog(onClose = { showFeedbackDialog = false })
        }
    }
}

@Composable
private fun SettingsHeaderView(onTapBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D120E))
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onTapBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
private fun SettingOptionItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color(0xFF0D120E)
        )
        Text(
            text = "›",
            fontSize = 18.sp,
            color = Color(0xFFBBBBBB)
        )
    }
}
