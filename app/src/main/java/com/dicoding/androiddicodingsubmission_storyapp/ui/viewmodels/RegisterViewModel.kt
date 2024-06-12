package com.dicoding.androiddicodingsubmission_storyapp.ui

import androidx.lifecycle.ViewModel
import com.dicoding.androiddicodingsubmission_storyapp.data.StoryRepository

class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun register(name: String, email: String, pass: String) =
        storyRepository.registerUser(email, pass, name)

}