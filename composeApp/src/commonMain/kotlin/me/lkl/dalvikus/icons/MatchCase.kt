package io.github.composegears.valkyrie

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Filled.MatchCase: ImageVector
    get() {
        if (_MatchCase != null) {
            return _MatchCase!!
        }
        _MatchCase = ImageVector.Builder(
            name = "Filled.MatchCase",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE8EAED))) {
                moveToRelative(131f, 708f)
                lineToRelative(165f, -440f)
                horizontalLineToRelative(79f)
                lineToRelative(165f, 440f)
                horizontalLineToRelative(-76f)
                lineToRelative(-39f, -112f)
                lineTo(247f, 596f)
                lineToRelative(-40f, 112f)
                horizontalLineToRelative(-76f)
                close()
                moveTo(270f, 532f)
                horizontalLineToRelative(131f)
                lineToRelative(-64f, -182f)
                horizontalLineToRelative(-4f)
                lineToRelative(-63f, 182f)
                close()
                moveTo(665f, 718f)
                quadToRelative(-51f, 0f, -81f, -27.5f)
                reflectiveQuadTo(554f, 618f)
                quadToRelative(0f, -44f, 34.5f, -72.5f)
                reflectiveQuadTo(677f, 517f)
                quadToRelative(23f, 0f, 45f, 4f)
                reflectiveQuadToRelative(38f, 11f)
                verticalLineToRelative(-12f)
                quadToRelative(0f, -29f, -20.5f, -47f)
                reflectiveQuadTo(685f, 455f)
                quadToRelative(-23f, 0f, -42f, 9.5f)
                reflectiveQuadTo(610f, 492f)
                lineToRelative(-47f, -35f)
                quadToRelative(24f, -29f, 54.5f, -43f)
                reflectiveQuadToRelative(68.5f, -14f)
                quadToRelative(69f, 0f, 103f, 32.5f)
                reflectiveQuadToRelative(34f, 97.5f)
                verticalLineToRelative(178f)
                horizontalLineToRelative(-63f)
                verticalLineToRelative(-37f)
                horizontalLineToRelative(-4f)
                quadToRelative(-14f, 23f, -38f, 35f)
                reflectiveQuadToRelative(-53f, 12f)
                close()
                moveTo(677f, 664f)
                quadToRelative(35f, 0f, 59.5f, -24f)
                reflectiveQuadToRelative(24.5f, -56f)
                quadToRelative(-14f, -8f, -33.5f, -12.5f)
                reflectiveQuadTo(689f, 567f)
                quadToRelative(-32f, 0f, -50f, 14f)
                reflectiveQuadToRelative(-18f, 37f)
                quadToRelative(0f, 20f, 16f, 33f)
                reflectiveQuadToRelative(40f, 13f)
                close()
            }
        }.build()

        return _MatchCase!!
    }

@Suppress("ObjectPropertyName")
private var _MatchCase: ImageVector? = null
