package com.vitalo.markrun.ui.exchange

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vitalo.markrun.R
import com.vitalo.markrun.data.remote.model.WithdrawalInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WithdrawRecordScreen(
    navController: NavController,
    viewModel: WithdrawRecordViewModel = hiltViewModel()
) {
    val records by viewModel.records.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRecords()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.popBackStack() },
                contentScale = ContentScale.Fit
            )
            
            Text(
                text = "Withdrawal History",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0D120E),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.width(24.dp))
        }

        Spacer(modifier = Modifier.height(19.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading...", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                }
            }
        } else if (records.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Fallback to large coin if empty img is missing
                    Image(
                        painter = painterResource(id = R.drawable.img_exchange_coin_large),
                        contentDescription = null,
                        modifier = Modifier.size(240.dp, 225.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No Data Available",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = records) { record ->
                    WithdrawRecordItemView(info = record)
                }
            }
        }
    }
}

@Composable
fun WithdrawRecordItemView(info: WithdrawalInfo) {
    val dateStr = info.applyTime?.let { ts ->
        try {
            SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(ts * 1000))
        } catch (_: Exception) { "" }
    } ?: ""

    val formattedCurrency = remember(info.amount) {
        val value = info.amount ?: 0.0
        if (value % 1.0 == 0.0) {
            "$${value.toInt()}"
        } else {
            String.format(Locale.US, "$%.2f", value)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF5F2ED)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        
        Image(
            painter = painterResource(id = R.drawable.img_exchange_coin),
            contentDescription = null,
            modifier = Modifier.size(66.dp),
            contentScale = ContentScale.Fit
        )
        
        Spacer(modifier = Modifier.width(6.dp))
        
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Withdrawal",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0D120E)
            )
            Text(
                text = dateStr,
                fontSize = 11.sp,
                color = Color(0xFF757575),
                modifier = Modifier.height(22.dp).padding(top = 2.dp)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "+ $formattedCurrency",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D120E)
        )
        
        Spacer(modifier = Modifier.width(32.dp))
    }
}
