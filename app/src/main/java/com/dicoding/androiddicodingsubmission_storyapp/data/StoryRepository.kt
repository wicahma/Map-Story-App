package com.dicoding.androiddicodingsubmission_storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.androiddicodingsubmission_storyapp.data.local.db.StoryDatabase
import com.dicoding.androiddicodingsubmission_storyapp.data.local.db.StoryRemoteMediator
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.DefaultResponse
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.LoginResponse
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.StoriesResponse
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.StoryResponse
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.retrofit.ApiService
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
    private val pref: SettingPreferences,
    private val database: StoryDatabase
) {
    fun getToken() = pref.getUserToken()

    fun getName() = pref.getUserName()

    fun saveToken(token: String) = runBlocking { pref.setUserToken(token) }

    fun saveUserName(name: String) = runBlocking { pref.setUserName(name) }

    fun logoutUser() {
        runBlocking {
            pref.clearUserToken()
            pref.clearUserName()
        }
    }

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

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStory(): LiveData<PagingData<StoryResponse>> {
        return Pager(
            config = PagingConfig(
                pageSize = 3
            ),
            pagingSourceFactory = {
                database.storyDao().getAllStory()
            },
            remoteMediator = StoryRemoteMediator(database, apiService)
        ).liveData
    }

    fun getStoryLocation(): LiveData<Result<StoriesResponse>> {
        val result = MediatorLiveData<Result<StoriesResponse>>()
        result.value = Result.Loading
        val client = apiService.getStoriesWithLocation()
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>, res: Response<StoriesResponse>
            ) {
                if (res.body()?.error == false) {
                    result.value = Result.Success(res.body()!!)
                } else {
                    val jsonInString = res.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, StoriesResponse::class.java)
                    val errorMessage = errorBody.message
                    result.value = Result.Error(errorMessage)
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
        multipartBodyImage: MultipartBody.Part,
        lat: RequestBody?,
        lng: RequestBody?
    ): LiveData<Result<DefaultResponse>> {
        val result = MediatorLiveData<Result<DefaultResponse>>()
        result.value = Result.Loading

        val client = apiService.addNewStory(multipartBodyImage, rbDesc, lat, lng)
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
            apiService: ApiService,
            preferences: SettingPreferences,
            database: StoryDatabase
        ): StoryRepository = instance ?: synchronized(this) {
            instance ?: StoryRepository(apiService, preferences, database)
        }.also { instance = it }
    }
}