package com.vitalo.markrun.ui.signin

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import com.vitalo.markrun.R
import com.vitalo.markrun.data.remote.model.SignInModel
import com.vitalo.markrun.data.remote.model.SignInRewardType
import com.vitalo.markrun.ui.common.CommonLottieView
import com.vitalo.markrun.ui.utils.StrokeTextView
import kotlinx.coroutines.launch

// -------------------- 样式常量（参照 iOS SignInStyle.swift） --------------------
private val TodayBgBrush = Brush.verticalGradient(
    0.0f to Color(red = 196 / 255f, green = 242 / 255f, blue = 33 / 255f, alpha = 1f),
    1.0f to Color(red = 255 / 255f, green = 237 / 255f, blue = 41 / 255f, alpha = 1f)
)

private val ExpiredBgBrush = Brush.verticalGradient(
    0.0f to Color(red = 255 / 255f, green = 189 / 255f, blue = 148 / 255f, alpha = 1f),
    1.0f to Color(red = 250 / 255f, green = 245 / 255f, blue = 194 / 255f, alpha = 1f)
)

private val FutureBgBrush = Brush.verticalGradient(
    0.0f to Color(red = 252 / 255f, green = 252 / 255f, blue = 231 / 255f, alpha = 1f),
    1.0f to Color(red = 255 / 255f, green = 242 / 255f, blue = 199 / 255f, alpha = 1f)
)

private val TodayBottomBrush = Brush.horizontalGradient(
    listOf(
        Color(red = 237 / 255f, green = 194 / 255f, blue = 0 / 255f, alpha = 1f),
        Color(red = 199 / 255f, green = 212 / 255f, blue = 0 / 255f, alpha = 1f),
        Color(red = 133 / 255f, green = 209 / 255f, blue = 0 / 255f, alpha = 1f)
    )
)

private val ExpiredBottomBrush = Brush.horizontalGradient(
    listOf(
        Color(red = 245 / 255f, green = 212 / 255f, blue = 117 / 255f, alpha = 1f),
        Color(red = 247 / 255f, green = 171 / 255f, blue = 105 / 255f, alpha = 1f)
    )
)

private val FutureBottomBrush = Brush.horizontalGradient(
    listOf(
        Color(0xFFF0E5AE),
        Color(0xFFFCE592)
    )
)

private val TodayBorderBrush = Brush.verticalGradient(
    0.0f to Color(0xFFFFFF00),
    0.5f to Color(0xFFFFBF35),
    1.0f to Color(0xFF97E10A)
)

// 奖励文字颜色
private val RewardTextColor = Color.White
private val DayLabelColorFuture = Color(0xFFD5BD9A)
private val DayLabelColorOther = Color.White

@Composable
fun SignInOverlay(
    onClose: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val signInData by viewModel.signInData.collectAsState()
    val remainingDays by viewModel.remainingDays.collectAsState()
    val currentWeekIndex by viewModel.currentWeekIndex.collectAsState()

    val pagerState = rememberPagerState(initialPage = currentWeekIndex) { 4 }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentWeekIndex) {
        if (pagerState.currentPage != currentWeekIndex) {
            pagerState.animateScrollToPage(currentWeekIndex)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .pointerInput(Unit){
                detectTapGestures {
                    //ignore
                }
            },
        contentAlignment = Alignment.Center
    ) {
            Box(
                modifier = Modifier
                    .size(372.dp, 544.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {}
            ) {
                // 主卡片背景
                Image(
                    painter = painterResource(id = R.drawable.img_sign_in_card_bg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SignInTitleView()

                    Spacer(modifier = Modifier.height(9.dp))

                    SignInRemainingDaysView(remainingDays = remainingDays)

                    Spacer(modifier = Modifier.height(10.dp))

                    // 日历容器
                    SignInCalendarContainer(
                        viewModel = viewModel,
                        signInData = signInData,
                        pagerState = pagerState,
                        onPageChange = { week ->
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(week)
                            }
                        }
                    )
                }

                // 顶部左侧关闭按钮（与 iOS 位置一致）
                Image(
                    painter = painterResource(id = R.drawable.ic_coin_arrived_dialog_close),
                    contentDescription = "Close",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 10.dp, y = (-35).dp)
                        .size(30.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onClose() }
                )
            }
        }
}

