package com.example.network

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class GitHubRepoAnalysisResult(
    val isValid: Boolean,
    val owner: String = "",
    val repoName: String = "",
    val description: String = "",
    val languagesSummary: String = "",
    val readmeSnippet: String = "",
    val fileStructureSummary: String = "",
    val stars: Int = 0,
    val defaultBranch: String = "main",
    val errorMessage: String? = null
)

object GitHubService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    fun parseRepoUrl(url: String): Pair<String, String>? {
        val clean = url.trim().removeSuffix("/").removeSuffix(".git")
        // Match https://github.com/owner/repo or github.com/owner/repo or owner/repo
        val regex = Regex("""(?:https?://)?(?:www\.)?github\.com/([a-zA-Z0-9_\-\.]+)/([a-zA-Z0-9_\-\.]+)""")
        val match = regex.find(clean)
        if (match != null) {
            val (owner, repo) = match.destructured
            return Pair(owner, repo)
        }
        val parts = clean.split("/")
        if (parts.size == 2 && parts[0].isNotEmpty() && parts[1].isNotEmpty() && !clean.contains("http")) {
            return Pair(parts[0], parts[1])
        }
        return null
    }

    suspend fun analyzeRepository(repoUrl: String): GitHubRepoAnalysisResult = withContext(Dispatchers.IO) {
        val parsed = parseRepoUrl(repoUrl)
            ?: return@withContext GitHubRepoAnalysisResult(
                isValid = false,
                errorMessage = "Invalid GitHub repository URL format. Please use 'https://github.com/owner/repo'."
            )

        val owner = parsed.first
        val repo = parsed.second

        try {
            // 1. Fetch Repository Details
            val repoRequest = Request.Builder()
                .url("https://api.github.com/repos/$owner/$repo")
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "NexusIntern-App")
                .build()

            val repoResponse = client.newCall(repoRequest).execute()
            if (!repoResponse.isSuccessful) {
                val code = repoResponse.code
                val errBody = repoResponse.body?.string() ?: ""
                val msg = when (code) {
                    404 -> "Repository '$owner/$repo' not found or is private. Only public repositories can be evaluated."
                    403 -> "GitHub API rate limit exceeded or access forbidden. Check repository accessibility."
                    else -> "Failed to access GitHub repository (HTTP $code). $errBody"
                }
                return@withContext GitHubRepoAnalysisResult(isValid = false, errorMessage = msg)
            }

            val repoJson = JSONObject(repoResponse.body?.string() ?: "{}")
            val description = repoJson.optString("description", "No description provided.")
            val stars = repoJson.optInt("stargazers_count", 0)
            val defaultBranch = repoJson.optString("default_branch", "main")

            // 2. Fetch Languages
            var languagesSummary = "Not specified"
            try {
                val langRequest = Request.Builder()
                    .url("https://api.github.com/repos/$owner/$repo/languages")
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("User-Agent", "NexusIntern-App")
                    .build()
                val langResponse = client.newCall(langRequest).execute()
                if (langResponse.isSuccessful) {
                    val langJson = JSONObject(langResponse.body?.string() ?: "{}")
                    val keys = langJson.keys()
                    var totalBytes = 0L
                    val langList = mutableListOf<Pair<String, Long>>()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val bytes = langJson.optLong(key, 0L)
                        totalBytes += bytes
                        langList.add(Pair(key, bytes))
                    }
                    if (totalBytes > 0) {
                        languagesSummary = langList.take(5).joinToString(", ") { (lang, bytes) ->
                            val pct = (bytes * 100) / totalBytes
                            "$lang ($pct%)"
                        }
                    }
                }
            } catch (_: Exception) {}

            // 3. Fetch README
            var readmeSnippet = "README not available in default branch."
            try {
                val readmeRequest = Request.Builder()
                    .url("https://api.github.com/repos/$owner/$repo/readme")
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("User-Agent", "NexusIntern-App")
                    .build()
                val readmeResponse = client.newCall(readmeRequest).execute()
                if (readmeResponse.isSuccessful) {
                    val readmeJson = JSONObject(readmeResponse.body?.string() ?: "{}")
                    val contentBase64 = readmeJson.optString("content", "")
                    if (contentBase64.isNotEmpty()) {
                        val decodedBytes = Base64.decode(contentBase64.replace("\n", ""), Base64.DEFAULT)
                        val fullReadme = String(decodedBytes, Charsets.UTF_8)
                        readmeSnippet = fullReadme.take(800).trim()
                    }
                }
            } catch (_: Exception) {}

            // 4. Fetch Repo Tree (Source files & Structure)
            var fileStructure = "Root files: README.md, src/"
            try {
                val treeRequest = Request.Builder()
                    .url("https://api.github.com/repos/$owner/$repo/git/trees/$defaultBranch?recursive=1")
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("User-Agent", "NexusIntern-App")
                    .build()
                val treeResponse = client.newCall(treeRequest).execute()
                if (treeResponse.isSuccessful) {
                    val treeJson = JSONObject(treeResponse.body?.string() ?: "{}")
                    val treeArray = treeJson.optJSONArray("tree")
                    if (treeArray != null && treeArray.length() > 0) {
                        val paths = mutableListOf<String>()
                        for (i in 0 until minOf(treeArray.length(), 20)) {
                            val pathObj = treeArray.getJSONObject(i)
                            val path = pathObj.optString("path")
                            if (path.isNotEmpty() && !path.startsWith(".") && !path.contains("node_modules") && !path.contains("gradle")) {
                                paths.add(path)
                            }
                        }
                        if (paths.isNotEmpty()) {
                            fileStructure = paths.take(12).joinToString(" | ")
                        }
                    }
                }
            } catch (_: Exception) {}

            return@withContext GitHubRepoAnalysisResult(
                isValid = true,
                owner = owner,
                repoName = repo,
                description = description,
                languagesSummary = languagesSummary,
                readmeSnippet = readmeSnippet,
                fileStructureSummary = fileStructure,
                stars = stars,
                defaultBranch = defaultBranch
            )
        } catch (e: Exception) {
            return@withContext GitHubRepoAnalysisResult(
                isValid = false,
                errorMessage = "Network or connection error accessing GitHub: ${e.localizedMessage ?: "Unknown error"}"
            )
        }
    }
}
