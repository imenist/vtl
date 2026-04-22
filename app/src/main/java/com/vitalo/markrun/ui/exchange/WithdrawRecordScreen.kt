package com.vitalo.markrun.ui.exchange

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vitalo.markrun.data.remote.model.WithdrawalInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Withdrawal Records") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("← Back", color = Color(0xFF0D120E))
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No withdrawal records", color = Color(0xFF575757))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = records) { record ->
                    WithdrawRecordCard(record = record)
                }
            }
        }
    }
}

@Composable
fun WithdrawRecordCard(record: WithdrawalInfo) {
    val statusText = when (record.status) {
        0 -> "Processing"
        1 -> "Completed"
        2 -> "Failed"
        else -> "Unknown"
    }
    val statusColor = when (record.status) {
        0 -> Color(0xFFFF9000)
        1 -> Color(0xFF4CAF50)
        2 -> Color(0xFFF44336)
        else -> Color(0xFF575757)
    }

    val dateStr = record.applyTime?.let { ts ->
        try {
            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(ts * 1000))
        } catch (_: Exception) { "" }
    } ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8F8F8))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$${String.format("%.2f", record.amount ?: 0.0)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D120E)
            )
            Text(
                text = dateStr,
                fontSize = 12.sp,
                color = Color(0xFF575757)
            )
            if (!record.gcClaimCode.isNullOrEmpty()) {
                Text(
                    text = "Code: ${record.gcClaimCode}",
                    fontSize = 12.sp,
                    color = Color(0xFF0D120E),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Text(
            text = statusText,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = statusColor
        )
    }
}
