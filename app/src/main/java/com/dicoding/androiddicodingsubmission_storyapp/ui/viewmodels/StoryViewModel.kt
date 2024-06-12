package com.dicoding.androiddicodingsubmission_storyapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.androiddicodingsubmission_storyapp.data.StoryRepository
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.StoryResponse

class StoryViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {

    val getAllStory: LiveData<PagingData<StoryResponse>> =
        storyRepository.getAllStory().cachedIn(viewModelScope)

    fun getUsername() = storyRepository.getName().asLiveData()

    fun logoutUser() = storyRepository.logoutUser()
}