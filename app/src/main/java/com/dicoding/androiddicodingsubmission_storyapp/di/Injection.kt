package com.dicoding.androiddicodingsubmission_storyapp.di

import android.content.Context
import com.dicoding.androiddicodingsubmission_storyapp.data.StoryRepository
import com.dicoding.androiddicodingsubmission_storyapp.data.local.db.StoryDatabase
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.androiddicodingsubmission_storyapp.utils.SettingPreferences
import com.dicoding.androiddicodingsubmission_storyapp.utils.dataStore

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val pref = SettingPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return StoryRepository.getInstance(apiService, pref, database)
    }
}