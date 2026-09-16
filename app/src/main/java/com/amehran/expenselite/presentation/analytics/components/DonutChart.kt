package com.amehran.expenselite.presentation.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.amehran.expenselite.domain.usecase.CategorySpend

@Composable
fun DonutChart(
    data: List<Pair<CategorySpend, Color>>,
    modifier: Modifier = Modifier,
    thickness: Dp = 32.dp,
) {
    val totalSpend = data.sumOf { it.first.amountCents }

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f

            if (data.isEmpty()) {
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = thickness.toPx(), cap = StrokeCap.Round),
                    size = Size(size.width, size.height),
                    topLeft = Offset(0f, 0f),
                )
            } else {
                data.forEach { (categorySpend, color) ->
                    val sweepAngle = (categorySpend.percentage / 100f) * 360f

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = thickness.toPx(), cap = StrokeCap.Butt),
                        size = Size(size.width, size.height),
                        topLeft = Offset(0f, 0f),
                    )
                    startAngle += sweepAngle
                }
            }
        }

        val totalSpendString = String.format("$%.2f", totalSpend / 100.0)
        Text(
            text = totalSpendString,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}
