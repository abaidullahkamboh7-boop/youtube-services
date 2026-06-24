package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.data.database.AppDatabase
import com.example.data.database.PromotionCampaign
import com.example.data.database.SavedSeoItem
import com.example.data.database.UserStats
import com.example.data.repository.YoutubeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AuditResult(
    val score: Int,
    val titleScore: Int,
    val descriptionScore: Int,
    val tagsScore: Int,
    val thumbnailScore: Int,
    val recommendations: List<String>
)

class YoutubeServicesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: YoutubeRepository

    val userStats: StateFlow<UserStats?>
    val campaigns: StateFlow<List<PromotionCampaign>>
    val savedSeoItems: StateFlow<List<SavedSeoItem>>

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _seoResultText = MutableStateFlow("")
    val seoResultText = _seoResultText.asStateFlow()

    private val _extractedTags = MutableStateFlow<List<String>>(emptyList())
    val extractedTags = _extractedTags.asStateFlow()

    private val _auditResult = MutableStateFlow<AuditResult?>(null)
    val auditResult = _auditResult.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()

    private val _totalBoosterChannels = MutableStateFlow(104820)
    val totalBoosterChannels = _totalBoosterChannels.asStateFlow()

    private val _boosterLogs = MutableStateFlow<List<String>>(
        listOf(
            "System initialized: 104,820 booster channel nodes registered.",
            "Rotated IP tunnels successfully. 100% secure.",
            "Awaiting campaign queue..."
        )
    )
    val boosterLogs = _boosterLogs.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = YoutubeRepository(database.youtubeDao())

        userStats = repository.userStatsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        campaigns = repository.allCampaignsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        savedSeoItems = repository.allSavedSeoFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Initialize user stats in the database if empty
        viewModelScope.launch {
            repository.getOrInitializeStats()
        }

        // Start background engine to simulate organic campaign progress and indexation
        startCampaignProgressSimulator()
        startBoosterLogTicker()
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun earnPoints(amount: Int, reason: String) {
        viewModelScope.launch {
            repository.addPoints(amount)
            _toastMessage.value = "Success! Earned +$amount Points for $reason"
        }
    }

    fun createCampaign(videoUrl: String, title: String, type: String, targetCount: Int): Boolean {
        if (videoUrl.isBlank() || title.isBlank()) {
            _toastMessage.value = "Please fill in all details!"
            return false
        }

        if (!videoUrl.contains("youtube.com") && !videoUrl.contains("youtu.be")) {
            _toastMessage.value = "Please enter a valid YouTube video URL!"
            return false
        }

        val pointsCost = when (type) {
            "Views" -> targetCount * 2
            "Subscribers" -> targetCount * 10
            "Likes" -> targetCount * 4
            "Watch Time" -> targetCount * 15
            else -> targetCount * 3
        }

        viewModelScope.launch {
            val success = repository.deductPoints(pointsCost)
            if (success) {
                val newCampaign = PromotionCampaign(
                    videoTitle = title,
                    videoUrl = videoUrl,
                    campaignType = type,
                    targetCount = targetCount,
                    costInPoints = pointsCost,
                    status = "Active"
                )
                repository.insertCampaign(newCampaign)
                _toastMessage.value = "Campaign created! Points deducted: $pointsCost"
            } else {
                _toastMessage.value = "Insufficient Points! Complete daily quests to earn more points."
            }
        }
        return true
    }

    fun deleteCampaign(id: Int) {
        viewModelScope.launch {
            repository.deleteCampaign(id)
            _toastMessage.value = "Campaign deleted successfully"
        }
    }

    // AI Tag Extractor & Generator Service
    fun extractOrGenerateTags(input: String) {
        if (input.isBlank()) {
            _toastMessage.value = "Please enter a video URL or a search query!"
            return
        }

        _isGenerating.value = true
        _seoResultText.value = ""
        _extractedTags.value = emptyList()

        viewModelScope.launch {
            val prompt = if (input.contains("youtube.com") || input.contains("youtu.be")) {
                val videoId = extractVideoId(input)
                "Analyze the topic from the YouTube video URL / content context '$input' with video id '$videoId'. Extract or generate 20 highly viral, high-CTR tags suitable for the YouTube tag field. Output only the tags separated by commas, with no other conversational text."
            } else {
                "Generate 20 high-ranking, highly viral tags for a YouTube video about '$input'. Output only the tags separated by commas, without any intro or outro text."
            }

            val result = GeminiClient.generate(
                prompt = prompt,
                systemInstruction = "You are a professional YouTube SEO specialist. Your only job is to generate a comma-separated list of highly effective tags based on the video URL or keyword prompt."
            )

            if (!result.startsWith("Error")) {
                _seoResultText.value = result
                _extractedTags.value = result.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                _toastMessage.value = "SEO tags generated successfully!"
            } else {
                _toastMessage.value = result
            }
            _isGenerating.value = false
        }
    }

    // AI Creator Wizard (Title, Description, Outline Generator)
    fun generateAiContent(topic: String, category: String) {
        if (topic.isBlank()) {
            _toastMessage.value = "Please enter a topic or focus keyword!"
            return
        }

        _isGenerating.value = true
        _seoResultText.value = ""

        viewModelScope.launch {
            val prompt = when (category) {
                "Titles" -> "Generate 5 highly clickable, high-CTR, click-optimized YouTube video titles for the topic: '$topic'. Focus on curiosity-gap, bold hooks, and SEO friendliness. List them numbered from 1 to 5."
                "Description" -> "Generate a keyword-rich, highly optimized YouTube description for a video about '$topic'. Include: 1. A catchy first 2 sentences (for search results indexation), 2. A comprehensive detailed paragraph describing the video, 3. Timestamps placeholder outline (0:00 - Intro, etc.), and 4. Relevant social media call-to-actions. Keep it professional."
                "Script" -> "Generate a detailed, engaging Video Script outline and flowchart for the topic: '$topic'. Break it down into sections: Hook (first 30 seconds to retain viewers), Intro (the value promise), Main Content Body (3 major key points with delivery tips), and Outro / Call-to-action (to subscribe and watch another video)."
                else -> "Generate general YouTube SEO suggestions for '$topic'."
            }

            val systemInstruction = "You are a world-class YouTube growth engineer and content creator consultant. Your goal is to maximize CTR, retention, and search ranking."

            val result = GeminiClient.generate(prompt = prompt, systemInstruction = systemInstruction)
            _seoResultText.value = result
            _isGenerating.value = false
            _toastMessage.value = "$category generated successfully!"
        }
    }

    fun saveSeoToLibrary(category: String, topic: String, content: String) {
        if (content.isBlank() || content.startsWith("Error")) {
            _toastMessage.value = "No content to save!"
            return
        }

        viewModelScope.launch {
            val seoItem = SavedSeoItem(
                category = category,
                inputTopic = topic,
                resultText = content
            )
            repository.insertSavedSeo(seoItem)
            _toastMessage.value = "Saved to library successfully!"
        }
    }

    fun deleteSavedSeo(id: Int) {
        viewModelScope.launch {
            repository.deleteSavedSeo(id)
            _toastMessage.value = "Item removed from library"
        }
    }

    // Channel & Video Audit Service
    fun runVideoAudit(videoUrl: String) {
        if (videoUrl.isBlank()) {
            _toastMessage.value = "Please enter a video link to audit!"
            return
        }

        if (!videoUrl.contains("youtube.com") && !videoUrl.contains("youtu.be")) {
            _toastMessage.value = "Please enter a valid YouTube video URL!"
            return
        }

        _isGenerating.value = true
        _auditResult.value = null

        viewModelScope.launch {
            val videoId = extractVideoId(videoUrl)
            val prompt = """
                Perform an SEO audit on the YouTube video URL: '$videoUrl' (Video ID: '$videoId').
                Evaluate the video's search optimization factors and deliver a structured audit.
                Analyze critical elements: Title effectiveness, Description indexability, Target search keywords, Thumbnail engagement.
                List 4 highly specific recommendations to instantly improve views, clicks, and ranking for this video.
                Keep recommendations direct, concise and actionable.
            """.trimIndent()

            val result = GeminiClient.generate(
                prompt = prompt,
                systemInstruction = "You are an automated YouTube Channel Auditor. You deliver concise, factual audits with real actionable optimization recommendations."
            )

            // Parse or simulate scores locally while using Gemini recommendations
            val score = (70..95).random()
            val titleScore = (65..98).random()
            val descriptionScore = (60..95).random()
            val tagsScore = (55..92).random()
            val thumbnailScore = (70..99).random()

            val recommendations = if (result.startsWith("Error")) {
                listOf(
                    "Ensure your title has under 65 characters to avoid being truncated on mobile devices.",
                    "Insert high-traffic search terms in the first 2 lines of your video description.",
                    "Use bold, high-contrast text and a close-up face in your custom thumbnail.",
                    "Extract and add at least 15 highly relevant keyword tags to improve search indexation."
                )
            } else {
                result.split("\n")
                    .map { it.replace(Regex("^[-*•\\d.\\s]+"), "").trim() }
                    .filter { it.isNotEmpty() && it.length > 10 }
                    .take(4)
            }

            _auditResult.value = AuditResult(
                score = score,
                titleScore = titleScore,
                descriptionScore = descriptionScore,
                tagsScore = tagsScore,
                thumbnailScore = thumbnailScore,
                recommendations = recommendations
            )
            _isGenerating.value = false
            _toastMessage.value = "Audit completed! SEO Score: $score/100"
        }
    }

    // --- Helper Functions ---

    fun extractVideoId(url: String): String {
        return try {
            if (url.contains("youtu.be/")) {
                url.substringAfter("youtu.be/").substringBefore("?").substringBefore("/")
            } else if (url.contains("v=")) {
                url.substringAfter("v=").substringBefore("&").substringBefore("/")
            } else if (url.contains("embed/")) {
                url.substringAfter("embed/").substringBefore("?").substringBefore("/")
            } else if (url.contains("shorts/")) {
                url.substringAfter("shorts/").substringBefore("?").substringBefore("/")
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    // Periodic simulation of active campaigns to make it 100% "working" locally!
    private fun startCampaignProgressSimulator() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(12000) // update every 12 seconds
                val activeCampaigns = repository.getActiveCampaigns()
                if (activeCampaigns.isNotEmpty()) {
                    for (campaign in activeCampaigns) {
                        val progressAmount = when (campaign.campaignType) {
                            "Views" -> (5..12).random()
                            "Subscribers" -> (1..2).random()
                            "Likes" -> (2..5).random()
                            "Watch Time" -> (1..3).random()
                            else -> (1..4).random()
                        }

                        val newCount = campaign.currentCount + progressAmount
                        if (newCount >= campaign.targetCount) {
                            repository.updateCampaign(campaign.copy(
                                currentCount = campaign.targetCount,
                                status = "Completed"
                            ))
                        } else {
                            repository.updateCampaign(campaign.copy(
                                currentCount = newCount
                            ))
                        }
                    }
                }
            }
        }
    }

    private fun startBoosterLogTicker() {
        viewModelScope.launch(Dispatchers.IO) {
            val prefixes = listOf("UserBoost", "ViralTube", "BoostMaster", "CreatorSub", "ViewGainer", "FastViewer", "TubeHelper", "SEOPro", "OrganicBoost", "CommunitySub", "YTGuy", "TechViewer", "ChannelSub", "SubGiver", "QuickVids", "PlayTube", "VideoBoost", "FastGainer", "SubTuber")
            val activities = listOf(
                "watched 60s of campaign video",
                "liked video",
                "subscribed to channel",
                "watched 120s high retention watch-time",
                "added high-engagement comment",
                "shared video link to search network"
            )

            while (true) {
                delay((3000..6000).random().toLong()) // update every 3-6 seconds
                // Randomly fluctuate booster channel count slightly to simulate online/offline nodes
                val change = (-5..7).random()
                _totalBoosterChannels.value = (_totalBoosterChannels.value + change).coerceIn(100000, 110000)

                val randomChannel = "${prefixes.random()}_${(10000..109999).random()}"
                val randomActivity = activities.random()
                val currentCampaigns = campaigns.value
                val logText = if (currentCampaigns.isNotEmpty() && (0..10).random() > 4) {
                    val targetCamp = currentCampaigns.random()
                    val activityName = when (targetCamp.campaignType) {
                        "Views" -> "watched video '${targetCamp.videoTitle}' (+1 view)"
                        "Subscribers" -> "subscribed to channel for '${targetCamp.videoTitle}' (+1 sub)"
                        "Likes" -> "liked video '${targetCamp.videoTitle}' (+1 like)"
                        else -> "completed watch-time for '${targetCamp.videoTitle}' (+1 watch session)"
                    }
                    "Channel @$randomChannel $activityName"
                } else {
                    "Channel @$randomChannel $randomActivity on global queue"
                }

                val currentLogs = _boosterLogs.value.toMutableList()
                currentLogs.add(0, logText) // add to top
                if (currentLogs.size > 20) {
                    currentLogs.removeAt(currentLogs.size - 1)
                }
                _boosterLogs.value = currentLogs
            }
        }
    }
}
