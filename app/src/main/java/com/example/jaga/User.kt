package com.example.jaga

import com.google.firebase.database.IgnoreExtraProperties


data class User(
    val number: String,
    val name: String,
    val isLogin: Boolean
)