// -------------------- 标题 --------------------
@Composable
private fun SignInTitleView() {
    StrokeTextView(
        text = "Daily Sign-in",
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        strokeWidth = 14f,
        strokeColor = Color(0xFFE2611A), // 偏深橙/棕色的描边颜色
        textColor = Color.White,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.5f),
            offset = Offset(0f, 6f),
            blurRadius = 8f
        )
    )
}

@Composable
private fun SignInRemainingDaysView(remainingDays: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.5.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_sign_in_clock),
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = "Remaining days: $remainingDays days",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

// -------------------- 日历容器 --------------------
@Composable
private fun SignInCalendarContainer(
    viewModel: SignInViewModel,
    signInData: List<SignInModel>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onPageChange: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .width(320.dp)
            .height(408.dp)
    ) {
        // 内部日历背景
        Image(
            painter = painterResource(id = R.drawable.img_sign_in_calendar_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 标题栏 (Week x/4)
            SignInCalendarTitleBar(
                currentPage = pagerState.currentPage,
                onPrev = { if (pagerState.currentPage > 0) onPageChange(pagerState.currentPage - 1) },
                onNext = { if (pagerState.currentPage < 3) onPageChange(pagerState.currentPage + 1) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .width(311.dp)
                    .height(195.dp)
            ) { page ->
                val start = page * 7
                val end = minOf(start + 7, signInData.size)
                val weekData = if (start < signInData.size) signInData.subList(start, end) else emptyList()
                SignInCalendarGridView(
                    weekData = weekData,
                    viewModel = viewModel
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SignInCalendarPageIndicator(
                currentPage = pagerState.currentPage,
                pageCount = 4
            )

            Spacer(modifier = Modifier.height(20.dp))

            SignInChestGroupView(
                viewModel = viewModel,
                signInData = signInData
            )
        }
    }
}

@Composable
private fun SignInCalendarTitleBar(
    currentPage: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_sign_in_arrow),
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .graphicsLayer { rotationZ = 180f }
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onPrev() }
                .alpha(if (currentPage > 0) 1f else 0.3f)
        )
        Text(
            text = "Week (${currentPage + 1}/4)",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5A2A0E)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_sign_in_arrow),
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onNext() }
                .alpha(if (currentPage < 3) 1f else 0.3f)
        )
    }
}

@Composable
private fun SignInCalendarPageIndicator(currentPage: Int, pageCount: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until pageCount) {
            Box(
                modifier = Modifier
                    .size(if (i == currentPage) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (i == currentPage) Color(0xFFFFB400)
                        else Color(0xFFE0D6B6)
                    )
            )
        }
    }
}

// -------------------- 日历网格（4+3 布局） --------------------
@Composable
private fun SignInCalendarGridView(
    weekData: List<SignInModel>,
    viewModel: SignInViewModel
) {
    if (weekData.size < 7) return

    val spacing = 2.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 13.dp),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        val row1HasToday = weekData.subList(0, 4).any { it.isToday && !it.isSignedIn }
        val row2HasToday = weekData.subList(4, 7).any { it.isToday && !it.isSignedIn }

        // 第一行 4 个等宽
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(if (row1HasToday) 1f else 0f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            for (i in 0..3) {
                SignInCalendarCell(
                    model = weekData[i],
                    modifier = Modifier
                        .weight(1f)
                        .height(95.dp),
                    viewModel = viewModel,
                    isLarge = false
                )
            }
        }
        // 第二行前两个 + 大Cell
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(if (row2HasToday) 1f else 0f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            for (i in 4..5) {
                SignInCalendarCell(
                    model = weekData[i],
                    modifier = Modifier
                        .weight(1f)
                        .height(95.dp),
                    viewModel = viewModel,
                    isLarge = false
                )
            }
            SignInCalendarCell(
                model = weekData[6],
                modifier = Modifier
                    .weight(2f)
                    .height(95.dp),
                viewModel = viewModel,
                isLarge = true
            )
        }
    }
}

