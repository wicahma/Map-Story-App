package com.dicoding.androiddicodingsubmission_storyapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.androiddicodingsubmission_storyapp.data.StoryRepository
import com.dicoding.androiddicodingsubmission_storyapp.utils.SettingPreferences
import kotlinx.coroutines.runBlocking

class StoryViewModel(
    private val preferences: SettingPreferences, private val storyRepository: StoryRepository
) : ViewModel() {

    fun getAllStory() = storyRepository.getAllStory()

    fun getUsername() = storyRepository.getName().asLiveData()

    fun logoutUser() {
        runBlocking {
            preferences.clearUserToken()
        }
    }
}