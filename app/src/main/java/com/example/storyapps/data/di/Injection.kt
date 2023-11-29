package com.example.storyapps.data.di

import android.content.Context
import com.example.storyapps.data.database.ListStoryDatabase
import com.example.storyapps.data.pref.UserRepository
import com.example.storyapps.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context, token: String? = null): UserRepository {
        val database = ListStoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(token)
        return UserRepository(database, apiService)
    }
}