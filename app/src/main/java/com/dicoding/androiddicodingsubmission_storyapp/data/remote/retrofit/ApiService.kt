package com.dicoding.androiddicodingsubmission_storyapp.data.remote.retrofit

import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.DefaultResponse
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.LoginResponse
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.StoriesResponse
import com.dicoding.androiddicodingsubmission_storyapp.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun createUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<DefaultResponse>

    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Part photo: MultipartBody.Part,
        @Part("description") password: RequestBody,
    ): Call<DefaultResponse>

    @GET("stories")
    fun getAllStories(
        @Query("location") getWithLoc: Int = 0,
    ): Call<StoriesResponse>

    @GET("stories/{id}")
    @Headers("Authorization")
    fun getDetailStory(
        @Path("username") username: String
    ): Call<StoryResponse>
}