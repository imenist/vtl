package com.vitalo.markrun.ui.exchange

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vitalo.markrun.data.mock.Card
import com.vitalo.markrun.data.mock.MockDataProvider
import com.vitalo.markrun.data.mock.WithdrawalAmount
import com.vitalo.markrun.navigation.Screen
import com.vitalo.markrun.ui.collection.SettingSectionView
import com.vitalo.markrun.ui.theme.VitaloTheme
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── Stateful Wrapper ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun ExchangeScreen(
    navController: NavController,
    viewModel: ExchangeViewModel = hiltViewModel()
) {
    val coinBalance by viewModel.coinBalance.collectAsState()

    ExchangeScreenContent(
        coinBalance = coinBalance.toInt(),
        coinExchangeRate = 1000.0,
        adEnable = true,
        adReward = 200,
        showWithdrawl = true,
        withdrawalAmounts = MockDataProvider.withdrawalAmounts.filter { !it.isExchanged }.take(4),
        totalRewardedAdViewCount = 12,
        totalSignDays = 7,
        shouldShowCountdown = true,
        countdownText = "18:32:45",
        cards = MockDataProvider.cards,
        fragmentProgress = MockDataProvider.fragmentProgress,
        onFaqClick = {},
        onRecordClick = { navController.navigate(Screen.WithdrawRecord.route) }
    )
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── ExchangeScreenContent ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun ExchangeScreenContent(
    coinBalance: Int,
    coinExchangeRate: Double,
    adEnable: Boolean,
    adReward: Int,
    showWithdrawl: Boolean,
    withdrawalAmounts: List<WithdrawalAmount>,
    totalRewardedAdViewCount: Int,
    totalSignDays: Int,
    shouldShowCountdown: Boolean,
    countdownText: String,
    @Suppress("UNUSED_PARAMETER") cards: List<Card>,
    @Suppress("UNUSED_PARAMETER") fragmentProgress: Map<Int, Int>,
    onFaqClick: () -> Unit = {},
    onRecordClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    Box(modifier = modifier.fillMaxSize()) {
        // Background layers
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .align(Alignment.TopCenter)
        ) {
            Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.img_exchange_bg),
    contentDescription = null,
    modifier = Modifier.fillMaxSize(),
    contentScale = ContentScale.FillBounds
)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f / 3f)
                .background(Color.White)
                .align(Alignment.BottomCenter)
        )

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding()
        ) {
            // Navigation bar area
            ExchangeTopBar(
                coinBalance = coinBalance,
                coinExchangeRate = coinExchangeRate,
                showWithdrawl = showWithdrawl,
                onFaqClick = onFaqClick,
                onRecordClick = onRecordClick
            )

            // Wallet card
            if (adEnable) {
                MyWalletWithAdView(
                    coinBalance = coinBalance,
                    coinExchangeRate = coinExchangeRate,
                    adReward = adReward,
                    screenWidth = screenWidthDp,
                    modifier = Modifier.padding(top = 22.dp)
                )
            } else {
                MyWalletView(
                    coinBalance = coinBalance,
                    screenWidth = screenWidthDp,
                    modifier = Modifier.padding(top = 38.75.dp)
                )
            }

            if (showWithdrawl) {
                Column(
                    modifier = Modifier
                        .padding(top = 27.dp)
                        .fillMaxWidth()
                        .background(
                            Color.White,
                            RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        )
                ) {
                    WithdrawlView(
                        amounts = withdrawalAmounts,
                        totalRewardedAdViewCount = totalRewardedAdViewCount,
                        totalSignDays = totalSignDays,
                        shouldShowCountdown = shouldShowCountdown,
                        countdownText = countdownText,
                        screenWidth = screenWidthDp,
                        screenHeight = screenHeightDp
                    )

                    // "Withdrawal Method" divider
                    WithdrawalMethodDivider(
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    WithdrawMethodsView(
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(124.dp))
                }
            } else {
                RedeemFragmentView(
                    adEnable = adEnable,
                    screenWidth = screenWidthDp
                )
            }
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── ExchangeTopBar ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ExchangeTopBar(
    coinBalance: Int,
    coinExchangeRate: Double,
    showWithdrawl: Boolean,
    onFaqClick: () -> Unit,
    onRecordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        // Left: CoinBalancePill
        CoinBalancePill(
            coinBalance = coinBalance,
            coinExchangeRate = coinExchangeRate,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        // Center: Title
        Text(
            text = "My Wallet",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0D120E),
            modifier = Modifier.align(Alignment.Center)
        )

        // Right: FAQ + Record icons
        if (showWithdrawl) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.ic_exchange_faq),
    contentDescription = null,
    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 10.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onFaqClick() },
    contentScale = ContentScale.Crop
)
                Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.ic_exchange_record),
    contentDescription = null,
    modifier = Modifier
                        .size(32.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onRecordClick() },
    contentScale = ContentScale.Crop
)
            }
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── CoinBalancePill ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun CoinBalancePill(
    coinBalance: Int,
    coinExchangeRate: Double,
    showExchange: Boolean = true,
    modifier: Modifier = Modifier
) {
    val cornerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    val formattedBalance = remember(coinBalance) {
        NumberFormat.getIntegerInstance().format(coinBalance)
    }
    val hasRate = coinExchangeRate > 0

    Row(
        modifier = modifier
            .height(48.dp)
            .clip(cornerShape)
            .background(
                Brush.horizontalGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xFFA1C21C),
                        0.5f to Color(0xFFA3C93B),
                        1.0f to Color(0xFF2E824F)
                    )
                )
            )
            .border(0.5.dp, Color.White.copy(alpha = 0.4f), cornerShape)
            .padding(start = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!hasRate) {
            // Simple mode: icon + coin number
            Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.ic_coin_balance),
    contentDescription = null,
    modifier = Modifier.size(24.dp),
    contentScale = ContentScale.Crop
)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formattedBalance,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = Color.White
            )
        } else {
            // Rate mode: VStack with smaller icon+coin and ≈$xx
            Column(
                modifier = Modifier.padding(start = 4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.ic_coin_balance),
    contentDescription = null,
    modifier = Modifier.size(width = 16.8.dp, height = 18.dp),
    contentScale = ContentScale.Crop
)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = formattedBalance,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                val dollarValue = if (coinExchangeRate > 0) coinBalance / coinExchangeRate else 0.0
                Text(
                    text = "≈ $${String.format(Locale.US, "%.2f", dollarValue)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFED42)
                )
            }
        }

        if (showExchange) {
            Spacer(modifier = Modifier.width(6.dp))
            // Redeem button
            Box(
                modifier = Modifier
                    .height(34.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFFEF3E),
                                Color(0xFFF7FFBB),
                                Color(0xFFCDFF76)
                            )
                        )
                    )
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Redeem",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF324B1A)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        } else {
            Spacer(modifier = Modifier.width(25.dp))
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── MyWalletWithAdView ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun MyWalletWithAdView(
    coinBalance: Int,
    coinExchangeRate: Double,
    adReward: Int,
    screenWidth: Dp,
    modifier: Modifier = Modifier
) {
    @Suppress("UNUSED_VARIABLE") val cardWidth = screenWidth - 40.dp
    val hasRate = coinExchangeRate > 0
    val cardHeight = if (hasRate) 176.dp else 198.dp
    val formattedCoin = remember(coinBalance) {
        NumberFormat.getIntegerInstance().format(coinBalance)
    }

    Box(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(cardHeight)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, Color.White.copy(alpha = 0.75f))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
    ) {
        // Background mark (bottom-trailing)
        if (hasRate) {
            Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.img_exchange_wallet_card_bg_mark),
    contentDescription = null,
    modifier = Modifier
                    .size(141.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 20.dp, y = 15.dp),
    contentScale = ContentScale.Crop
)
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // "My CaloCoin" label
            Text(
                text = "My CaloCoin",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0D120E),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 22.dp, top = 14.dp)
            )

            if (hasRate) {
                // Rate mode: coin ≈ $xx
                Row(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .height(43.dp)
                        .padding(horizontal = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                            Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.img_exchange_coin_for_ad),
    contentDescription = null,
    modifier = Modifier.size(25.dp),
    contentScale = ContentScale.Crop
)
                        }
                        Text(
                            text = formattedCoin,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0D120E)
                        )
                    }
                    Text(
                        text = "≈",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0D120E)
                    )
                    val dollarValue = if (coinExchangeRate > 0) coinBalance / coinExchangeRate else 0.0
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", dollarValue)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFFFE6544)
                    )
                }
            } else {
                // No rate mode: large coin display
                Row(
                    modifier = Modifier
                        .padding(top = 24.dp, start = 22.dp, end = 22.dp)
                        .height(52.5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.6.dp)
                ) {
                    Box(modifier = Modifier.size(width = 50.4.dp, height = 52.5.dp), contentAlignment = Alignment.Center) {
                        Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.img_exchange_coin_for_ad),
    contentDescription = null,
    modifier = Modifier.size(75.6.dp),
    contentScale = ContentScale.Fit
)
                    }
                    Text(
                        text = formattedCoin,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF37511B)
                    )
                }
            }

            // "Earn" button
            Box(
                modifier = Modifier
                    .padding(top = 17.dp)
                    .size(width = 192.dp, height = 47.dp)
                    .clip(RoundedCornerShape(23.5.dp))
                    .background(Color.Black)
                    .clickable { /* TODO: Watch ad */ },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.ic_exchange_ad_play),
    contentDescription = null,
    modifier = Modifier.size(width = 16.dp, height = 13.dp),
    contentScale = ContentScale.Crop
)
                    Spacer(modifier = Modifier.width(2.5.dp))
                    Text(
                        text = "Earn",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.5.dp))
                    Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.ic_exchange_ad_coin),
    contentDescription = null,
    modifier = Modifier.size(width = 18.dp, height = 19.dp),
    contentScale = ContentScale.Crop
)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "$adReward",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── MyWalletView (no ad) ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
@Suppress("UNUSED_PARAMETER")
private fun MyWalletView(
    coinBalance: Int,
    screenWidth: Dp,
    modifier: Modifier = Modifier
) {
    val formattedCoin = remember(coinBalance) {
        NumberFormat.getIntegerInstance().format(coinBalance)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(26.dp)
    ) {
        Column(
            modifier = Modifier.width(139.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "My CaloCoin",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF0D120E)
            )
            Text(
                text = formattedCoin,
                fontSize = 48.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF37511B),
                maxLines = 1
            )
        }

        Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.img_exchange_coin_large),
    contentDescription = null,
    modifier = Modifier.size(111.dp),
    contentScale = ContentScale.Fit
)
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── WithdrawlView ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun WithdrawlView(
    amounts: List<WithdrawalAmount>,
    totalRewardedAdViewCount: Int,
    totalSignDays: Int,
    shouldShowCountdown: Boolean,
    countdownText: String,
    screenWidth: Dp,
    screenHeight: Dp,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SettingSectionView(
            title = "Withdraw",
            modifier = Modifier
                .padding(start = 20.dp)
                .padding(top = (20.dp * screenHeight.value / 812f))
        )

        // 2-column grid of withdrawal cells
        val cellWidth = (screenWidth - 40.dp - 11.dp) / 2
        val displayAmounts = amounts.take(4)

        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            for (rowIndex in displayAmounts.indices step 2) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    WithdrawalCellView(
                        amount = displayAmounts[rowIndex],
                        cellWidth = cellWidth,
                        totalRewardedAdViewCount = totalRewardedAdViewCount,
                        totalSignDays = totalSignDays,
                        shouldShowCountdown = shouldShowCountdown,
                        countdownText = countdownText
                    )
                    if (rowIndex + 1 < displayAmounts.size) {
                        WithdrawalCellView(
                            amount = displayAmounts[rowIndex + 1],
                            cellWidth = cellWidth,
                            totalRewardedAdViewCount = totalRewardedAdViewCount,
                            totalSignDays = totalSignDays,
                            shouldShowCountdown = shouldShowCountdown,
                            countdownText = countdownText
                        )
                    } else {
                        Spacer(modifier = Modifier.width(cellWidth))
                    }
                }
            }
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── WithdrawalCellView ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun WithdrawalCellView(
    amount: WithdrawalAmount,
    cellWidth: Dp,
    totalRewardedAdViewCount: Int,
    totalSignDays: Int,
    shouldShowCountdown: Boolean,
    countdownText: String,
    modifier: Modifier = Modifier
) {
    val cellAspectRatio = 162.0f / 134.0f
    val cellHeight = cellWidth / cellAspectRatio
    val cardAspectRatio = 162.0f / 98.0f
    val cardHeight = cellWidth / cardAspectRatio
    val buttonHeight = cellHeight * (30.0f / 134.0f)
    val bgMarkWidth = cellWidth * (94.0f / 162.0f)
    val bgMarkHeight = cellHeight * (82.0f / 134.0f)

    val isQuick = amount.withdrawType == 0
    val isExchanged = amount.isExchanged
    val underReview = isQuick && false // TODO: check review state
    val highlight = isQuick && !isExchanged
    val hasWatchAd = (amount.watchAdTimes ?: 0) > 0
    val hasSignDays = amount.limitSignDays > 0

    // Format currency
    val formattedCurrency = remember(amount.realCurrency) {
        if (amount.realCurrency < 1.0) {
            "$${String.format(Locale.US, "%.2f", amount.realCurrency)}"
        } else {
            "$${amount.realCurrency.toInt()}"
        }
    }
    val coinAmountText = remember(amount.coinAmountV2) {
        NumberFormat.getIntegerInstance().format(amount.coinAmountV2.toInt())
    }

    // Button text logic
    val buttonText = when {
        hasWatchAd && totalRewardedAdViewCount < (amount.watchAdTimes ?: 0) ->
            "$totalRewardedAdViewCount/${amount.watchAdTimes}"
        hasSignDays && totalSignDays < amount.limitSignDays ->
            "$totalSignDays/${amount.limitSignDays}"
        isExchanged -> "Received"
        underReview -> "Pending..."
        else -> "CASH OUT"
    }

    Column(
        modifier = modifier
            .width(cellWidth)
            .height(cellHeight)
    ) {
        // Upper card area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .then(
                    if (highlight && !underReview) {
                        Modifier.border(
                            width = 2.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFD4D82),
                                    Color(0xFFFF8F45),
                                    Color(0xFFF8D229)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    } else Modifier
                )
                .clip(RoundedCornerShape(19.dp))
                .background(
                    when {
                        underReview -> Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFC6DB1F).copy(alpha = 0.2f),
                                Color(0xFF08C261).copy(alpha = 0.2f)
                            )
                        )
                        highlight -> Brush.verticalGradient(
                            colors = listOf(Color(0xFFFCFFF8), Color(0xFFFCF28C))
                        )
                        else -> Brush.verticalGradient(
                            colors = listOf(Color(0xFFF4F3EE), Color(0xFFF4F3EE))
                        )
                    }
                )
        ) {
            // Background mark
            Image(
    painter = painterResource(id = if (underReview) com.vitalo.markrun.R.drawable.img_exchange_option_bg_mark_review
                else com.vitalo.markrun.R.drawable.img_exchange_option_bg_mark),
    contentDescription = null,
    modifier = Modifier
                    .size(width = bgMarkWidth, height = bgMarkHeight)
                    .align(Alignment.BottomEnd),
    contentScale = ContentScale.Crop
)

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Currency amount
                Text(
                    text = formattedCurrency,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D120D).copy(
                        alpha = when {
                            isExchanged -> 0.2f
                            underReview -> 0.6f
                            else -> 1f
                        }
                    ),
                    modifier = Modifier.padding(top = 6.dp)
                )

                // Coin amount
                Row(
                    modifier = Modifier
                        .height(if (hasSignDays) 19.dp else 24.dp)
                        .alpha(if (isExchanged) 0.2f else 1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.ic_exchange_option_coin),
    contentDescription = null,
    modifier = Modifier.size(20.dp),
    contentScale = ContentScale.Crop
)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = coinAmountText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFE6544)
                    )
                }

                // Sign-in days requirement
                if (hasSignDays) {
                    Text(
                        text = "SIGN IN ${amount.limitSignDays} DAYS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF7D4705),
                        modifier = Modifier
                            .height(16.dp)
                            .alpha(if (isExchanged) 0.2f else 1f)
                    )
                }
            }

            // Countdown badge (top-right)
            if (highlight && isQuick && shouldShowCountdown) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-1).dp, y = 0.5.dp)
                        .height(22.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 11.dp,
                                bottomStart = 11.dp,
                                bottomEnd = 0.dp
                            )
                        )
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFF8C72E),
                                    Color(0xFFFF8F45),
                                    Color(0xFFFF874A)
                                )
                            )
                        )
                        .padding(start = 9.dp, end = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.ic_exchange_option_timer),
    contentDescription = null,
    modifier = Modifier.size(10.dp),
    contentScale = ContentScale.Crop
)
                        Text(
                            text = countdownText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.width(53.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight)
                .clip(RoundedCornerShape(buttonHeight / 2))
                .background(
                    when {
                        underReview -> Brush.horizontalGradient(
                            listOf(Color(0xFF54C98C), Color(0xFF54C98C))
                        )
                        highlight -> Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFD4D82),
                                Color(0xFFFF8F45),
                                Color(0xFFF8D229)
                            )
                        )
                        isExchanged -> Brush.horizontalGradient(
                            listOf(Color.Gray.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.5f))
                        )
                        else -> Brush.horizontalGradient(
                            listOf(Color(0xFFD7D7C7), Color(0xFFD7D7C7))
                        )
                    }
                )
                .alpha(if (isExchanged) 0.2f else 1f)
                .clickable(enabled = !isExchanged) { /* TODO: Withdraw */ },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = buttonText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = when {
                    highlight -> Color.White
                    isExchanged -> Color.Gray
                    else -> Color(0xFF0D120D)
                }
            )
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── Withdrawal Method Divider ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun WithdrawalMethodDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(1.dp)
                .background(Color(0xFFEBEBEB))
        )
        Text(
            text = "Withdrawal Method",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFD7D5C7),
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(1.dp)
                .background(Color(0xFFEBEBEB))
        )
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── WithdrawMethodsView ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun WithdrawMethodsView(modifier: Modifier = Modifier) {
    val row1 = listOf(
        com.vitalo.markrun.R.drawable.img_pay_methods_paypal, com.vitalo.markrun.R.drawable.img_pay_methods_amazon, com.vitalo.markrun.R.drawable.img_pay_methods_master,
        com.vitalo.markrun.R.drawable.img_pay_methods_paypay, com.vitalo.markrun.R.drawable.img_pay_methods_zain, com.vitalo.markrun.R.drawable.img_pay_methods_visa,
        com.vitalo.markrun.R.drawable.img_pay_methods_stc
    )
    val row2 = listOf(
        com.vitalo.markrun.R.drawable.img_pay_methods_google, com.vitalo.markrun.R.drawable.img_pay_methods_line, com.vitalo.markrun.R.drawable.img_pay_methods_boleto,
        com.vitalo.markrun.R.drawable.img_pay_methods_pay, com.vitalo.markrun.R.drawable.img_pay_methods_dana, com.vitalo.markrun.R.drawable.img_pay_methods_paytm,
        com.vitalo.markrun.R.drawable.img_pay_methods_dragonpay
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 34.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PaymentMethodRow(icons = row1)
        PaymentMethodRow(icons = row2)
    }
}