// -------------------- 单个签到 Cell --------------------
@Composable
private fun SignInCalendarCell(
    model: SignInModel,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel,
    isLarge: Boolean
) {
    val isSignedIn = model.isSignedIn
    val isToday = model.isToday && !isSignedIn
    val isExpired = model.isExpired
    val isFuture = !isToday && !isExpired && !isSignedIn

    val bgBrush = when {
        isToday -> TodayBgBrush
        isExpired -> ExpiredBgBrush
        else -> FutureBgBrush
    }

    val bottomBrush = when {
        isToday -> TodayBottomBrush
        isExpired -> ExpiredBottomBrush
        isFuture -> FutureBottomBrush
        else -> Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
    }

    val dayText = if (isExpired) "Re-sign" else "Day ${model.day}"
    val dayLabelColor = if (isFuture) DayLabelColorFuture else DayLabelColorOther
    val context = LocalContext.current

    val shape: Shape = RoundedCornerShape(9.dp)

    Box(
        modifier = modifier
            .zIndex(if (isToday) 1f else 0f) // 确保今天的Cell在最上层
            .clickable {
                if (isFuture) {
                    android.widget.Toast.makeText(context, "The reward is not available yet", android.widget.Toast.LENGTH_SHORT).show()
                } else if (isSignedIn) {
                    android.widget.Toast.makeText(context, "You have already received this reward", android.widget.Toast.LENGTH_SHORT).show()
                } else if (isToday || isExpired) {
                    val activity = context.findActivity()
                    activity?.let { act -> viewModel.signIn(act, model.day) }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // 今日边框高亮
        if (isToday) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.958f) // 91/95 ≈ 0.958
                    .padding(horizontal = 1.dp)
                    .border(
                        width = 2.dp,
                        brush = TodayBorderBrush,
                        shape = RoundedCornerShape(11.dp)
                    )
                    .align(Alignment.Center)
            )
        }

        // 内部 Cell
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.905f) // 86/95 ≈ 0.905
                .padding(horizontal = 4.dp)
                .clip(shape)
                .background(bgBrush)
                .align(Alignment.Center)
        ) {
            // 顶部 Day X 或 Re-sign
            if (!isSignedIn) {
                Text(
                    text = dayText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = dayLabelColor,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp)
                )
            }

            // 中间图标
            val iconRes = getCellIconRes(model, isToday, isLarge)
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .align(if (isLarge) Alignment.Center else Alignment.TopCenter)
                    .padding(top = if (isLarge) 0.dp else 16.dp)
                    .let {
                        if (isLarge) it.size(width = 120.dp, height = 78.dp)
                        else it.size(44.dp)
                    }
                    .let {
                        if (isSignedIn) it.blur(1.5.dp) else it
                    }
            )

            // 底部奖励标签
            if (!isSignedIn) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(23.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp, topEnd = 0.dp,
                                bottomStart = 9.dp, bottomEnd = 9.dp
                            )
                        )
                        .background(bottomBrush),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isFuture) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_sign_in_cell_ad),
                            contentDescription = null,
                            modifier = Modifier.size(width = 22.dp, height = 17.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                    Text(
                        text = getRewardDisplayText(model),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RewardTextColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 提示领取的手指动画（仅今天可领取时显示）
        if (isToday) {
                val infiniteTransition = rememberInfiniteTransition(label = "fingerBounce")
                val offsetY by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 6f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(500),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "fingerBounceOffset"
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_finger_guide),
                    contentDescription = "Finger Guide",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(x = 15.dp, y = (0f + offsetY).dp)
                        .size(40.dp)
                        .zIndex(10f) // 确保在最上层
                )
            }

        // 已签到：白色遮罩 + 对勾
        if (isSignedIn) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
                    .clip(shape)
                    .background(Color.White.copy(alpha = 0.2f))
            )
            Image(
                painter = painterResource(id = R.drawable.ic_sign_in_cell_tick),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(width = 18.dp, height = 13.dp)
            )
        }
    }
}

private fun getCellIconRes(model: SignInModel, isToday: Boolean, isLarge: Boolean): Int {
    return if (isLarge) {
        when {
            model.day == 14 -> R.drawable.ic_sign_in_cell_gift
            model.day == 28 -> R.drawable.ic_sign_in_cell_gift_pile
            model.rewardType == SignInRewardType.CASH -> R.drawable.ic_sign_in_cell_cash_pile
            else -> R.drawable.ic_sign_in_cell_coin_pile
        }
    } else {
        if (model.rewardType == SignInRewardType.CASH) {
            if (isToday) R.drawable.ic_sign_in_cell_cash_shining
            else R.drawable.ic_sign_in_cell_cash
        } else {
            if (isToday) R.drawable.ic_sign_in_cell_coin_shining
            else R.drawable.ic_sign_in_cell_coin
        }
    }
}

