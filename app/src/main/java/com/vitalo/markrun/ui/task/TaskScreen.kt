package com.vitalo.markrun.ui.task

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitalo.markrun.data.remote.model.ChestState
import com.vitalo.markrun.data.remote.model.DailyTaskInfo
import com.vitalo.markrun.data.remote.model.DailyTaskKind
import com.vitalo.markrun.data.remote.model.DailyTaskStatus
import com.vitalo.markrun.ui.theme.VitaloTheme
import com.vitalo.markrun.ui.common.CoinBalanceView
import java.text.NumberFormat
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.vitalo.markrun.ad.AdManager
import com.vitalo.markrun.ad.Ads
import android.app.Activity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


// ═══════════════════════════════════════════════════════════════════════════════
// region ──── iOS-exact Design Tokens ────
// ═══════════════════════════════════════════════════════════════════════════════

private val TaskTitleColor = Color(0xFF0D120E)
private val TaskNameColor = Color(0xFF0D120E)
private val TaskDescColor = Color(0xFF575757)
private val CoinRewardColor = Color(0xFFFF9000)
private val TaskCardBg = Color(0xFFF8F8F8)
private val DisabledBtnBg = Color(0xFFD7D5C7)
private val GoBtnBg = Color(0xFF0D120D)
private val RelaxCardBg = Color(0xFFF7F7F7)

private val ClaimGradientStart = Color(0xFFFFED29)
private val ClaimGradientEnd = Color(0xFFC9FF6B)
private val ClaimedGradientStart = Color(0xFFFE6544)
private val ClaimedGradientEnd = Color(0xFFFBA23C)
private val ExpiredGradientStart = Color(0xFFDCD29D)
private val ExpiredGradientEnd = Color(0xFFD6D6D6)

private val SectionDotStart = Color(0xFFC9FF6B)
private val SectionDotEnd = Color(0xFFFFED29)

private val ChestContainerTop = Color(0xFFFFF9ED)
private val ChestContainerMid = Color(0xFFECFFA8)
private val ChestContainerBottom = Color(0xFFFFFCDA)

private val PageBgStart = Color(0xFFFCFFF8)
private val PageBgEnd = Color(0xFFFCF28C)

private val CoinPillLight = Color(0xFFA1C21C)
private val CoinPillMid = Color(0xFFA3C93B)
private val CoinPillDark = Color(0xFF2E824F)

private val ChestCoinGrad1 = Color(0xFFFFDA24)
private val ChestCoinGrad2 = Color(0xFFFE9814)
private val ChestCoinGrad3 = Color(0xFFFC1702)

private val ProgressBarStart = Color(0xFFFFED29)
private val ProgressBarEnd = Color(0xFFC9FF6B)
private val ProgressTextColor = Color(0xFFD7D7C7)

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── UI Display Models ────
// ═══════════════════════════════════════════════════════════════════════════════

data class ChestDisplayItem(
    val index: Int,
    val timeLabel: String,
    val reward: Int,
    val state: ChestState
)

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── Mock Data (Preview / Development) ────
// ═══════════════════════════════════════════════════════════════════════════════

private val mockChests = listOf(
    ChestDisplayItem(0, "08:00", 200, ChestState.CLAIMED),
    ChestDisplayItem(1, "12:00", 200, ChestState.CLAIMED),
    ChestDisplayItem(2, "16:00", 200, ChestState.CLAIMABLE),
    ChestDisplayItem(3, "20:00", 200, ChestState.WAITING)
)

private val mockStatus = DailyTaskStatus(
    chestClaimed = mutableListOf(true, true, false, false),
    runningKcal = 120,
    trainingMinutes = 15,
    signInToday = true,
    signInClaimed = true,
    newUserSpinCount = 7,
    crackEggCount = 10,
    luckySlotCount = 3,
    multiDailyRelaxationCompletedLinkIndices = mutableListOf(true, false, false)
)

