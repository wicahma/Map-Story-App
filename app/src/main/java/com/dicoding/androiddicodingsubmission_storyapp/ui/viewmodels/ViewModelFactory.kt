package com.dicoding.androiddicodingsubmission_storyapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.dicoding.androiddicodingsubmission_storyapp.data.StoryRepository
import com.dicoding.androiddicodingsubmission_storyapp.di.Injection
import com.dicoding.androiddicodingsubmission_storyapp.utils.SettingPreferences
import com.dicoding.androiddicodingsubmission_storyapp.utils.dataStore

class ViewModelFactory private constructor(
    private val storyRepository: StoryRepository,
    private val pref: SettingPreferences
) : ViewModelProvider.Factory {

    companion object : ViewModelProvider.Factory {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory = instance ?: synchronized(this) {
            instance ?: ViewModelFactory(
                Injection.provideRepository(context),
                pref = SettingPreferences.getInstance(context.dataStore)
            )
        }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        with(modelClass) {
            when {
                isAssignableFrom(StoryViewModel::class.java) -> StoryViewModel(
                    pref,
                    storyRepository
                )

                isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(
                    pref,
                    storyRepository
                )

                isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(storyRepository)
                isAssignableFrom(UploadStoryViewModel::class.java) -> UploadStoryViewModel(
                    storyRepository
                )

                isAssignableFrom(DetailStoryViewModel::class.java) -> DetailStoryViewModel(
                    storyRepository
                )

                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}