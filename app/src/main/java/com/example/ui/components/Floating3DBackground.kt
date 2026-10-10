package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.NexusTokens
import kotlin.math.cos
import kotlin.math.sin

/**
 * Signature 3D Visual Language for the Landing Screen.
 * Premium editorial technology aesthetic: refined neutral polyhedra with soft studio lighting,
 * subtle #5558A6 accent gradients, ground shadow projections, and restrained physics motion.
 */
@Composable
fun Floating3DBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating_3d_editorial")

    val floatOffset1 by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )

    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Subtle Precision Coordinate Grid (4% alpha)
        val step = 48f
        val dotColor = NexusTokens.Foreground.copy(alpha = 0.035f)
        var gx = step
        while (gx < w) {
            var gy = step
            while (gy < h) {
                drawCircle(color = dotColor, radius = 0.85f, center = Offset(gx, gy))
                gy += step
            }
            gx += step
        }

        // 2. Object 1: Precision Studio Isometric Cube (Upper Right)
        val center1 = Offset(w * 0.82f, h * 0.16f + floatOffset1)
        val shadowScale1 = 1f - (floatOffset1 / 60f)
        drawFacetedShadow(
            center = Offset(center1.x, h * 0.16f + 55f),
            radiusX = 38f * shadowScale1,
            radiusY = 12f * shadowScale1
        )
        drawStudioIsometricCube(
            center = center1,
            size = 42f,
            accentColor = NexusTokens.Accent,
            neutralColor = NexusTokens.Foreground
        )

        // 3. Object 2: Precision Studio Octahedron (Lower Left)
        val center2 = Offset(w * 0.15f, h * 0.72f + floatOffset2)
        val shadowScale2 = 1f - (floatOffset2 / 60f)
        drawFacetedShadow(
            center = Offset(center2.x, h * 0.72f + 62f),
            radiusX = 42f * shadowScale2,
            radiusY = 14f * shadowScale2
        )
        drawStudioOctahedron(
            center = center2,
            radius = 44f,
            accentColor = NexusTokens.Accent,
            neutralColor = NexusTokens.Foreground
        )

        // 4. Object 3: Precision Architectural Wireframe Ring / Node Cluster (Mid Right)
        val center3 = Offset(w * 0.86f, h * 0.58f + (floatOffset1 * 0.6f))
        drawArchitecturalTorus(
            center = center3,
            radiusX = 36f,
            radiusY = 16f,
            accentColor = NexusTokens.Accent,
            neutralColor = NexusTokens.Foreground,
            angle = rotateAngle
        )

        // 5. Object 4: Micro Floating Prisms (Accents)
        drawMicroAccentPrism(
            center = Offset(w * 0.22f, h * 0.28f + (floatOffset2 * 0.5f)),
            size = 14f,
            accentColor = NexusTokens.Accent
        )
    }
}

private fun DrawScope.drawFacetedShadow(center: Offset, radiusX: Float, radiusY: Float) {
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                NexusTokens.Foreground.copy(alpha = 0.06f),
                NexusTokens.Foreground.copy(alpha = 0.02f),
                Color.Transparent
            ),
            center = center,
            radius = radiusX
        ),
        topLeft = Offset(center.x - radiusX, center.y - radiusY),
        size = Size(radiusX * 2, radiusY * 2)
    )
}

private fun DrawScope.drawStudioIsometricCube(
    center: Offset,
    size: Float,
    accentColor: Color,
    neutralColor: Color
) {
    val h = size * 0.866f
    val halfH = h / 2f
    val halfS = size / 2f

    // Top Face (Lit from top-left studio softbox)
    val topPath = Path().apply {
        moveTo(center.x, center.y - size)
        lineTo(center.x + h, center.y - halfS)
        lineTo(center.x, center.y)
        lineTo(center.x - h, center.y - halfS)
        close()
    }
    drawPath(
        path = topPath,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.65f),
                neutralColor.copy(alpha = 0.04f)
            ),
            start = Offset(center.x - h, center.y - size),
            end = Offset(center.x + h, center.y)
        )
    )
    drawPath(topPath, neutralColor.copy(alpha = 0.16f), style = Stroke(width = 1.2f))

    // Top Specular Highlight Edge (crisp white line)
    val specPath = Path().apply {
        moveTo(center.x - h, center.y - halfS)
        lineTo(center.x, center.y - size)
        lineTo(center.x + h, center.y - halfS)
    }
    drawPath(specPath, Color.White.copy(alpha = 0.8f), style = Stroke(width = 1.4f))

    // Left Face (Accent tone reflection)
    val leftPath = Path().apply {
        moveTo(center.x - h, center.y - halfS)
        lineTo(center.x, center.y)
        lineTo(center.x, center.y + size)
        lineTo(center.x - h, center.y + halfS)
        close()
    }
    drawPath(
        path = leftPath,
        brush = Brush.linearGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.14f),
                accentColor.copy(alpha = 0.04f)
            ),
            start = Offset(center.x - h, center.y - halfS),
            end = Offset(center.x, center.y + size)
        )
    )
    drawPath(leftPath, accentColor.copy(alpha = 0.22f), style = Stroke(width = 1.2f))

    // Right Face (Subtle ambient occlusion shadow)
    val rightPath = Path().apply {
        moveTo(center.x, center.y)
        lineTo(center.x + h, center.y - halfS)
        lineTo(center.x + h, center.y + halfS)
        lineTo(center.x, center.y + size)
        close()
    }
    drawPath(
        path = rightPath,
        brush = Brush.linearGradient(
            colors = listOf(
                neutralColor.copy(alpha = 0.03f),
                neutralColor.copy(alpha = 0.08f)
            ),
            start = Offset(center.x, center.y),
            end = Offset(center.x + h, center.y + size)
        )
    )
    drawPath(rightPath, neutralColor.copy(alpha = 0.14f), style = Stroke(width = 1.2f))
}

