package com.vitalo.markrun.ui.mine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.vitalo.markrun.data.local.db.entity.WeightRecord

@Composable
fun WeightChartSection(
    viewModel: WeightChartViewModel
) {
    val records by viewModel.records.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF4F3EE).copy(alpha = 0.4f))
            .padding(16.dp)
    ) {
        Text(
            text = "( KG )",
            fontSize = 14.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No weight data yet",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        } else {
            WeightChart(
                records = records,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }
    }
}

@Composable
fun WeightChart(
    records: List<WeightRecord>,
    modifier: Modifier = Modifier
) {
    val sortedRecords = remember(records) {
        records.sortedBy { it.date }
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(sortedRecords) {
        if (sortedRecords.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries {
                    series(sortedRecords.map { it.weight.toDouble() })
                }
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom()
        ),
        modelProducer = modelProducer,
        modifier = modifier
    )
}
