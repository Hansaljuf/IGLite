package com.example.iglite.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.iglite.data.local.model.Story

@Dao
interface StoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(story: List<Story>)

    @Query("SELECT * FROM stories ")
    fun getAllStories(): PagingSource<Int, Story>

    @Query("DELETE FROM stories")
    suspend fun deleteAllStories()

    @Query("SELECT * FROM stories")
    suspend fun getStoriesFromDb(): List<Story>
}