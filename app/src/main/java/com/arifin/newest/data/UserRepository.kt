package com.arifin.newest.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.arifin.newest.data.database.StoryDatabase
import com.arifin.newest.data.preference.UserModel
import com.arifin.newest.data.preference.UserPreference
import com.arifin.newest.data.response.ListStoryItem
import com.arifin.newest.data.response.LoginResponse
import com.arifin.newest.data.retrofit.ApiServices
import com.arifin.newest.paging.StoryPagingSource
import com.arifin.newest.paging.StoryRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.Response

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiServices: ApiServices,
    private val storyDatabase: StoryDatabase,
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(apiServices, storyDatabase, userPreference),
            pagingSourceFactory = {
                StoryPagingSource(apiServices, userPreference)
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return apiServices.login(email, password)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            userPreference: UserPreference,
            apiServices: ApiServices,
            storyDatabase: StoryDatabase,
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiServices, storyDatabase)
            }.also { instance = it }
    }
}