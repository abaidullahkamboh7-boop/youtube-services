package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val points: Int = 1200, // Starts with some free points so they can run a campaign immediately!
    val channelName: String = "My Creator Channel",
    val channelUrl: String = ""
)

@Entity(tableName = "promotion_campaigns")
data class PromotionCampaign(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoTitle: String,
    val videoUrl: String,
    val campaignType: String, // "Views", "Subscribers", "Watch Time", "Likes", "Comments"
    val targetCount: Int,
    val currentCount: Int = 0,
    val costInPoints: Int,
    val status: String = "Active", // "Active", "Completed"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_seo_items")
data class SavedSeoItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // "Tags", "Titles", "Script"
    val inputTopic: String,
    val resultText: String,
    val timestamp: Long = System.currentTimeMillis()
)
