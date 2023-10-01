package com.arifin.newest.view.tambah

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.arifin.newest.data.preference.UserPreference
import com.arifin.newest.data.response.LoginResult

class TambahActivityViewModel(private val pref: UserPreference) : ViewModel() {

    fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }
}