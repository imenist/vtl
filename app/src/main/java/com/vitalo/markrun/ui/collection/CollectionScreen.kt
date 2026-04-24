package com.vitalo.markrun.ui.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vitalo.markrun.data.mock.Card
import com.vitalo.markrun.data.mock.CardEvent
import com.vitalo.markrun.data.mock.MockDataProvider
import com.vitalo.markrun.ui.theme.VitaloTheme
import com.vitalo.markrun.R

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── Stateful Wrapper ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun CollectionScreen() {
    var showIntro by remember { mutableStateOf(false) }

    CollectionScreenContent(
        cards = MockDataProvider.cards,
        fragmentProgress = MockDataProvider.fragmentProgress,
        cardEvents = MockDataProvider.cardEvents,
        onInfoClick = { showIntro = true }
    )

    if (showIntro) {
        CollectionIntroOverlay(onDismiss = { showIntro = false })
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── CollectionScreenContent ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun CollectionScreenContent(
    cards: List<Card>,
    fragmentProgress: Map<Int, Int>,
    @Suppress("UNUSED_PARAMETER") cardEvents: List<CardEvent> = emptyList(),
    onInfoClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val headerHeight = 106.dp * (screenWidthDp.value / 375f) + statusBarHeight

    var selectedCard by remember { mutableStateOf<Card?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = headerHeight + 22.dp,
                bottom = 43.dp + WindowInsets.navigationBars
                    .asPaddingValues().calculateBottomPadding() + 20.dp
            ),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            items(cards, key = { it.id }) { card ->
                val collected = fragmentProgress[card.fragmentId] ?: 0
                val unlocked = collected >= card.fragmentRequiredNum

                CardGridItem(
                    card = card,
                    collected = collected,
                    unlocked = unlocked,
                    screenWidth = screenWidthDp,
                    onClick = { if (unlocked) selectedCard = card }
                )
            }
        }

        CollectionHeader(
            onInfoClick = onInfoClick,
            headerHeight = headerHeight,
            screenWidth = screenWidthDp,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    selectedCard?.let { card ->
        CardDetailOverlay(
            card = card,
            onDismiss = { selectedCard = null }
        )
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── CollectionHeader ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
@Suppress("UNUSED_PARAMETER")
private fun CollectionHeader(
    onInfoClick: () -> Unit,
    headerHeight: Dp,
    screenWidth: Dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.img_collection_top_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Collection",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Box(
                modifier = Modifier
                    .size(width = 42.dp, height = 20.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onInfoClick() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_collection_info),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── CardGridItem ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun CardGridItem(
    card: Card,
    collected: Int,
    unlocked: Boolean,
    screenWidth: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardWidth = (screenWidth - 40.dp - 15.dp) / 2
    val cardHeight = cardWidth * (220f / 160f)
    val imageSize = cardWidth * (120f / 160f)
    val progressPercent = if (card.fragmentRequiredNum > 0)
        collected.toFloat() / card.fragmentRequiredNum else 0f

    Box(
        modifier = modifier
            .size(width = cardWidth, height = cardHeight)
            .clip(RoundedCornerShape(20.dp))
    ) {
        // Layer 1: Unlock gradient background
        if (unlocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.00f to Color.White,
                                0.13f to Color(0xFFEBFFE3),
                                0.32f to Color.White,
                                0.59f to Color(0xFFFCFFEB),
                                0.86f to Color(0xFFFAFFAE).copy(alpha = 0f),
                                0.96f to Color.White
                            )
                        )
                    )
            )
        }

        // Layer 2: Default card background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color(0xFFE8E8E8)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.01f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_collection_bg_lock),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.1f),
                contentScale = ContentScale.Crop
            )
        }

        // Layer 3: Content
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card name
            Text(
                text = card.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF0D120E),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 11.dp)
            )

            // Card image area
            Box(
                modifier = Modifier
                    .padding(top = 8.5.dp)
                    .size(imageSize)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color(0xFFE8E8E8)
                    )
                    .clip(RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Background image
                Image(
                    painter = painterResource(id = R.drawable.img_collection_bg_item),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.1f),
                    contentScale = ContentScale.Crop
                )

                // Card image with opacity/blur based on progress
                val blurRadius = 10.dp * (1f - progressPercent.coerceIn(0f, 1f))
                Image(
                    painter = painterResource(id = card.cardImageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(RoundedCornerShape(20.dp))
                        .alpha(progressPercent.coerceIn(0f, 1f))
                        .blur(radius = blurRadius),
                    contentScale = ContentScale.Crop
                )

                // Lock icon
                if (!unlocked) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_collection_lock),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Unlocked: Check button / Locked: Progress bar
            if (unlocked) {
                Box(
                    modifier = Modifier
                        .padding(top = 13.5.dp)
                        .size(width = 118.dp, height = 32.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF0D120E))
                        .clickable { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Check",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                LockProgressView(
                    progress = collected,
                    total = card.fragmentRequiredNum,
                    fragmentImageRes = card.fragmentImageRes,
                    modifier = Modifier.padding(top = 11.dp)
                )
            }
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── LockProgressView ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun LockProgressView(
    progress: Int,
    total: Int,
    fragmentImageRes: Int,
    modifier: Modifier = Modifier
) {
    val percentage = if (total > 0) progress.toFloat() / total else 0f
    val totalWidth = 116.dp
    val fillWidth = if (progress > 0) (totalWidth - 29.dp) * percentage + 20.3.dp else 0.dp
    val isFull = percentage >= 1f
    val fillShape = if (isFull) RoundedCornerShape(20.dp)
    else RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)

    Box(
        modifier = modifier.size(width = totalWidth, height = 22.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Background track
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0D120E).copy(alpha = 0.1f))
        )

        // Progress fill
        if (fillWidth > 0.dp) {
            Box(
                modifier = Modifier
                    .width(fillWidth)
                    .height(22.dp)
                    .clip(fillShape)
                    .background(Color(0xFFE2FF04))
            )
        }

        // Fragment icon overlay (leading)
        Image(
            painter = painterResource(id = fragmentImageRes),
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .offset(x = (-7).dp)
                .align(Alignment.CenterStart),
            contentScale = ContentScale.Crop
        )

        // Center text
        Text(
            text = "$progress/$total",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D120E).copy(alpha = 0.2f),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── CardDetailOverlay ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun CardDetailOverlay(
    card: Card,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.width(265.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                // Glow effect behind the card
                Image(
                    painter = painterResource(id = R.drawable.img_collection_detail_glow),
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 256.dp, height = 236.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 54.dp),
                    contentScale = ContentScale.Crop
                )

                // Main card container
                Column(
                    modifier = Modifier
                        .width(256.dp)
                        .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFE8E8E8))
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.White, Color.White.copy(alpha = 0.06f))
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFC9FF69),
                                    Color(0xFFFDED2B)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {},
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = card.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        color = Color.Black
                    )

                    Text(
                        text = card.description,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                    )

                    // Card image
                    Box(modifier = Modifier.padding(top = 10.dp)) {
                        Image(
                            painter = painterResource(id = card.cardImageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(146.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    spotColor = Color(0x66C3845A)
                                )
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // "Get" button
                    Box(
                        modifier = Modifier
                            .size(width = 200.dp, height = 56.dp)
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(50.dp),
                                spotColor = Color.Black.copy(alpha = 0.15f)
                            )
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(0xFF0D120F))
                            .clickable { /* TODO: Get action */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Get",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(27.dp))
                }

                // Decorative ring (top-right)
                Image(
                    painter = painterResource(id = R.drawable.img_reward_popup_ring),
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 43.dp, height = 41.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 9.dp, y = (-12).dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── CollectionIntroOverlay ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun CollectionIntroOverlay(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // "1. Steps" title with gradient
                Text(
                    text = "1. Steps",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFFFFED29)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Steps illustration row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Run/Complete training
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.img_collection_intro_girl),
                            contentDescription = null,
                            modifier = Modifier.size(width = 60.64.dp, height = 91.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Run or Complete\ntraining",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )
                    }

                    // Arrow
                    Image(
                        painter = painterResource(id = R.drawable.img_collection_intro_arrow),
                        contentDescription = null,
                        modifier = Modifier.size(90.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Get fragments
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_collection_intro_fragment),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "get fragments",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Unlock reward card
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.img_collection_intro_gift),
                            contentDescription = null,
                            modifier = Modifier.size(width = 91.2.dp, height = 92.75.dp),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = "Unlock your\nreward card",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(59.dp))

                // "2. Notes" title with gradient
                Text(
                    text = "2. Notes",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFFFFED29)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "1)Duplicate fragments will be automatically converted to CaloCoins\n" +
                            "2)Fragments are given randomly, but the higher the reward weight, " +
                            "the more likely you are to get it\n" +
                            "3)Complete more tasks to earn more fragments!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(44.dp))

                // Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_collection_close),
                        contentDescription = null,
                        modifier = Modifier
                            .size(54.dp)
                            .alpha(0.9f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onDismiss() },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

// endregion

// ═══════════════════════════════════════════════════════════════════════════════
// region ──── SettingSectionView (shared) ────
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun SettingSectionView(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFC9FF6B),
                            Color(0xFFFFED29)
                        )
                    )
                )
        )
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
// region ──── Previews ────
// ═══════════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CollectionScreenPreview() {
    VitaloTheme {
        CollectionScreenContent(
            cards = MockDataProvider.cards,
            fragmentProgress = MockDataProvider.fragmentProgress,
            cardEvents = MockDataProvider.cardEvents
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CardDetailOverlayPreview() {
    VitaloTheme {
        CardDetailOverlay(
            card = MockDataProvider.cards[0],
            onDismiss = {}
        )
    }
}

// endregion
