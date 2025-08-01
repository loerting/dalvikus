package me.lkl.dalvikus.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Slideshow
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.materialkolor.ktx.darken
import me.lkl.dalvikus.ui.editor.suggestions.SuggestionType

data class FileTypeMeta(
    val icon: ImageVector,
    val color: Color? = null
)

fun getSuggestionTypeIcon(type: SuggestionType): ImageVector {
    return when (type) {
        SuggestionType.Instruction -> Icons.Default.Code
        SuggestionType.Directive -> Icons.Default.Directions
        SuggestionType.Register -> Icons.Default.Memory
        SuggestionType.Access -> Icons.Default.Api
        SuggestionType.LabelOrType -> Icons.Default.Class
    }
}

val readableImageFormats = listOf("png", "jpg", "jpeg", "bmp", "webp", "gif", "ico")

fun getFileExtensionMeta(fileName: String): FileTypeMeta {
    val ext = fileName.substringAfterLast('.', "").lowercase()

    return when (ext) {
        "txt", "md", "log" -> FileTypeMeta(Icons.Outlined.Description)
        in readableImageFormats ->
            FileTypeMeta(Icons.Outlined.Image, ImagePurple)
        "mp3", "wav", "ogg", "flac", "aac" ->
            FileTypeMeta(Icons.Outlined.MusicNote, AudioTeal)
        "mp4", "avi", "mov", "mkv", "webm" ->
            FileTypeMeta(Icons.Outlined.Movie, VideoRed)
        "pdf" -> FileTypeMeta(Icons.Outlined.PictureAsPdf, PdfRed)
        "zip", "jar", "rar", "7z", "tar", "gz" ->
            FileTypeMeta(Icons.Filled.FolderZip, ArchiveGray)
        "doc", "docx" -> FileTypeMeta(Icons.AutoMirrored.Outlined.Article, WordBlue)
        "xls", "xlsx" -> FileTypeMeta(Icons.Outlined.TableChart, ExcelGreen)
        "ppt", "pptx" -> FileTypeMeta(Icons.Outlined.Slideshow, PowerPointOrange)
        "html", "xml", "json", "yaml", "yml" ->
            FileTypeMeta(Icons.Outlined.Code, CodeBlue)
        "apk", "apks", "aab", "xapk", "dex", "odex" ->
            FileTypeMeta(Icons.Filled.Android, AndroidGreen)
        else -> FileTypeMeta(Icons.Outlined.Description)
    }
}

internal val AndroidGreen = Color(0xFF97AA4E)
internal val ArchiveGray = Color(0xFF878B87).darken()

internal val CodeBlue = Color(0xFF5C87B8)
internal val ImagePurple = Color(0xFF7A5A99)
internal val AudioTeal = Color(0xFF5E8E85)
internal val VideoRed = Color(0xFFB45757)
internal val PdfRed = Color(0xFF934444)
internal val WordBlue = Color(0xFF4A5FA8)
internal val ExcelGreen = Color(0xFF61895E)
internal val PowerPointOrange = Color(0xFFBA6E4E)
internal val PackageOrange = Color(0xFFC28852)