private val mockDailyTasks = listOf(
    DailyTaskInfo(DailyTaskKind.NOTIFICATION, canClaim = false, claimed = false, reward = 400),
    DailyTaskInfo(DailyTaskKind.MOTION_USAGE, canClaim = false, claimed = false, reward = 400),
    DailyTaskInfo(DailyTaskKind.CHEST_ALL, canClaim = false, claimed = false, reward = 500),
    DailyTaskInfo(DailyTaskKind.NEW_USER_SPIN, canClaim = false, claimed = false, reward = 300),
    DailyTaskInfo(DailyTaskKind.CRACK_EGG, canClaim = true, claimed = false, reward = 300),
    DailyTaskInfo(DailyTaskKind.LUCKY_SLOT, canClaim = false, claimed = false, reward = 300),
    DailyTaskInfo(DailyTaskKind.UPPER_STEP_CONVERSION, canClaim = false, claimed = false, reward = 0),
    DailyTaskInfo(DailyTaskKind.MULTI_DAILY_RELAXATION, canClaim = false, claimed = false, reward = 400),
    DailyTaskInfo(DailyTaskKind.DAILY_RELAXATION, canClaim = false, claimed = false, reward = 400),
    DailyTaskInfo(DailyTaskKind.RUNNING, canClaim = false, claimed = false, reward = 300),
    DailyTaskInfo(DailyTaskKind.TRAINING, canClaim = false, claimed = false, reward = 300),
    DailyTaskInfo(DailyTaskKind.SIGN_IN, canClaim = false, claimed = true, reward = 200),
)

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── TaskScreen (Stateful Wrapper) ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun TaskScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    onShowSignIn: () -> Unit = {},
    onNavigateToWebGame: (String) -> Unit = {},
    onNavigateToWebGameWithIndex: (String, Int) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val coinBalance by viewModel.coinBalance.collectAsState()
    val taskStatus by viewModel.dailyTaskStatus.collectAsState()
    val dailyTasks = remember(taskStatus) { viewModel.allDailyTasks() }
    val chests = remember(taskStatus) {
        (0..3).map { index ->
            ChestDisplayItem(
                index = index,
                timeLabel = viewModel.chestTimeLabel(index),
                reward = viewModel.chestReward,
                state = viewModel.chestState(index)
            )
        }
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.markNotificationClaimed()
            com.vitalo.markrun.ui.common.GlobalOverlayManager.showCoinArrivedOverlay(50)
        }
    }

    val motionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.markMotionUsageClaimed()
            com.vitalo.markrun.ui.common.GlobalOverlayManager.showCoinArrivedOverlay(50)
        }
    }

    TaskScreenContent(
        coinBalance = coinBalance.toInt(),
        chests = chests,
        dailyTasks = dailyTasks,
        taskStatus = taskStatus,
        multiRelaxTotalNum = 3,
        multiRelaxCompletedIndices = taskStatus.multiDailyRelaxationCompletedLinkIndices,
        onChestClaim = { viewModel.claimChest(it) },
        onTaskGo = { kind ->
            when (kind) {
                DailyTaskKind.SIGN_IN -> onShowSignIn()
                DailyTaskKind.NEW_USER_SPIN -> com.vitalo.markrun.ui.common.GlobalOverlayManager.showSpinWheelOverlay()
                DailyTaskKind.CRACK_EGG -> onNavigateToWebGame("smashEgg")
                DailyTaskKind.LUCKY_SLOT -> onNavigateToWebGame("slotMachine")
                DailyTaskKind.DAILY_RELAXATION -> onNavigateToWebGame("dailyRelaxation")
                DailyTaskKind.UPPER_STEP_CONVERSION -> {
                    val activity = context as? Activity
                    if (activity != null) {
                        AdManager.showAd(
                            activity = activity,
                            virtualId = Ads.REWARD_TASK_STEP_LIMIT_UP,
                            onComplete = { rewarded ->
                                if (rewarded) {
                                    viewModel.markUpperStepConversionClaimed()
                                    com.vitalo.markrun.ui.common.GlobalOverlayManager.showCoinArrivedOverlay(0)
                                }
                            }
                        )
                    }
                }
                DailyTaskKind.NOTIFICATION -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val isGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                        if (isGranted) {
                            viewModel.markNotificationClaimed()
                            com.vitalo.markrun.ui.common.GlobalOverlayManager.showCoinArrivedOverlay(50)
                        } else {
                            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        viewModel.markNotificationClaimed()
                        com.vitalo.markrun.ui.common.GlobalOverlayManager.showCoinArrivedOverlay(50)
                    }
                }
                DailyTaskKind.MOTION_USAGE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val isGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
                        if (isGranted) {
                            viewModel.markMotionUsageClaimed()
                            com.vitalo.markrun.ui.common.GlobalOverlayManager.showCoinArrivedOverlay(50)
                        } else {
                            motionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                        }
                    } else {
                        viewModel.markMotionUsageClaimed()
                        com.vitalo.markrun.ui.common.GlobalOverlayManager.showCoinArrivedOverlay(50)
                    }
                }
                else -> {}
            }
        },
        onMultiDailyRelaxationGo = { index ->
            onNavigateToWebGameWithIndex("multiDailyRelaxation", index)
        },
        onTaskClaim = { viewModel.claimReward(it) }
    )
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── TaskScreenContent (Stateless Root — iOS TaskView) ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun TaskScreenContent(
    coinBalance: Int,
    chests: List<ChestDisplayItem>,
    dailyTasks: List<DailyTaskInfo>,
    taskStatus: DailyTaskStatus,
    multiRelaxTotalNum: Int = 3,
    multiRelaxCompletedIndices: List<Boolean> = listOf(false, false, false),
    onChestClaim: (Int) -> Unit = {},
    onTaskGo: (DailyTaskKind) -> Unit = {},
    onMultiDailyRelaxationGo: (Int) -> Unit = {},
    onTaskClaim: (DailyTaskKind) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.0f to PageBgStart,
                            1.0f to PageBgEnd,
                        ),
                        start = Offset(0.64f * size.width, 0.83f * size.height),
                        end = Offset(0.77f * size.width, 1.12f * size.height)
                    )
                )
            }
            .statusBarsPadding()
            .verticalScroll(scrollState)
    ) {
        TaskTopBarContent(coinBalance = coinBalance)

        Spacer(modifier = Modifier.height(22.dp))

        // ── Section: Limited-Time Chest ──
        SettingSectionView(
            title = "Limited-Time Chest",
            modifier = Modifier.padding(start = 21.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        TaskChestListView(
            chests = chests,
            onClaim = onChestClaim,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Section: Daily Tasks ──
        SettingSectionView(
            title = "Daily Tasks",
            modifier = Modifier.padding(start = 21.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        val navBarBottom = WindowInsets.navigationBars
            .asPaddingValues().calculateBottomPadding()

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 43.dp + navBarBottom + 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            dailyTasks.forEach { task ->
                when (task.kind) {
                    DailyTaskKind.CHEST -> { /* individual chests in chest list */ }
                    DailyTaskKind.MULTI_DAILY_RELAXATION -> {
                        TaskDailyRelaxationItemView(
                            task = task,
                            totalNum = multiRelaxTotalNum,
                            completedIndices = multiRelaxCompletedIndices,
                            rewardPerRound = task.reward,
                            onGoIndex = onMultiDailyRelaxationGo,
                            onClaim = { onTaskClaim(task.kind) }
                        )
                    }
                    else -> {
                        DailyTaskView(
                            task = task,
                            taskStatus = taskStatus,
                            onGo = { onTaskGo(task.kind) },
                            onClaim = { onTaskClaim(task.kind) }
                        )
                    }
                }
            }
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── TopBar + CoinBalancePill ────
// ═══════════════════════════════════════════════════════════════════════════════

@Suppress("UNUSED_PARAMETER")
@Composable
fun TaskTopBarContent(
    coinBalance: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .height(48.dp)
    ) {
        CoinBalanceView(
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = "Task Center",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TaskTitleColor,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── SettingSectionView (gradient dot + title) ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SettingSectionView(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(SectionDotStart, SectionDotEnd)
                    )
                )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── TaskChestListView (iOS TaskChestListView) ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun TaskChestListView(
    chests: List<ChestDisplayItem>,
    onClaim: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0xFF292929).copy(alpha = 0.1f),
                ambientColor = Color.Transparent
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(ChestContainerTop, ChestContainerMid, ChestContainerBottom)
                )
            )
            .border(0.5.dp, Color.White.copy(alpha = 0.48f), RoundedCornerShape(20.dp))
            .padding(top = 15.dp, bottom = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            chests.forEach { chest ->
                TaskChestItemView(
                    chest = chest,
                    onClaim = { onClaim(chest.index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── TaskChestItemView (iOS TaskChestItemView) ────
// ═══════════════════════════════════════════════════════════════════════════════

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun TaskChestItemView(
    chest: ChestDisplayItem,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isClaimable = chest.state == ChestState.CLAIMABLE
    val isClaimed = chest.state == ChestState.CLAIMED
    val isExpired = chest.state == ChestState.EXPIRED

    val bgResName = when (chest.state) {
        ChestState.CLAIMED -> "img_task_chest_bg_claimed"
        ChestState.EXPIRED -> "img_task_chest_bg_expired"
        else -> "img_task_chest_bg_waiting"
    }
    val chestResName = when (chest.state) {
        ChestState.CLAIMED -> "img_task_chest_claimed"
        ChestState.EXPIRED -> "img_task_chest_expired"
        else -> "img_task_chest_waiting"
    }

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(68.59f / 91.45f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = isClaimable
            ) { onClaim() }
    ) {
        val cardW = maxWidth
        val cardH = maxHeight

        // Background image
        TaskImage(
            resName = bgResName,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.FillBounds,
            placeholderContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                isClaimed -> Color(0xFFFFE0B2)
                                isExpired -> Color(0xFFE0E0E0)
                                else -> Color(0xFFFFF8E1)
                            }
                        )
                )
            }
        )

        // Chest image
        TaskImage(
            resName = chestResName,
            modifier = Modifier
                .size(cardW * 79f / 68f)
                .align(Alignment.TopCenter)
                .offset(y = (-12).dp),
            placeholderContent = {
                Box(
                    modifier = Modifier
                        .size(cardW * 79f / 68f)
                        .align(Alignment.TopCenter)
                        .offset(y = (-12).dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isClaimed -> Color(0xFFBCAAA4).copy(alpha = 0.5f)
                                isExpired -> Color(0xFFBDBDBD).copy(alpha = 0.5f)
                                isClaimable -> Color(0xFFFFF176).copy(alpha = 0.5f)
                                else -> Color(0xFFFFE082).copy(alpha = 0.5f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            isClaimed -> "✓"
                            isExpired -> "✕"
                            isClaimable -> "!"
                            else -> "?"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        )

        // Claimed coin text "+XXX"
        if (isClaimed) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = cardH * 0.51f)
            ) {
                Text(
                    text = "+${chest.reward}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    style = androidx.compose.ui.text.TextStyle(
                        drawStyle = androidx.compose.ui.graphics.drawscope.Stroke(
                            miter = 10f,
                            width = 4f,
                            join = androidx.compose.ui.graphics.StrokeJoin.Round
                        )
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "+${chest.reward}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    style = androidx.compose.ui.text.TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(ChestCoinGrad1, ChestCoinGrad2, ChestCoinGrad3)
                        )
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Bottom button
        val btnText = when (chest.state) {
            ChestState.WAITING, ChestState.NOT_READY -> chest.timeLabel
            ChestState.CLAIMABLE -> "Claim"
            ChestState.EXPIRED -> "Expired"
            ChestState.CLAIMED -> ""
        }

        val btnGradient = when (chest.state) {
            ChestState.CLAIMABLE -> Brush.horizontalGradient(listOf(ClaimGradientStart, ClaimGradientEnd))
            ChestState.CLAIMED -> Brush.horizontalGradient(listOf(ClaimedGradientStart, ClaimedGradientEnd))
            ChestState.EXPIRED -> Brush.horizontalGradient(listOf(ExpiredGradientStart, ExpiredGradientEnd))
            else -> Brush.horizontalGradient(listOf(ClaimedGradientStart, ClaimedGradientEnd))
        }

        val btnAlpha = when (chest.state) {
            ChestState.CLAIMABLE, ChestState.EXPIRED -> 1f
            else -> 0.5f
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-6).dp)
                .width(cardW * 0.88f)
                .height(21.dp)
                .alpha(btnAlpha)
                .clip(RoundedCornerShape(10.dp))
                .background(btnGradient),
            contentAlignment = Alignment.Center
        ) {
            if (isClaimed) {
                TaskImage(
                    resName = "ic_daily_chest_claimed",
                    modifier = Modifier.size(14.dp),
                    placeholderContent = {
                        Text("✓", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                )
            } else {
                Text(
                    text = btnText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = if (chest.state == ChestState.CLAIMABLE) Color.Black else Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── DailyTaskView (iOS DailyTaskView) ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun DailyTaskView(
    task: DailyTaskInfo,
    taskStatus: DailyTaskStatus = DailyTaskStatus.empty(),
    onGo: () -> Unit = {},
    onClaim: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val showGo = canShowGoButton(task)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(TaskCardBg)
            .padding(vertical = 16.dp)
            .padding(start = 6.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Task icon 46×46
        val iconResName = getIconResName(task.kind)
        TaskImage(
            resName = iconResName,
            modifier = Modifier.size(46.dp),
            contentScale = ContentScale.Crop,
            placeholderContent = {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(getTaskIconGradient(task.kind))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getTaskIconLabel(task.kind),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Task name + description
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = getTaskName(task.kind, taskStatus),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TaskNameColor,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = getTaskDesc(task.kind),
                fontSize = 11.sp,
                color = TaskDescColor,
                maxLines = 1
            )
        }

        // Coin reward + action button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (task.reward > 0 && !task.claimed) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TaskImage(
                        resName = "ic_daily_task_coin",
                        modifier = Modifier.size(width = 15.dp, height = 14.dp),
                        placeholderContent = {
                            Box(
                                modifier = Modifier
                                    .size(width = 15.dp, height = 14.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFD700)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("¢", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B6914))
                            }
                        }
                    )
                    Text(
                        text = "+${task.reward}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = CoinRewardColor
                    )
                }
            }
            TaskActionButton(
                canClaim = task.canClaim,
                claimed = task.claimed,
                showGoButton = showGo,
                kind = task.kind,
                onAction = {
                    when {
                        task.canClaim -> onClaim()
                        showGo -> onGo()
                    }
                }
            )
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── TaskDailyRelaxationItemView (iOS — 3 variants) ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun TaskDailyRelaxationItemView(
    task: DailyTaskInfo,
    totalNum: Int = 3,
    completedIndices: List<Boolean> = listOf(false, false, false),
    @Suppress("UNUSED_PARAMETER") rewardPerRound: Int = 400,
    onGoIndex: (Int) -> Unit = {},
    onClaim: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val completedCount = completedIndices.count { it }
    val allDone = completedCount >= totalNum
    val showGo = !task.canClaim && !task.claimed

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(163.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(RelaxCardBg)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        // Entry cards area
        when {
            totalNum >= 3 -> RelaxationVariantA(completedIndices, onGoIndex)
            totalNum >= 2 -> RelaxationVariantB(completedIndices, onGoIndex)
            else -> RelaxationVariantC(completedIndices.firstOrNull() == true, { onGoIndex(0) })
        }

        Spacer(modifier = Modifier.weight(1f))

        // Progress bar + button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RelaxationProgressBar(
                current = completedCount,
                total = totalNum,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            TaskActionButton(
                canClaim = allDone && !task.claimed,
                claimed = task.claimed,
                showGoButton = showGo && !allDone,
                kind = task.kind,
                onAction = {
                    when {
                        allDone && !task.claimed -> onClaim()
                        showGo && !allDone -> onGoIndex(completedIndices.indexOfFirst { !it }.coerceAtLeast(0))
                    }
                }
            )
        }
    }
}

@Composable
private fun RelaxationVariantA(
    completedIndices: List<Boolean>,
    onGoIndex: (Int) -> Unit
) {
    val entry1Done = completedIndices.getOrElse(0) { false }
    val entry2Done = completedIndices.getOrElse(1) { false }
    val entry3Done = completedIndices.getOrElse(2) { false }

    Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
        // Entry 1 — large left card
        RelaxationEntry(
            index = 0,
            isDone = entry1Done,
            bgRes = if (entry1Done) "bg_task_daily_relaxation_1_disable" else "bg_task_daily_relaxation_1",
            fgRes = "img_task_daily_relaxation_1",
            title = "Daily Bonus",
            subtitle = "Lucky Rewards Daily",
            titleSize = 14,
            modifier = Modifier
                .width(165.dp)
                .height(106.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = !entry1Done
                ) { onGoIndex(0) }
        )

        // Right column
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            RelaxationEntry(
                index = 1,
                isDone = entry2Done,
                bgRes = if (entry2Done) "bg_task_daily_relaxation_2_disable" else "bg_task_daily_relaxation_2",
                fgRes = "img_task_daily_relaxation_2",
                title = "20s Quick Win",
                titleSize = 12,
                modifier = Modifier
                    .width(135.dp)
                    .height(48.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !entry2Done
                    ) { onGoIndex(1) }
            )
            RelaxationEntry(
                index = 2,
                isDone = entry3Done,
                bgRes = if (entry3Done) "bg_task_daily_relaxation_2_disable" else "bg_task_daily_relaxation_3",
                fgRes = "img_task_daily_relaxation_3",
                title = "Multi Tasks",
                titleSize = 12,
                modifier = Modifier
                    .width(135.dp)
                    .height(48.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !entry3Done
                    ) { onGoIndex(2) }
            )
        }
    }
}

@Composable
private fun RelaxationVariantB(
    completedIndices: List<Boolean>,
    onGoIndex: (Int) -> Unit
) {
    val entry1Done = completedIndices.getOrElse(0) { false }
    val entry2Done = completedIndices.getOrElse(1) { false }

    Row(
        horizontalArrangement = Arrangement.spacedBy(11.dp),
        modifier = Modifier.padding(start = 4.dp, end = 3.dp)
    ) {
        RelaxationEntry(
            index = 0,
            isDone = entry1Done,
            bgRes = if (entry1Done) "bg_task_daily_relaxation_1_small_disable" else "bg_task_daily_relaxation_1_small",
            fgRes = "img_task_daily_relaxation_1_small",
            title = "Daily Bonus",
            subtitle = "Lucky Rewards Daily",
            titleSize = 14,
            modifier = Modifier
                .width(147.dp)
                .height(106.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = !entry1Done
                ) { onGoIndex(0) }
        )
        RelaxationEntry(
            index = 1,
            isDone = entry2Done,
            bgRes = if (entry2Done) "bg_task_daily_relaxation_2_disable" else "bg_task_daily_relaxation_2_large",
            fgRes = "img_task_daily_relaxation_2_large",
            title = "20s Quick Win",
            titleSize = 12,
            modifier = Modifier
                .width(147.dp)
                .height(106.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = !entry2Done
                ) { onGoIndex(1) }
        )
    }
}

@Composable
private fun RelaxationVariantC(
    isDone: Boolean,
    onGo: () -> Unit
) {
    RelaxationEntry(
        index = 0,
        isDone = isDone,
        bgRes = if (isDone) "bg_task_daily_relaxation_1_large_disable" else "bg_task_daily_relaxation_1_large",
        fgRes = "img_task_daily_relaxation_1_large",
        title = "Daily Fast Bonus",
        subtitle = "Complete Multiple Tasks In 20s",
        titleSize = 15,
        modifier = Modifier
            .width(311.dp)
            .height(106.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !isDone
            ) { onGo() }
    )
}

@Composable
private fun RelaxationEntry(
    @Suppress("UNUSED_PARAMETER") index: Int,
    isDone: Boolean,
    bgRes: String,
    fgRes: String,
    title: String,
    subtitle: String? = null,
    titleSize: Int = 12,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.clip(RoundedCornerShape(12.dp))) {
        // Background image
        TaskImage(
            resName = bgRes,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            placeholderContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isDone) Brush.horizontalGradient(listOf(Color(0xFFE8E8E8), Color(0xFFE8E8E8)))
                            else Brush.horizontalGradient(listOf(Color(0xFFFFF9C4), Color(0xFFC8E6C9)))
                        )
                )
            }
        )

        // Foreground image
        TaskImage(
            resName = fgRes,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 80.dp),
            contentScale = ContentScale.Fit
        )

        // Text overlay
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 14.dp, top = if (subtitle != null) 23.dp else 14.dp)
        ) {
            Text(
                text = title,
                fontSize = titleSize.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    color = Color.Gray.copy(alpha = 0.5f)
                )
            }
        }

        // Completed tick
        if (isDone) {
            TaskImage(
                resName = "ic_task_daily_relaxation_tick",
                modifier = Modifier
                    .size(21.dp)
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
                placeholderContent = {
                    Box(
                        modifier = Modifier
                            .size(21.dp)
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── Relaxation Progress Bar ────
// ═══════════════════════════════════════════════════════════════════════════════

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun RelaxationProgressBar(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (total > 0) current.toFloat() / total else 0f

    Box(
        modifier = modifier
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White),
        contentAlignment = Alignment.CenterStart
    ) {
        // Filled portion
        if (progress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress)
                    .height(10.dp)
                    .padding(start = 1.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(ProgressBarStart, ProgressBarEnd)
                        )
                    )
            )
        }

        // Progress text
        Text(
            text = "$current/$total",
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            fontStyle = FontStyle.Italic,
            color = ProgressTextColor,
            modifier = Modifier.align(Alignment.Center)
        )

        // Coin slider icon
        if (progress > 0f) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val sliderOffset = maxWidth * progress - 12.dp
                TaskImage(
                    resName = "ic_task_daily_relaxation_coin",
                    modifier = Modifier
                        .size(width = 24.dp, height = 22.dp)
                        .offset(x = sliderOffset)
                        .align(Alignment.CenterStart),
                    placeholderContent = {
                        Box(
                            modifier = Modifier
                                .size(width = 24.dp, height = 22.dp)
                                .offset(x = sliderOffset)
                                .align(Alignment.CenterStart)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD700)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("¢", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B6914))
                        }
                    }
                )
            }
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── TaskActionButton (all states) ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TaskActionButton(
    canClaim: Boolean,
    claimed: Boolean,
    showGoButton: Boolean,
    kind: DailyTaskKind = DailyTaskKind.RUNNING,
    onAction: () -> Unit
) {
    val isUpperStep = kind == DailyTaskKind.UPPER_STEP_CONVERSION

    val bgBrush = when {
        claimed -> Brush.horizontalGradient(listOf(ClaimedGradientStart, ClaimedGradientEnd))
        showGoButton && isUpperStep -> Brush.horizontalGradient(listOf(ClaimGradientStart, ClaimGradientEnd))
        showGoButton -> Brush.horizontalGradient(listOf(GoBtnBg, GoBtnBg))
        canClaim -> Brush.horizontalGradient(listOf(ClaimGradientStart, ClaimGradientEnd))
        else -> Brush.horizontalGradient(listOf(DisabledBtnBg, DisabledBtnBg))
    }

    val btnAlpha = if (claimed) 0.5f else 1f

    val textColor = when {
        claimed -> Color.White
        showGoButton && isUpperStep -> Color.Black
        showGoButton -> Color.White
        canClaim -> Color.Black
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .width(62.dp)
            .height(28.dp)
            .alpha(btnAlpha)
            .clip(RoundedCornerShape(14.dp))
            .background(bgBrush)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !claimed && (canClaim || showGoButton)
            ) { onAction() },
        contentAlignment = Alignment.Center
    ) {
        when {
            claimed -> {
                TaskImage(
                    resName = "ic_daily_chest_claimed",
                    modifier = Modifier.size(14.dp),
                    placeholderContent = {
                        Text("✓", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                )
            }
            showGoButton && isUpperStep -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    TaskImage(
                        resName = "ic_upper_limit_play",
                        modifier = Modifier.size(width = 13.dp, height = 11.dp),
                        placeholderContent = {
                            Text("▶", fontSize = 8.sp, color = Color.Black)
                        }
                    )
                    Text(
                        text = "Claim",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = textColor
                    )
                }
            }
            else -> {
                Text(
                    text = when {
                        showGoButton -> "GO"
                        canClaim -> "Claim"
                        else -> "Claim"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = textColor
                )
            }
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── TaskImage (dynamic drawable loader with placeholder) ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TaskImage(
    resName: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
    placeholderContent: @Composable () -> Unit = {}
) {
    val context = LocalContext.current
    val resId = remember(resName) {
        if (resName.isBlank()) 0
        else context.resources.getIdentifier(resName, "drawable", context.packageName)
    }

    if (resId != 0) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        placeholderContent()
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── Helper Functions ────
// ═══════════════════════════════════════════════════════════════════════════════

private val GO_BUTTON_KINDS = setOf(
    DailyTaskKind.SIGN_IN,
    DailyTaskKind.NEW_USER_SPIN,
    DailyTaskKind.CRACK_EGG,
    DailyTaskKind.LUCKY_SLOT,
    DailyTaskKind.NOTIFICATION,
    DailyTaskKind.MOTION_USAGE,
    DailyTaskKind.DAILY_RELAXATION,
    DailyTaskKind.MULTI_DAILY_RELAXATION,
    DailyTaskKind.UPPER_STEP_CONVERSION
)

private fun canShowGoButton(task: DailyTaskInfo): Boolean {
    return !task.canClaim && !task.claimed && task.kind in GO_BUTTON_KINDS
}

private fun getIconResName(kind: DailyTaskKind): String = when (kind) {
    DailyTaskKind.SIGN_IN -> "img_daily_task_sign"
    DailyTaskKind.CHEST_ALL -> "img_daily_task_chest"
    DailyTaskKind.NEW_USER_SPIN -> "img_daily_task_spin"
    DailyTaskKind.CRACK_EGG -> "img_daily_task_egg"
    DailyTaskKind.LUCKY_SLOT -> "img_daily_task_slot"
    DailyTaskKind.RUNNING -> "img_daily_task_running"
    DailyTaskKind.TRAINING -> "img_daily_task_workout"
    DailyTaskKind.NOTIFICATION -> "img_daily_task_notification"
    DailyTaskKind.MOTION_USAGE -> "img_daily_task_motion"
    DailyTaskKind.DAILY_RELAXATION -> "img_daily_task_relaxation"
    DailyTaskKind.UPPER_STEP_CONVERSION -> "img_daily_task_upper_limit"
    else -> ""
}

private fun getTaskName(kind: DailyTaskKind, status: DailyTaskStatus): String = when (kind) {
    DailyTaskKind.SIGN_IN -> "Daily Sign-in"
    DailyTaskKind.CHEST_ALL -> "Claim Treasure chests(${status.chestClaimed.count { it }}/4)"
    DailyTaskKind.NEW_USER_SPIN -> "Lucky Spin\uFF08${minOf(status.newUserSpinCount, 10)}/10)"
    DailyTaskKind.CRACK_EGG -> "Crack Golden Egg\uFF08${minOf(status.crackEggCount, 10)}/10)"
    DailyTaskKind.LUCKY_SLOT -> "Play Lucky Slot\uFF08${minOf(status.luckySlotCount, 10)}/10)"
    DailyTaskKind.RUNNING -> "Running Goal(${minOf(status.runningKcal, 200)}/200)"
    DailyTaskKind.TRAINING -> "Training Goal(${minOf(status.trainingMinutes, 30)}/30)"
    DailyTaskKind.NOTIFICATION -> "Daily Notification Permission"
    DailyTaskKind.MOTION_USAGE -> "Enable Sports and Health"
    DailyTaskKind.DAILY_RELAXATION -> "Daily Relaxation (${if (status.dailyRelaxationCompleted) 20 else 0}/20s)"
    DailyTaskKind.UPPER_STEP_CONVERSION -> "Increase the Step Limit"
    DailyTaskKind.MULTI_DAILY_RELAXATION -> ""
    DailyTaskKind.CHEST -> "Treasure Chest"
}

private fun getTaskDesc(kind: DailyTaskKind): String = when (kind) {
    DailyTaskKind.SIGN_IN -> "Sign in for 28 days and get rewards"
    DailyTaskKind.CHEST_ALL -> "Claim at 8/12/16/20 Daily"
    DailyTaskKind.NEW_USER_SPIN,
    DailyTaskKind.CRACK_EGG,
    DailyTaskKind.LUCKY_SLOT -> "Have fun and earn"
    DailyTaskKind.RUNNING -> "Burn 200 kcal by Running"
    DailyTaskKind.TRAINING -> "Finish 30 min Course Training"
    DailyTaskKind.NOTIFICATION -> "Get Daily Updates"
    DailyTaskKind.MOTION_USAGE -> "Open and convert more CaloCoin"
    DailyTaskKind.DAILY_RELAXATION -> "Relax 20s to Win Rewards"
    DailyTaskKind.UPPER_STEP_CONVERSION -> "Increase from 10,000 to 15,000"
    DailyTaskKind.MULTI_DAILY_RELAXATION -> ""
    DailyTaskKind.CHEST -> ""
}

private fun getTaskIconLabel(kind: DailyTaskKind): String = when (kind) {
    DailyTaskKind.RUNNING -> "RUN"
    DailyTaskKind.TRAINING -> "TRN"
    DailyTaskKind.SIGN_IN -> "SGN"
    DailyTaskKind.NEW_USER_SPIN -> "SPN"
    DailyTaskKind.CRACK_EGG -> "EGG"
    DailyTaskKind.LUCKY_SLOT -> "SLT"
    DailyTaskKind.NOTIFICATION -> "NTF"
    DailyTaskKind.MOTION_USAGE -> "MTN"
    DailyTaskKind.DAILY_RELAXATION -> "RLX"
    DailyTaskKind.MULTI_DAILY_RELAXATION -> "RLX"
    DailyTaskKind.UPPER_STEP_CONVERSION -> "STP"
    DailyTaskKind.CHEST -> "CHT"
    DailyTaskKind.CHEST_ALL -> "ALL"
}

private fun getTaskIconGradient(kind: DailyTaskKind): List<Color> = when (kind) {
    DailyTaskKind.RUNNING -> listOf(Color(0xFFB8FF5A), Color(0xFF7AE62B))
    DailyTaskKind.TRAINING -> listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
    DailyTaskKind.SIGN_IN -> listOf(Color(0xFF81D4FA), Color(0xFF2BADF4))
    DailyTaskKind.NEW_USER_SPIN -> listOf(Color(0xFFCE93D8), Color(0xFF9C27B0))
    DailyTaskKind.CRACK_EGG -> listOf(Color(0xFFFFE082), Color(0xFFFFB300))
    DailyTaskKind.LUCKY_SLOT -> listOf(Color(0xFFEF9A9A), Color(0xFFF44336))
    DailyTaskKind.NOTIFICATION -> listOf(Color(0xFFA5D6A7), Color(0xFF4CAF50))
    DailyTaskKind.MOTION_USAGE -> listOf(Color(0xFFB0BEC5), Color(0xFF78909C))
    DailyTaskKind.DAILY_RELAXATION -> listOf(Color(0xFFB2DFDB), Color(0xFF26A69A))
    DailyTaskKind.MULTI_DAILY_RELAXATION -> listOf(Color(0xFF80CBC4), Color(0xFF00897B))
    DailyTaskKind.UPPER_STEP_CONVERSION -> listOf(Color(0xFFC9FF6B), Color(0xFFFFED29))
    DailyTaskKind.CHEST -> listOf(Color(0xFFFFE082), Color(0xFFFFB300))
    DailyTaskKind.CHEST_ALL -> listOf(Color(0xFFC9FF6B), Color(0xFFFFED29))
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── Previews ────
// ═══════════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TaskScreenContentPreview() {
    VitaloTheme {
        TaskScreenContent(
            coinBalance = 12_580,
            chests = mockChests,
            dailyTasks = mockDailyTasks,
            taskStatus = mockStatus,
            multiRelaxTotalNum = 3,
            multiRelaxCompletedIndices = listOf(true, false, false)
        )
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun TaskTopBarPreview() {
    VitaloTheme {
        TaskTopBarContent(coinBalance = 12_580)
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun ChestListPreview() {
    VitaloTheme {
        TaskChestListView(chests = mockChests)
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun DailyTaskClaimablePreview() {
    VitaloTheme {
        DailyTaskView(
            task = DailyTaskInfo(DailyTaskKind.CRACK_EGG, canClaim = true, claimed = false, reward = 300),
            taskStatus = mockStatus
        )
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun DailyTaskGoPreview() {
    VitaloTheme {
        DailyTaskView(
            task = DailyTaskInfo(DailyTaskKind.NEW_USER_SPIN, canClaim = false, claimed = false, reward = 300),
            taskStatus = mockStatus
        )
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun DailyTaskDonePreview() {
    VitaloTheme {
        DailyTaskView(
            task = DailyTaskInfo(DailyTaskKind.SIGN_IN, canClaim = false, claimed = true, reward = 200),
            taskStatus = mockStatus
        )
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun DailyTaskUpperStepPreview() {
    VitaloTheme {
        DailyTaskView(
            task = DailyTaskInfo(DailyTaskKind.UPPER_STEP_CONVERSION, canClaim = false, claimed = false, reward = 0),
            taskStatus = mockStatus
        )
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun RelaxationItemPreview() {
    VitaloTheme {
        TaskDailyRelaxationItemView(
            task = DailyTaskInfo(DailyTaskKind.MULTI_DAILY_RELAXATION, canClaim = false, claimed = false, reward = 400),
            totalNum = 3,
            completedIndices = listOf(true, false, false)
        )
    }
}

// endregion
