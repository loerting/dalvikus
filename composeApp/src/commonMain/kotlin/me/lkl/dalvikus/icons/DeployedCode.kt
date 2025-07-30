package io.github.composegears.valkyrie

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Filled.DeployedCode: ImageVector
    get() {
        if (_DeployedCode != null) {
            return _DeployedCode!!
        }
        _DeployedCode = ImageVector.Builder(
            name = "Filled.DeployedCode",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE8EAED))) {
                moveTo(440f, 777f)
                verticalLineToRelative(-274f)
                lineTo(200f, 364f)
                verticalLineToRelative(274f)
                lineToRelative(240f, 139f)
                close()
                moveTo(520f, 777f)
                lineTo(760f, 638f)
                verticalLineToRelative(-274f)
                lineTo(520f, 503f)
                verticalLineToRelative(274f)
                close()
                moveTo(480f, 434f)
                lineTo(717f, 297f)
                lineTo(480f, 160f)
                lineTo(243f, 297f)
                lineTo(480f, 434f)
                close()
                moveTo(160f, 708f)
                quadToRelative(-19f, -11f, -29.5f, -29f)
                reflectiveQuadTo(120f, 639f)
                verticalLineToRelative(-318f)
                quadToRelative(0f, -22f, 10.5f, -40f)
                reflectiveQuadToRelative(29.5f, -29f)
                lineToRelative(280f, -161f)
                quadToRelative(19f, -11f, 40f, -11f)
                reflectiveQuadToRelative(40f, 11f)
                lineToRelative(280f, 161f)
                quadToRelative(19f, 11f, 29.5f, 29f)
                reflectiveQuadToRelative(10.5f, 40f)
                verticalLineToRelative(318f)
                quadToRelative(0f, 22f, -10.5f, 40f)
                reflectiveQuadTo(800f, 708f)
                lineTo(520f, 869f)
                quadToRelative(-19f, 11f, -40f, 11f)
                reflectiveQuadToRelative(-40f, -11f)
                lineTo(160f, 708f)
                close()
                moveTo(480f, 480f)
                close()
            }
        }.build()

        return _DeployedCode!!
    }

@Suppress("ObjectPropertyName")
private var _DeployedCode: ImageVector? = null
