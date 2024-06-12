package com.dicoding.androiddicodingsubmission_storyapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.dicoding.androiddicodingsubmission_storyapp.data.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoryLocation() = storyRepository.getStoryLocation()
}