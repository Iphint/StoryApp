package com.arifin.newest.view.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arifin.newest.data.response.ListStoryItem
import com.arifin.newest.data.response.LocationResponse
import com.arifin.newest.data.retrofit.ApiConfig
import com.arifin.newest.view.main.MainViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel : ViewModel() {

    private val _location = MutableLiveData<List<ListStoryItem>>()
    val location: LiveData<List<ListStoryItem>> = _location

    private val _isLoading = MutableLiveData<Boolean>()

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }

    fun getLocation(token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiServices().getLocation(token = "Bearer $token", 100)
        client.enqueue(object : Callback<LocationResponse> {
            override fun onResponse(
                call: Call<LocationResponse>,
                response: Response<LocationResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _location.value = responseBody.listStory
                    Log.d(TAG, responseBody.listStory.toString())
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}