package com.arifin.newest.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.arifin.newest.data.UserRepository
import com.arifin.newest.data.preference.UserModel
import com.arifin.newest.data.response.ListStoryItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    val story: LiveData<PagingData<ListStoryItem>> = liveData {
        emitSource(repository.getStories())
    }

    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> get() = _token

    init {
        setToken()
    }

    private fun setToken() {
        viewModelScope.launch {
            _token.value = "Bearer " + repository.getSession().first().token
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}