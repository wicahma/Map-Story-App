package com.dicoding.androiddicodingsubmission_storyapp.data.local.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.StoryResponse

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(quote: List<StoryResponse>)

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, StoryResponse>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}