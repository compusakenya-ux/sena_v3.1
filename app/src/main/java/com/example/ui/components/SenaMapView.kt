package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SenaBackground
import com.example.ui.theme.SenaElectricCyan
import com.example.ui.theme.SenaPeach
import com.example.ui.theme.SenaTextMuted
import com.example.ui.theme.SenaTextSecondary

@Composable
fun SenaMapView(
    modifier: Modifier = Modifier,
    isTrackingMode: Boolean = false,
    selectedDestination: String = "Nyali Bridge",
    onLocationClick: (String) -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "map_animation")
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val progressAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SenaBackground)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onLocationClick(selectedDestination) }
        ) {
            val width = size.width
            val height = size.height

            // 1. Draw Mombasa Coastline & Water Bodies (Dark Cyan/Blue Water)
            val waterColor = Color(0xFF090B10)
            val landColor = Color(0xFF131622)
            val gridColor = Color(0xFF1C2030)
            val roadColor = Color(0xFF262B3E)
            val mainBridgeColor = Color(0xFF333B54)

            // Base Background
            drawRect(color = landColor)

            // Grid lines overlay
            val gridSpacing = 40.dp.toPx()
            var x = 0f
            while (x < width) {
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += gridSpacing
            }
            var y = 0f
            while (y < height) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += gridSpacing
            }

            // Draw Tudor Creek & Indian Ocean Water Curves
            val waterPath = Path().apply {
                moveTo(width * 0.1f, 0f)
                cubicTo(width * 0.3f, height * 0.25f, width * 0.05f, height * 0.6f, width * 0.4f, height)
                lineTo(0f, height)
                lineTo(0f, 0f)
                close()
            }
            drawPath(path = waterPath, color = waterColor)

            // Nyali Creek / North Coast Water
            val creekPath = Path().apply {
                moveTo(width * 0.5f, 0f)
                cubicTo(width * 0.65f, height * 0.35f, width * 0.85f, height * 0.2f, width, height * 0.4f)
                lineTo(width, 0f)
                close()
            }
            drawPath(path = creekPath, color = waterColor)

            // Main Arterial Roads (Mombasa Highway, Nyali Bridge Road, Nyerere Ave)
            val road1 = Path().apply {
                moveTo(width * 0.2f, height * 0.85f)
                quadraticTo(width * 0.45f, height * 0.55f, width * 0.7f, height * 0.25f)
                lineTo(width * 0.95f, height * 0.1f)
            }
            drawPath(path = road1, color = roadColor, style = Stroke(width = 8.dp.toPx()))

            val road2 = Path().apply {
                moveTo(width * 0.1f, height * 0.35f)
                cubicTo(width * 0.35f, height * 0.35f, width * 0.5f, height * 0.5f, width * 0.85f, height * 0.75f)
            }
            drawPath(path = road2, color = roadColor, style = Stroke(width = 6.dp.toPx()))

            // Nyali Bridge structure
            drawLine(
                color = mainBridgeColor,
                start = Offset(width * 0.42f, height * 0.38f),
                end = Offset(width * 0.55f, height * 0.3f),
                strokeWidth = 12.dp.toPx()
            )

            // Active Route Path (Glowing Cyan Route)
            val routeStart = Offset(width * 0.32f, height * 0.68f) // City Center
            val routeEnd = Offset(width * 0.75f, height * 0.22f) // Nyali Bridge / Beach
            val controlPoint = Offset(width * 0.52f, height * 0.58f)

            val routePath = Path().apply {
                moveTo(routeStart.x, routeStart.y)
                quadraticTo(controlPoint.x, controlPoint.y, routeEnd.x, routeEnd.y)
            }

            // Route Base Glow
            drawPath(
                path = routePath,
                color = SenaElectricCyan.copy(alpha = 0.25f),
                style = Stroke(width = 12.dp.toPx())
            )

            // Route Dashed Animated Line
            drawPath(
                path = routePath,
                color = SenaElectricCyan,
                style = Stroke(
                    width = 4.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 16f), progressAnim * 40f)
                )
            )

            // Start Location Pin (City Center)
            drawCircle(color = SenaTextMuted, radius = 8.dp.toPx(), center = routeStart)
            drawCircle(color = Color.White, radius = 4.dp.toPx(), center = routeStart)

            // Destination Location Pin (Nyali Bridge)
            drawCircle(
                color = SenaPeach.copy(alpha = 0.3f),
                radius = pulseAnim * 1.2f,
                center = routeEnd
            )
            drawCircle(color = SenaPeach, radius = 9.dp.toPx(), center = routeEnd)
            drawCircle(color = Color.White, radius = 4.dp.toPx(), center = routeEnd)

            // Vehicle Marker (Scooter position on curve depending on progress/tracking)
            val t = if (isTrackingMode) (0.35f + progressAnim * 0.5f) % 1f else 0.55f
            // quadratic bezier point formula: (1-t)^2 * P0 + 2(1-t)t * P1 + t^2 * P2
            val vehicleX = (1 - t) * (1 - t) * routeStart.x + 2 * (1 - t) * t * controlPoint.x + t * t * routeEnd.x
            val vehicleY = (1 - t) * (1 - t) * routeStart.y + 2 * (1 - t) * t * controlPoint.y + t * t * routeEnd.y
            val vehiclePos = Offset(vehicleX, vehicleY)

            // Vehicle Outer Pulsing Halo
            drawCircle(
                color = SenaElectricCyan.copy(alpha = 0.35f),
                radius = pulseAnim * 1.5f,
                center = vehiclePos
            )
            // Vehicle Center Badge
            drawCircle(color = Color(0xFF092A33), radius = 18.dp.toPx(), center = vehiclePos)
            drawCircle(
                color = SenaElectricCyan,
                radius = 18.dp.toPx(),
                style = Stroke(width = 2.dp.toPx()),
                center = vehiclePos
            )
            // Electric Bolt Dot
            drawCircle(color = SenaElectricCyan, radius = 7.dp.toPx(), center = vehiclePos)
        }

        // Map Watermark Labels
        Text(
            text = "MOMBASA COAST",
            color = SenaTextMuted.copy(alpha = 0.4f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 80.dp)
        )

        Text(
            text = "NYALI",
            color = SenaTextMuted.copy(alpha = 0.4f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 36.dp, top = 90.dp)
        )

        Text(
            text = "CITY CENTER",
            color = SenaTextMuted.copy(alpha = 0.4f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 36.dp, bottom = 180.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(SenaBackground.copy(alpha = 0.75f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "⚡ Realtime E-Fleet Network Active",
                color = SenaElectricCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
