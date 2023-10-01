package com.arifin.newest.data.preference

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)