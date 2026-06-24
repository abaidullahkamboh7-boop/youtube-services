package com.example.data.repository

import com.example.data.database.PromotionCampaign
import com.example.data.database.SavedSeoItem
import com.example.data.database.UserStats
import com.example.data.database.YoutubeDao
import kotlinx.coroutines.flow.Flow

class YoutubeRepository(private val youtubeDao: YoutubeDao) {

    val userStatsFlow: Flow<UserStats?> = youtubeDao.getUserStatsFlow()
    val allCampaignsFlow: Flow<List<PromotionCampaign>> = youtubeDao.getAllCampaignsFlow()
    val allSavedSeoFlow: Flow<List<SavedSeoItem>> = youtubeDao.getAllSavedSeoFlow()

    suspend fun getOrInitializeStats(): UserStats {
        val stats = youtubeDao.getUserStats()
        return if (stats == null) {
            val defaultStats = UserStats()
            youtubeDao.insertUserStats(defaultStats)
            defaultStats
        } else {
            stats
        }
    }

    suspend fun updateStats(stats: UserStats) {
        youtubeDao.updateUserStats(stats)
    }

    suspend fun addPoints(amount: Int) {
        val stats = getOrInitializeStats()
        youtubeDao.updateUserStats(stats.copy(points = stats.points + amount))
    }

    suspend fun deductPoints(amount: Int): Boolean {
        val stats = getOrInitializeStats()
        return if (stats.points >= amount) {
            youtubeDao.updateUserStats(stats.copy(points = stats.points - amount))
            true
        } else {
            false
        }
    }

    suspend fun insertCampaign(campaign: PromotionCampaign) {
        youtubeDao.insertCampaign(campaign)
    }

    suspend fun updateCampaign(campaign: PromotionCampaign) {
        youtubeDao.updateCampaign(campaign)
    }

    suspend fun getActiveCampaigns(): List<PromotionCampaign> {
        return youtubeDao.getActiveCampaigns()
    }

    suspend fun deleteCampaign(campaignId: Int) {
        youtubeDao.deleteCampaign(campaignId)
    }

    suspend fun insertSavedSeo(seoItem: SavedSeoItem) {
        youtubeDao.insertSavedSeo(seoItem)
    }

    suspend fun deleteSavedSeo(seoId: Int) {
        youtubeDao.deleteSavedSeo(seoId)
    }
}
