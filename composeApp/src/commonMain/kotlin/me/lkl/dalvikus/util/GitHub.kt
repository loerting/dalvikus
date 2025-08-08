package me.lkl.dalvikus.util

import co.touchlab.kermit.Logger
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI

fun getLatestReleaseTag(owner: String, repo: String): String? {
    val url = URI("https://api.github.com/repos/$owner/$repo/releases/latest").toURL()
    val connection = url.openConnection() as HttpURLConnection

    connection.requestMethod = "GET"
    connection.setRequestProperty("Accept", "application/vnd.github+json")
    connection.setRequestProperty("User-Agent", "KotlinApp") // Required by GitHub

    return try {
        if (connection.responseCode == 200) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()
            reader.close()

            // Extract tag_name manually (primitive parsing)
            val regex = Regex("\"tag_name\"\\s*:\\s*\"([^\"]+)\"")
            val match = regex.find(response)
            match?.groupValues?.get(1)
        } else {
            Logger.e("GitHub API error: HTTP ${connection.responseCode}")
            null
        }
    } catch (e: Exception) {
        Logger.e("Error fetching latest release tag: ${e.message}", e)
        null
    } finally {
        connection.disconnect()
    }
}