private fun DrawScope.drawStudioOctahedron(
    center: Offset,
    radius: Float,
    accentColor: Color,
    neutralColor: Color
) {
    val top = Offset(center.x, center.y - radius * 1.2f)
    val bottom = Offset(center.x, center.y + radius * 1.2f)
    val left = Offset(center.x - radius, center.y)
    val right = Offset(center.x + radius, center.y)
    val front = Offset(center.x, center.y + radius * 0.35f)

    // Upper Left Face (Primary studio lighting + accent sheen)
    val p1 = Path().apply {
        moveTo(top.x, top.y)
        lineTo(left.x, left.y)
        lineTo(front.x, front.y)
        close()
    }
    drawPath(
        path = p1,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.45f),
                accentColor.copy(alpha = 0.10f)
            ),
            start = top,
            end = front
        )
    )
    drawPath(p1, accentColor.copy(alpha = 0.24f), style = Stroke(width = 1.2f))

    // Upper Right Face (Subtle neutral)
    val p2 = Path().apply {
        moveTo(top.x, top.y)
        lineTo(right.x, right.y)
        lineTo(front.x, front.y)
        close()
    }
    drawPath(p2, neutralColor.copy(alpha = 0.045f), style = Fill)
    drawPath(p2, neutralColor.copy(alpha = 0.16f), style = Stroke(width = 1.2f))

    // Lower Left Face
    val p3 = Path().apply {
        moveTo(bottom.x, bottom.y)
        lineTo(left.x, left.y)
        lineTo(front.x, front.y)
        close()
    }
    drawPath(p3, accentColor.copy(alpha = 0.05f), style = Fill)
    drawPath(p3, accentColor.copy(alpha = 0.18f), style = Stroke(width = 1.2f))

    // Lower Right Face (Shadow facet)
    val p4 = Path().apply {
        moveTo(bottom.x, bottom.y)
        lineTo(right.x, right.y)
        lineTo(front.x, front.y)
        close()
    }
    drawPath(p4, neutralColor.copy(alpha = 0.07f), style = Fill)
    drawPath(p4, neutralColor.copy(alpha = 0.14f), style = Stroke(width = 1.2f))

    // Vertical Apex Axis line (architectural construction feel)
    drawLine(
        color = accentColor.copy(alpha = 0.18f),
        start = top,
        end = bottom,
        strokeWidth = 1f
    )
}

private fun DrawScope.drawArchitecturalTorus(
    center: Offset,
    radiusX: Float,
    radiusY: Float,
    accentColor: Color,
    neutralColor: Color,
    angle: Float
) {
    // Outer Elliptical Ring
    drawOval(
        color = neutralColor.copy(alpha = 0.12f),
        topLeft = Offset(center.x - radiusX, center.y - radiusY),
        size = Size(radiusX * 2, radiusY * 2),
        style = Stroke(width = 1.2f)
    )

    // Inner Concentric Ring
    val inRadX = radiusX * 0.65f
    val inRadY = radiusY * 0.65f
    drawOval(
        color = accentColor.copy(alpha = 0.15f),
        topLeft = Offset(center.x - inRadX, center.y - inRadY),
        size = Size(inRadX * 2, inRadY * 2),
        style = Stroke(width = 1f)
    )

    // Orbital Nodes along the ring
    val rad = Math.toRadians(angle.toDouble())
    val nodeX = center.x + cos(rad).toFloat() * radiusX
    val nodeY = center.y + sin(rad).toFloat() * radiusY
    drawCircle(
        color = accentColor,
        radius = 3.5f,
        center = Offset(nodeX, nodeY)
    )
    drawCircle(
        color = Color.White,
        radius = 1.5f,
        center = Offset(nodeX, nodeY)
    )
}

private fun DrawScope.drawMicroAccentPrism(
    center: Offset,
    size: Float,
    accentColor: Color
) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        lineTo(center.x + size * 0.86f, center.y + size * 0.5f)
        lineTo(center.x - size * 0.86f, center.y + size * 0.5f)
        close()
    }
    drawPath(path, accentColor.copy(alpha = 0.08f), style = Fill)
    drawPath(path, accentColor.copy(alpha = 0.22f), style = Stroke(width = 1f))
    drawCircle(color = accentColor.copy(alpha = 0.4f), radius = 2f, center = center)
}
