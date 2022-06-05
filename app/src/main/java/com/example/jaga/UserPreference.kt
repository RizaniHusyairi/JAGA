package com.example.jaga

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getUser(): Flow<User> {
        return dataStore.data.map { preference ->
            User(
                preference[NUMBER_KEY] ?: "",
                preference[NAME_KEY] ?: "",
                preference[STATE_KEY] ?: false,
            )
        }
    }

    suspend fun signIn(user: User) {
        dataStore.edit { preferences ->
            preferences[NUMBER_KEY] = user.number
            preferences[NAME_KEY] = user.name
            preferences[STATE_KEY] = user.isLogin
        }
    }

    suspend fun signOut() {
        dataStore.edit {
            it.clear()
        }
    }



    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val NUMBER_KEY = stringPreferencesKey("number")
        private val NAME_KEY = stringPreferencesKey("name")
        private val STATE_KEY = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<androidx.datastore.preferences.core.Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}