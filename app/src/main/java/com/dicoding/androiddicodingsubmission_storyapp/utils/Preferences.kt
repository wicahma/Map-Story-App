package com.dicoding.androiddicodingsubmission_storyapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
    private val USER_NAME = stringPreferencesKey("user_name")

    fun getUserName(): Flow<String?> {
        return dataStore.data.map { preferences -> preferences[USER_NAME] }
    }

    suspend fun setUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    fun getUserToken(): Flow<String?> {
        return dataStore.data.map { preferences -> preferences[USER_TOKEN_KEY] }
    }

    suspend fun setUserToken(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
        }
    }

    suspend fun clearUserToken() {
        dataStore.edit { preferences ->
            preferences.remove(USER_TOKEN_KEY)
        }
    }

    suspend fun clearUserName() {
        dataStore.edit { preferences ->
            preferences.remove(USER_NAME)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}