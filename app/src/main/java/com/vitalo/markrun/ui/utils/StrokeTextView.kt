package com.vitalo.markrun.ui.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalTextApi::class)
@Composable
fun StrokeTextView(
    text: String,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    fontStyle: FontStyle? = null,
    strokeWidth: Float = 10f,
    strokeColor: Color,
    textColor: Color,
    rotationDegrees: Float = 0f,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    autoSize: Boolean = false,
    shadow: androidx.compose.ui.graphics.Shadow? = null
) {
    val fontSizeState = remember(fontSize) { mutableStateOf(fontSize) }
    val readyToDrawState = remember { mutableStateOf(!autoSize) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.drawWithContent {
            if (readyToDrawState.value) {
                drawContent()
            }
        }
    ) {
        //描边
        Text(
            text = text,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            fontStyle = fontStyle,
            maxLines = maxLines,
            style = TextStyle(
                fontSize = fontSizeState.value,
                drawStyle = Stroke(width = strokeWidth, join = StrokeJoin.Round),
                color = strokeColor,
                textAlign = TextAlign.Center,
                shadow = shadow
            ),
            modifier = Modifier.graphicsLayer {
                rotationZ = rotationDegrees
            },
            onTextLayout = { textLayoutResult ->
                if (autoSize && textLayoutResult.hasVisualOverflow) {
                    fontSizeState.value *= 0.9f
                } else {
                    readyToDrawState.value = true
                }
            }
        )

        Text(
            text = text,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            fontStyle = fontStyle,
            maxLines = maxLines,
            style = TextStyle(
                fontSize = fontSizeState.value,
                color = textColor,
                textAlign = TextAlign.Center,
                shadow = shadow
            ),
            modifier = Modifier.graphicsLayer {
                rotationZ = rotationDegrees
            }
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun GradientStrokeTextView(
    text: String,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    fontStyle: FontStyle? = null,
    strokeWidth: Float = 10f,
    strokeColor: Color,
    gradientColors: List<Color>,
    rotationDegrees: Float = 0f,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            text = text,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            fontStyle = fontStyle,
            maxLines = maxLines,
            style = TextStyle(
                fontSize = fontSize,
                drawStyle = Stroke(width = strokeWidth, join = StrokeJoin.Round),
                color = strokeColor,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.graphicsLayer {
                rotationZ = rotationDegrees
            }
        )

        Text(
            text = text,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            fontStyle = fontStyle,
            maxLines = maxLines,
            style = TextStyle(
                fontSize = fontSize,
                brush = Brush.linearGradient(colors = gradientColors),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.graphicsLayer {
                rotationZ = rotationDegrees
            }
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun StrokeAnnotatedTextView(
    text: AnnotatedString,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    strokeWidth: Float = 10f,
    strokeColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        // 描边
        Text(
            text = text,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = fontSize,
                drawStyle = Stroke(width = strokeWidth, join = StrokeJoin.Round),
                color = strokeColor
            )
        )

        // 填充
        Text(
            text = text,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = fontSize,
                color = textColor
            )
        )
    }
}

@Composable
fun PriceWithRollingDigits(
    priceText: String,
    fontWeight: FontWeight,
    fontSize: TextUnit,
    strokeWidth: Float,
    fontFamily: FontFamily,
    strokeColor: Color,
    textColor: Color,
    rotationDegrees: Float = 0f
) {
    // 分离货币符号和数字部分
    val currencySymbol = priceText.takeWhile { !it.isDigit() && it != '.' }
    val priceValue = priceText.substring(currencySymbol.length)

    val isRolling = remember { mutableStateOf(true) }

    // 2秒后停止滚动
    LaunchedEffect(Unit) {
        delay(1000)
        isRolling.value = false
    }

    if (isRolling.value) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.graphicsLayer {
                rotationZ = rotationDegrees
            }
        ) {
            // 货币符号部分
            StrokeTextView(
                text = currencySymbol,
                fontWeight = fontWeight,
                fontSize = fontSize,
                strokeWidth = strokeWidth,
                fontFamily = fontFamily,
                strokeColor = strokeColor,
                textColor = textColor
            )

            // 数字部分
            for (char in priceValue) {
                if (char.isDigit()) {
                    RollingDigit(
                        targetDigit = char.toString(),
                        isRolling = true,
                        fontWeight = fontWeight,
                        fontSize = fontSize,
                        strokeWidth = strokeWidth,
                        fontFamily = fontFamily,
                        strokeColor = strokeColor,
                        textColor = textColor
                    )
                } else {
                    // 小数点或其他非数字字符
                    StrokeTextView(
                        text = char.toString(),
                        fontWeight = fontWeight,
                        fontSize = fontSize,
                        strokeWidth = strokeWidth,
                        fontFamily = fontFamily,
                        strokeColor = strokeColor,
                        textColor = textColor
                    )
                }
            }
        }
    } else {
        // 停止滚动后，直接显示完整的原始价格
        StrokeTextView(
            text = priceText,
            fontWeight = fontWeight,
            fontSize = fontSize,
            strokeWidth = strokeWidth,
            fontFamily = fontFamily,
            strokeColor = strokeColor,
            textColor = textColor,
            rotationDegrees = rotationDegrees
        )
    }
}

@Composable
fun RollingDigit(
    targetDigit: String,
    isRolling: Boolean,
    fontWeight: FontWeight,
    fontSize: TextUnit,
    strokeWidth: Float,
    fontFamily: FontFamily,
    strokeColor: Color,
    textColor: Color
) {
    val currentDigit = remember { mutableStateOf((0..9).random().toString()) }

    LaunchedEffect(Unit) {
        if (isRolling) {
            while (isActive) {
                currentDigit.value = (0..9).random().toString()
                delay(50) // 快速滚动
            }
        }
    }

    StrokeTextView(
        text = currentDigit.value,
        fontWeight = fontWeight,
        fontSize = fontSize,
        strokeWidth = strokeWidth,
        fontFamily = fontFamily,
        strokeColor = strokeColor,
        textColor = textColor
    )
}
