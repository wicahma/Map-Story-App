package com.dicoding.androiddicodingsubmission_storyapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.androiddicodingsubmission_storyapp.data.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginViewModel(private val storyRepository: StoryRepository
) : ViewModel() {

    fun login(email: String, pass: String) = storyRepository.loginUser(email, pass)

    fun setToken(token: String) {
        viewModelScope.launch {
            storyRepository.saveToken(token)
        }
    }

    fun setName(name: String) {
        viewModelScope.launch {
            storyRepository.saveUserName(name)
        }
    }

    fun getToken() = storyRepository.getToken().asLiveData()

    fun getUsername() = runBlocking { storyRepository.getName().first() }
}