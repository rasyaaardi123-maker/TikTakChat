package com.viana.chat

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal object AppIcons {
    val Status = ImageVector.Builder("Status", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round) {
            moveTo(12f, 3f)
            curveTo(16.97f, 3f, 21f, 7.03f, 21f, 12f)
            curveTo(21f, 16.97f, 16.97f, 21f, 12f, 21f)
            curveTo(7.03f, 21f, 3f, 16.97f, 3f, 12f)
            curveTo(3f, 7.03f, 7.03f, 3f, 12f, 3f)
            close()
            moveTo(12f, 7f)
            curveTo(14.76f, 7f, 17f, 9.24f, 17f, 12f)
            curveTo(17f, 14.76f, 14.76f, 17f, 12f, 17f)
            curveTo(9.24f, 17f, 7f, 14.76f, 7f, 12f)
            curveTo(7f, 9.24f, 9.24f, 7f, 12f, 7f)
        }
    }.build()

    val Call = ImageVector.Builder("Call", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(7.2f, 3.8f)
            curveTo(6.55f, 3.15f, 5.5f, 3.15f, 4.85f, 3.8f)
            lineTo(3.8f, 4.85f)
            curveTo(2.95f, 5.7f, 3.25f, 8.3f, 6.1f, 12.15f)
            curveTo(8.95f, 16f, 12.5f, 19.2f, 16.3f, 20.2f)
            curveTo(17.2f, 20.45f, 18.05f, 20.15f, 18.65f, 19.55f)
            lineTo(20.05f, 18.15f)
            curveTo(20.7f, 17.5f, 20.7f, 16.45f, 20.05f, 15.8f)
            lineTo(17.65f, 13.4f)
            curveTo(17.1f, 12.85f, 16.2f, 12.75f, 15.55f, 13.2f)
            lineTo(14.1f, 14.2f)
            curveTo(12.55f, 13.4f, 10.6f, 11.45f, 9.8f, 9.9f)
            lineTo(10.8f, 8.45f)
            curveTo(11.25f, 7.8f, 11.15f, 6.9f, 10.6f, 6.35f)
            lineTo(7.2f, 3.8f)
        }
    }.build()

    val Chat = ImageVector.Builder("Chat", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(5f, 4f)
            curveTo(3.9f, 4f, 3f, 4.9f, 3f, 6f)
            lineTo(3f, 16f)
            curveTo(3f, 17.1f, 3.9f, 18f, 5f, 18f)
            lineTo(7f, 18f)
            lineTo(7f, 21f)
            lineTo(11f, 18f)
            lineTo(19f, 18f)
            curveTo(20.1f, 18f, 21f, 17.1f, 21f, 16f)
            lineTo(21f, 6f)
            curveTo(21f, 4.9f, 20.1f, 4f, 19f, 4f)
            close()
            moveTo(7f, 9f)
            lineTo(17f, 9f)
            moveTo(7f, 13f)
            lineTo(14f, 13f)
        }
    }.build()

    val Profile = ImageVector.Builder("Profile", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(12f, 3.5f)
            curveTo(14.5f, 3.5f, 16.5f, 5.5f, 16.5f, 8f)
            curveTo(16.5f, 10.5f, 14.5f, 12.5f, 12f, 12.5f)
            curveTo(9.5f, 12.5f, 7.5f, 10.5f, 7.5f, 8f)
            curveTo(7.5f, 5.5f, 9.5f, 3.5f, 12f, 3.5f)
            moveTo(4f, 20f)
            curveTo(4.3f, 16.7f, 7.5f, 14.5f, 12f, 14.5f)
            curveTo(16.5f, 14.5f, 19.7f, 16.7f, 20f, 20f)
        }
    }.build()

    val Search = ImageVector.Builder("Search", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round) {
            moveTo(10.5f, 4f)
            curveTo(14.09f, 4f, 17f, 6.91f, 17f, 10.5f)
            curveTo(17f, 14.09f, 14.09f, 17f, 10.5f, 17f)
            curveTo(6.91f, 17f, 4f, 14.09f, 4f, 10.5f)
            curveTo(4f, 6.91f, 6.91f, 4f, 10.5f, 4f)
            moveTo(15.5f, 15.5f)
            lineTo(20f, 20f)
        }
    }.build()

    val Add = ImageVector.Builder("Add", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round) {
            moveTo(12f, 5f)
            lineTo(12f, 19f)
            moveTo(5f, 12f)
            lineTo(19f, 12f)
        }
    }.build()

    val Close = ImageVector.Builder("Close", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round) {
            moveTo(6f, 6f)
            lineTo(18f, 18f)
            moveTo(18f, 6f)
            lineTo(6f, 18f)
        }
    }.build()

    val Video = ImageVector.Builder("Video", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round) {
            moveTo(4f, 6f)
            curveTo(3.45f, 6f, 3f, 6.45f, 3f, 7f)
            lineTo(3f, 17f)
            curveTo(3f, 17.55f, 3.45f, 18f, 4f, 18f)
            lineTo(15f, 18f)
            curveTo(15.55f, 18f, 16f, 17.55f, 16f, 17f)
            lineTo(16f, 7f)
            curveTo(16f, 6.45f, 15.55f, 6f, 15f, 6f)
            close()
            moveTo(16f, 10f)
            lineTo(21f, 7f)
            lineTo(21f, 17f)
            lineTo(16f, 14f)
        }
    }.build()
}