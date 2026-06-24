package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface YoutubeDao {

    // User Stats Queries
    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    fun getUserStatsFlow(): Flow<UserStats?>

    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    suspend fun getUserStats(): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStats)

    @Update
    suspend fun updateUserStats(stats: UserStats)

    // Campaigns Queries
    @Query("SELECT * FROM promotion_campaigns ORDER BY timestamp DESC")
    fun getAllCampaignsFlow(): Flow<List<PromotionCampaign>>

    @Query("SELECT * FROM promotion_campaigns WHERE status = 'Active'")
    suspend fun getActiveCampaigns(): List<PromotionCampaign>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaign(campaign: PromotionCampaign)

    @Update
    suspend fun updateCampaign(campaign: PromotionCampaign)

    @Query("DELETE FROM promotion_campaigns WHERE id = :campaignId")
    suspend fun deleteCampaign(campaignId: Int)

    // Saved SEO Items Queries
    @Query("SELECT * FROM saved_seo_items ORDER BY timestamp DESC")
    fun getAllSavedSeoFlow(): Flow<List<SavedSeoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedSeo(seoItem: SavedSeoItem)

    @Query("DELETE FROM saved_seo_items WHERE id = :seoId")
    suspend fun deleteSavedSeo(seoId: Int)
}
