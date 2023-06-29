package com.example.iglite.data.local.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferenceSession @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val userToken = stringPreferencesKey("userToken")

    fun getUserToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[this.userToken] ?: ""
        }
    }

    suspend fun saveUserToken(userToken: String) {
        dataStore.edit { preferences ->
            preferences[this.userToken] = userToken
        }
    }

    suspend fun clearUserToken() {
        dataStore.edit { preferences ->
            preferences[this.userToken] = ""
        }
    }
}