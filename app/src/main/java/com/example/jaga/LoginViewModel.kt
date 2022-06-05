package com.example.jaga

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreference) : ViewModel() {
    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }

    fun login(user: User) {
        viewModelScope.launch {
            pref.signIn(user)
        }
    }

    fun logout() {
        viewModelScope.launch {
            pref.signOut()
        }
    }
}