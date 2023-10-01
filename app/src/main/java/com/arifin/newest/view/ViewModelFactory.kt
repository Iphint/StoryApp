package com.arifin.newest.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arifin.newest.data.UserRepository
import com.arifin.newest.data.preference.UserPreference
import com.arifin.newest.data.preference.dataStore
import com.arifin.newest.data.retrofit.ApiServices
import com.arifin.newest.di.Injection
import com.arifin.newest.view.login.LoginViewModel
import com.arifin.newest.view.main.MainViewModel
import com.arifin.newest.view.tambah.TambahActivityViewModel

class ViewModelFactory(
    private val repository: UserRepository,
    private val pref: UserPreference
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(TambahActivityViewModel::class.java) -> {
                TambahActivityViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                val repository = Injection.provideRepository(context)
                val pref = UserPreference.getInstance(context.dataStore)
                ViewModelFactory(repository, pref).also { INSTANCE = it }
            }
        }
    }
}
