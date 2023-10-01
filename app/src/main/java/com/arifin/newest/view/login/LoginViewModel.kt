package com.arifin.newest.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arifin.newest.data.UserRepository
import com.arifin.newest.data.preference.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.login(email, password)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && !loginResponse.error) {
                        // Login sukses, simpan token ke DataStore
                        val token = loginResponse.loginResult.token
                        repository.saveSession(UserModel(email, token))
                        onSuccess.invoke(token) // Kirim token sebagai parameter
                    } else {
                        onError.invoke("Login gagal. Email atau password salah.")
                    }
                } else {
                    onError.invoke("Login gagal. Terjadi kesalahan server.")
                }
            } catch (e: Exception) {
                onError.invoke("Login gagal. Terjadi kesalahan jaringan.")
            }
        }
    }
}