private fun getRewardDisplayText(model: SignInModel): String {
    if (model.day == 7 && model.rewardType == SignInRewardType.CASH) return "???"
    if (model.day == 14 || model.day == 28) return "???"
    return if (model.rewardType == SignInRewardType.CASH) {
        "+$${model.cashAmount ?: 0.01}"
    } else {
        "+${model.rewardAmount}"
    }
}

// -------------------- 累计签到宝箱 --------------------
@Composable
private fun SignInChestGroupView(
    viewModel: SignInViewModel,
    signInData: List<SignInModel>
) {
    val totalSignDays = signInData.count { it.isSignedIn }
    val claimedChests by viewModel.claimedChests.collectAsState()

    val progress2To7 = when {
        totalSignDays < 2 -> 0f
        totalSignDays >= 7 -> 1f
        else -> (totalSignDays - 2).toFloat() / (7 - 2)
    }

    val progress7To15 = when {
        totalSignDays < 7 -> 0f
        totalSignDays >= 15 -> 1f
        else -> (totalSignDays - 7).toFloat() / (15 - 7)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp),
        contentAlignment = Alignment.Center
    ) {
        // 进度条（在宝箱图标之下）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 60.dp)
                .offset(y = (-10).dp), // 提高进度条
            horizontalArrangement = Arrangement.spacedBy(29.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SignInChestProgressBar(
                progress = progress2To7,
                modifier = Modifier.weight(1f)
            )
            SignInChestProgressBar(
                progress = progress7To15,
                modifier = Modifier.weight(1f)
            )
        }

        // 三个宝箱
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val activity = context.findActivity()
            SignInChestItem(2, totalSignDays, claimedChests.contains(2)) {
                activity?.let { act -> viewModel.claimChest(act, 2) }
            }
            SignInChestItem(7, totalSignDays, claimedChests.contains(7)) {
                activity?.let { act -> viewModel.claimChest(act, 7) }
            }
            SignInChestItem(15, totalSignDays, claimedChests.contains(15)) {
                activity?.let { act -> viewModel.claimChest(act, 15) }
            }
        }
    }
}

@Composable
private fun SignInChestItem(
    requiredDays: Int,
    totalSignDays: Int,
    isClaimed: Boolean,
    onClick: () -> Unit
) {
    val isClaimable = !isClaimed && totalSignDays >= requiredDays

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(70.dp)
            .clickable(enabled = isClaimable) { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(
                    id = if (isClaimed) R.drawable.ic_sign_in_chest_opened
                    else R.drawable.ic_sign_in_chest
                ),
                contentDescription = "Chest",
                modifier = Modifier.size(61.dp),
                alpha = if (isClaimed) 0.7f else 1f
            )
            if (isClaimable) {
                Image(
                    painter = painterResource(id = R.drawable.ic_sign_in_chest_bubble),
                    contentDescription = null,
                    modifier = Modifier.size(68.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(0.dp)) // 缩小间距
        
        if (isClaimable) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(23.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            0.0f to Color(0xFFFC4D82),
                            0.54f to Color(0xFFFF8F45),
                            1.0f to Color(0xFFF7D129)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(start = 2.dp, end = 6.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_sign_in_chest_ad),
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 24.dp, height = 19.dp)
                        .offset(x = 2.dp)
                )
                Text(
                    text = "Claim",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        } else {
            Text(
                text = "$requiredDays day",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(red = 1f, green = 0.4f, blue = 0.27f).copy(
                    alpha = if (isClaimed) 1f else 0.4f
                ),
                modifier = Modifier.height(23.dp).wrapContentHeight(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun SignInChestProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(5.dp)
            .background(Color.Black.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = progress)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(red = 1f, green = 0.46f, blue = 0.41f, alpha = 1f),
                            Color(red = 0.98f, green = 0.94f, blue = 0.49f, alpha = 1f)
                        )
                    )
                )
        )
    }
}

private tailrec fun android.content.Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}
