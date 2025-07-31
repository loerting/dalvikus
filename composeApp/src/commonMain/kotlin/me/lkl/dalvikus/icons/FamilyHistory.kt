package me.lkl.dalvikus.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Filled.FamilyHistory: ImageVector
    get() {
        if (_FamilyHistory != null) {
            return _FamilyHistory!!
        }
        _FamilyHistory = ImageVector.Builder(
            name = "Filled.FamilyHistory",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE8EAED))) {
                moveTo(480f, 900f)
                quadToRelative(-63f, 0f, -106.5f, -43.5f)
                reflectiveQuadTo(330f, 750f)
                quadToRelative(0f, -52f, 31f, -91.5f)
                reflectiveQuadToRelative(79f, -53.5f)
                verticalLineToRelative(-85f)
                lineTo(200f, 520f)
                verticalLineToRelative(-160f)
                lineTo(100f, 360f)
                verticalLineToRelative(-280f)
                horizontalLineToRelative(280f)
                verticalLineToRelative(280f)
                lineTo(280f, 360f)
                verticalLineToRelative(80f)
                horizontalLineToRelative(400f)
                verticalLineToRelative(-85f)
                quadToRelative(-48f, -14f, -79f, -53.5f)
                reflectiveQuadTo(570f, 210f)
                quadToRelative(0f, -63f, 43.5f, -106.5f)
                reflectiveQuadTo(720f, 60f)
                quadToRelative(63f, 0f, 106.5f, 43.5f)
                reflectiveQuadTo(870f, 210f)
                quadToRelative(0f, 52f, -31f, 91.5f)
                reflectiveQuadTo(760f, 355f)
                verticalLineToRelative(165f)
                lineTo(520f, 520f)
                verticalLineToRelative(85f)
                quadToRelative(48f, 14f, 79f, 53.5f)
                reflectiveQuadToRelative(31f, 91.5f)
                quadToRelative(0f, 63f, -43.5f, 106.5f)
                reflectiveQuadTo(480f, 900f)
                close()
                moveTo(720f, 280f)
                quadToRelative(29f, 0f, 49.5f, -20.5f)
                reflectiveQuadTo(790f, 210f)
                quadToRelative(0f, -29f, -20.5f, -49.5f)
                reflectiveQuadTo(720f, 140f)
                quadToRelative(-29f, 0f, -49.5f, 20.5f)
                reflectiveQuadTo(650f, 210f)
                quadToRelative(0f, 29f, 20.5f, 49.5f)
                reflectiveQuadTo(720f, 280f)
                close()
                moveTo(180f, 280f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(-120f)
                lineTo(180f, 160f)
                verticalLineToRelative(120f)
                close()
                moveTo(480f, 820f)
                quadToRelative(29f, 0f, 49.5f, -20.5f)
                reflectiveQuadTo(550f, 750f)
                quadToRelative(0f, -29f, -20.5f, -49.5f)
                reflectiveQuadTo(480f, 680f)
                quadToRelative(-29f, 0f, -49.5f, 20.5f)
                reflectiveQuadTo(410f, 750f)
                quadToRelative(0f, 29f, 20.5f, 49.5f)
                reflectiveQuadTo(480f, 820f)
                close()
                moveTo(240f, 220f)
                close()
                moveTo(720f, 210f)
                close()
                moveTo(480f, 750f)
                close()
            }
        }.build()

        return _FamilyHistory!!
    }

@Suppress("ObjectPropertyName")
private var _FamilyHistory: ImageVector? = null
