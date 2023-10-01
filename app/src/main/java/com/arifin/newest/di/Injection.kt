package com.arifin.newest.di

import android.content.Context
import com.arifin.newest.data.UserRepository
import com.arifin.newest.data.database.StoryDatabase
import com.arifin.newest.data.preference.UserPreference
import com.arifin.newest.data.preference.dataStore
import com.arifin.newest.data.retrofit.ApiConfig
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val database = StoryDatabase.getDatabase(context)
        val apiServices = ApiConfig.getApiServices()

        return UserRepository.getInstance(pref, apiServices, database)
    }
}