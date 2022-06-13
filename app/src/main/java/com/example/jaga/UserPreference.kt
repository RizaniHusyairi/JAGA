package com.example.jaga

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getUser(): Flow<User> {
        return dataStore.data.map { preference ->
            User(
                preference[USER_KEY] ?: "",
                preference[NUMBER_KEY]?: "",
                preference[NAMA_KEY] ?: "" ,
                preference[TGL_KEY] ?: "",
                preference[TENTANG_KEY] ?: "" ,
                preference[FOTO_KEY] ?: ""

            )
        }
    }

    suspend fun signIn(user: User) {
        dataStore.edit { preferences ->
            preferences[USER_KEY] = user.id ?: ""
            preferences[NUMBER_KEY] = user.number ?: ""
            preferences[NAMA_KEY] = user.name ?: ""
            preferences[TGL_KEY] = user.tgl_lahir ?: ""
            preferences[TENTANG_KEY] = user.tentang ?: ""
            preferences[FOTO_KEY] = user.foto ?: ""
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

        private val USER_KEY = stringPreferencesKey("id_user")
        private val NUMBER_KEY = stringPreferencesKey("number_user")
        private val NAMA_KEY = stringPreferencesKey("nama_user")
        private val TGL_KEY = stringPreferencesKey("tgl_user")
        private val TENTANG_KEY = stringPreferencesKey("tentang_user")
        private val FOTO_KEY = stringPreferencesKey("foto_user")

        fun getInstance(dataStore: DataStore<androidx.datastore.preferences.core.Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}