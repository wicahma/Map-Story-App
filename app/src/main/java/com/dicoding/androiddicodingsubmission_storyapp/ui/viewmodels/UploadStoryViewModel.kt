package com.dicoding.androiddicodingsubmission_storyapp.ui

import android.net.Uri
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.androiddicodingsubmission_storyapp.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _imageUri = MediatorLiveData<Uri>()
    val imageUri = _imageUri

    fun uploadStory(
        rbDesc: RequestBody,
        multipartBodyImage: MultipartBody.Part,
        lat: RequestBody?,
        lng: RequestBody?
    ) =
        storyRepository.uploadStory(rbDesc, multipartBodyImage, lat, lng)

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }
}