package com.vitalo.markrun.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vitalo.markrun.navigation.Screen
import com.vitalo.markrun.ui.collection.CollectionScreen
import com.vitalo.markrun.ui.exchange.ExchangeScreen
import com.vitalo.markrun.ui.lesson.EarnRulesDialog
import com.vitalo.markrun.ui.lesson.LessonScreen
import com.vitalo.markrun.ui.common.GlobalOverlayManager
import com.vitalo.markrun.ui.task.TaskScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showEarnRules by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var earnCoin by remember { mutableIntStateOf(200) }
    var earnIsLoading by remember { mutableStateOf(false) }
    var earnIsCooling by remember { mutableStateOf(false) }
    var earnCooldownLeft by remember { mutableIntStateOf(0) }
    val earnCooldownTotal = 30
    var cooldownJob by remember { mutableStateOf<Job?>(null) }

    Scaffold(
        bottomBar = {
            CustomTabBar(
                selectedTab = selectedTab,
                onTabTapped = { tab ->
                    when (tab) {
                        2 -> {
                            navController.navigate(Screen.LocationPermission.route)
                        }
                        else -> selectedTab = tab
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())) {
            when (selectedTab) {
                0 -> LessonScreen(
                    navController = navController,
                    onShowSignIn = { GlobalOverlayManager.showSignInOverlay() },
                    onNavigateToWebGame = { kind ->
                        navController.navigate("web_game/$kind")
                    },
                    onTapExchange = {
                        selectedTab = 4
                    },
                    onTapEarnRules = {
                        showEarnRules = true
                    }
                )
                1 -> TaskScreen(
                    onShowSignIn = { GlobalOverlayManager.showSignInOverlay() },
                    onNavigateToWebGame = { kind ->
                        navController.navigate("web_game/$kind")
                    }
                )
                3 -> CollectionScreen()
                4 -> ExchangeScreen(navController = navController)
            }

            // Global floating ad
            if (selectedTab in listOf(0, 1, 3, 4)) { // Visible on Home, Task, Collection, Exchange screens
                AdEarnCoinFloatingButton(
                    modifier = Modifier
                        .padding(top = 232.dp, end = 5.dp)
                        .align(Alignment.TopEnd),
                    coinNum = earnCoin,
                    isLoading = earnIsLoading,
                    isCooling = earnIsCooling,
                    cooldownSeconds = earnCooldownLeft,
                    cooldownTotalSeconds = earnCooldownTotal,
                    onTap = {
                        if (earnIsLoading || earnIsCooling) return@AdEarnCoinFloatingButton
                        earnIsLoading = true
                        scope.launch {
                            delay(900)
                            earnIsLoading = false
                            
                            GlobalOverlayManager.showCoinArrivedOverlay(earnCoin)
                            
                            earnIsCooling = true
                            earnCooldownLeft = earnCooldownTotal
                            cooldownJob?.cancel()
                            cooldownJob = launch {
                                while (earnCooldownLeft > 0) {
                                    delay(1000)
                                    earnCooldownLeft -= 1
                                }
                                earnIsCooling = false
                            }
                        }
                    }
                )
            }
        }
    }

    if (showEarnRules) {
        EarnRulesDialog(onClose = { showEarnRules = false })
    }
}