@Composable
private fun PaymentMethodRow(icons: List<Int>) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        icons.forEach { iconName ->
            Box(
                modifier = Modifier
                    .size(width = 90.dp, height = 30.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFFEBEBEB), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
    painter = painterResource(id = iconName),
    contentDescription = null,
    modifier = Modifier.height(22.dp),
    contentScale = ContentScale.Fit
)
            }
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── RedeemFragmentView ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
@Suppress("UNUSED_PARAMETER")
private fun RedeemFragmentView(
    adEnable: Boolean,
    screenWidth: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(top = if (adEnable) 20.dp else 42.dp)
    ) {
        SettingSectionView(
            title = "Redeem Fragments",
            modifier = Modifier.padding(start = 20.dp, top = 20.dp)
        )

        // Exchange illustration card
        Box(
            modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.CenterHorizontally)
                .size(width = 323.dp, height = 302.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Background
            Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.img_exchange_card_bg),
    contentDescription = null,
    modifier = Modifier.fillMaxSize(),
    contentScale = ContentScale.FillBounds
)

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Coin → Arrow → Fragment row
                Row(
                    modifier = Modifier.padding(top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy((-6).dp)
                ) {
                    // Coin
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy((-12).dp)
                    ) {
                        Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.img_exchange_coin),
    contentDescription = null,
    modifier = Modifier.size(115.dp),
    contentScale = ContentScale.Fit
)
                        Text(
                            text = "100 CaloCoins",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF5D5C1B)
                        )
                    }

                    // Arrow
                    Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.img_exchange_arrow),
    contentDescription = null,
    modifier = Modifier
                            .size(width = 47.dp, height = 32.dp)
                            .offset(y = (-6).dp),
    contentScale = ContentScale.Crop
)

                    // Fragment
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy((-12).dp)
                    ) {
                        Box {
                            Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.img_exchange_fragment),
    contentDescription = null,
    modifier = Modifier.size(115.dp),
    contentScale = ContentScale.Fit
)
                            // "x1" badge
                            Text(
                                text = "x1",
                                fontSize = 19.2.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 17.dp, end = 9.dp)
                            )
                        }
                        Text(
                            text = "Mysterious\nFragments",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF5D5C1B),
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Tips bar
                Row(
                    modifier = Modifier
                        .padding(horizontal = 21.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFFEE6))
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
    painter = painterResource(id = com.vitalo.markrun.R.drawable.ic_exchange_tips),
    contentDescription = null,
    modifier = Modifier.size(25.6.dp),
    contentScale = ContentScale.Crop
)
                    Text(
                        text = "After the exchange is successful, the fragments will be automatically turned out",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9B8C19),
                        lineHeight = 12.sp
                    )
                }

                // "Redeem" button
                Box(
                    modifier = Modifier
                        .padding(top = 21.dp)
                        .size(width = 230.dp, height = 56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.Black)
                        .clickable { /* TODO: Check coin >= 100 */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Redeem",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(124.dp))
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── Previews ────
// ═══════════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ExchangeScreenWithdrawlPreview() {
    VitaloTheme {
        ExchangeScreenContent(
            coinBalance = 9380,
            coinExchangeRate = 1000.0,
            adEnable = true,
            adReward = 200,
            showWithdrawl = true,
            withdrawalAmounts = MockDataProvider.withdrawalAmounts.filter { !it.isExchanged }.take(4),
            totalRewardedAdViewCount = 12,
            totalSignDays = 7,
            shouldShowCountdown = true,
            countdownText = "18:32:45",
            cards = MockDataProvider.cards,
            fragmentProgress = MockDataProvider.fragmentProgress
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ExchangeScreenRedeemPreview() {
    VitaloTheme {
        ExchangeScreenContent(
            coinBalance = 9380,
            coinExchangeRate = 1000.0,
            adEnable = true,
            adReward = 200,
            showWithdrawl = false,
            withdrawalAmounts = emptyList(),
            totalRewardedAdViewCount = 0,
            totalSignDays = 0,
            shouldShowCountdown = false,
            countdownText = "",
            cards = MockDataProvider.cards,
            fragmentProgress = MockDataProvider.fragmentProgress
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyWalletWithAdPreview() {
    VitaloTheme {
        MyWalletWithAdView(
            coinBalance = 9380,
            coinExchangeRate = 1000.0,
            adReward = 200,
            screenWidth = 375.dp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WithdrawalCellPreview() {
    VitaloTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            WithdrawalCellView(
                amount = MockDataProvider.withdrawalAmounts[0],
                cellWidth = 162.dp,
                totalRewardedAdViewCount = 12,
                totalSignDays = 7,
                shouldShowCountdown = true,
                countdownText = "18:32:45"
            )
            WithdrawalCellView(
                amount = MockDataProvider.withdrawalAmounts[2],
                cellWidth = 162.dp,
                totalRewardedAdViewCount = 12,
                totalSignDays = 7,
                shouldShowCountdown = false,
                countdownText = ""
            )
        }
    }
}

// endregion
