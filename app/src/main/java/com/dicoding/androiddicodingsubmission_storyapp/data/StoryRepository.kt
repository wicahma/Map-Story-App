package com.dicoding.androiddicodingsubmission_storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.DefaultResponse
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.LoginResponse
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.StoriesResponse
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.StoryResponse
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.retrofit.ApiService
import com.dicoding.androiddicodingsubmission_storyapp.utils.AppExecutors
import com.dicoding.androiddicodingsubmission_storyapp.utils.SettingPreferences
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StoryRepository private constructor(
    private val apiService: ApiService,
    private val appExecutors: AppExecutors,
    private val pref: SettingPreferences
) {
    fun getToken() = pref.getUserToken()

    fun getName() = pref.getUserName()

    fun saveToken(token: String) = runBlocking { pref.setUserToken(token) }

    fun saveUserName(name: String) = runBlocking { pref.setUserName(name) }

    fun loginUser(email: String, pass: String): LiveData<Result<LoginResponse>> {
        val result = MediatorLiveData<Result<LoginResponse>>()
        result.value = Result.Loading
        val client = apiService.loginUser(
            email, pass
        )

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>, res: Response<LoginResponse>
            ) {
                if (res.body()?.error == false) {
                    result.value = Result.Success(res.body()!!)
                } else {
                    val jsonInString = res.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, DefaultResponse::class.java)
                    val errorMessage = errorBody.message
                    result.value = Result.Error(errorMessage.toString())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })
        return result
    }

    fun registerUser(
        email: String, pass: String, name: String
    ): LiveData<Result<DefaultResponse>> {
        val result = MediatorLiveData<Result<DefaultResponse>>()
        result.value = Result.Loading
        val client = apiService.createUser(
            name, email, pass
        )
        client.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(
                call: Call<DefaultResponse>, res: Response<DefaultResponse>
            ) {
                if (res.body()?.error == false) {
                    result.value = Result.Success(res.body()!!)
                } else {
                    val jsonInString = res.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, DefaultResponse::class.java)
                    val errorMessage = errorBody.message
                    result.value = Result.Error(errorMessage.toString())
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })
        return result
    }

    fun getAllStory(): LiveData<Result<List<StoryResponse?>>> {
        val result = MediatorLiveData<Result<List<StoryResponse?>>>()
        result.value = Result.Loading
        val client = apiService.getAllStories()
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(call: Call<StoriesResponse>, res: Response<StoriesResponse>) {
                if (res.body()?.error == false) {
                    val storyData = res.body()?.listStory!!
                    result.value = Result.Success(storyData)
                } else {
                    val jsonInString = res.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, DefaultResponse::class.java)
                    val errorMessage = errorBody.message
                    result.value = Result.Error(errorMessage.toString())
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })

        return result
    }

    fun uploadStory(
        rbDesc: RequestBody,
        multipartBodyImage: MultipartBody.Part
    ): LiveData<Result<DefaultResponse>> {
        val result = MediatorLiveData<Result<DefaultResponse>>()
        result.value = Result.Loading

        val client = apiService.addNewStory(multipartBodyImage, rbDesc)
        client.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, res: Response<DefaultResponse>) {
                if (res.body()?.error == false) {
                    result.value = Result.Success(res.body()!!)
                } else {
                    val jsonInString = res.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, DefaultResponse::class.java)
                    val errorMessage = errorBody.message
                    result.value = Result.Error(errorMessage.toString())
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })

        return result
    }


    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService, appExecutors: AppExecutors, preferences: SettingPreferences
        ): StoryRepository = instance ?: synchronized(this) {
            instance ?: StoryRepository(apiService, appExecutors, preferences)
        }.also { instance = it }
    }
}