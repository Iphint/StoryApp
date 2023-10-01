package com.arifin.newest.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.arifin.newest.data.preference.UserPreference
import com.arifin.newest.data.response.ListStoryItem
import com.arifin.newest.data.retrofit.ApiServices
import kotlinx.coroutines.flow.first

class StoryPagingSource(
    private val apiService: ApiServices,
    private val userPreference: UserPreference
) : PagingSource<Int, ListStoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        val token = userPreference.getSession().first().token
        val page = params.key ?: 1
        return try {
            val response = apiService.getAllStories(token = "Bearer $token", page, params.loadSize)
            val stories = response.listStory
            LoadResult.Page(
                data = stories,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (stories.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}