package com.dicoding.androiddicodingsubmission_storyapp.data.remote.retrofit

import android.util.Log
import com.dicoding.androiddicodingsubmission_storyapp.BuildConfig
import com.dicoding.androiddicodingsubmission_storyapp.utils.SettingPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {

    companion object {
        fun getApiService(preferences: SettingPreferences): ApiService {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val authInterceptor = Interceptor { chain ->
                val req = chain.request()
                val token = runBlocking { preferences.getUserToken().first() }
                Log.d("Token APIs", token.toString())
                val requestHeaders = req.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(requestHeaders)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()
            val retrofit = Retrofit.Builder().baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create()).client(client).build()
            return retrofit.create(ApiService::class.java)
        }
    }
}