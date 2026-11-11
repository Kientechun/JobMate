package com.example.jobmate.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(@ApplicationContext private val context: Context) {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_ROLE = stringPreferencesKey("user_role")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_NAME = stringPreferencesKey("user_name")
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { it[ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[REFRESH_TOKEN] }
    val userRole: Flow<String?> = context.dataStore.data.map { it[USER_ROLE] }
    val userName: Flow<String?> = context.dataStore.data.map { it[USER_NAME] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[USER_EMAIL] }

    suspend fun saveTokens(access: String, refresh: String, role: String, email: String, name: String) {
        context.dataStore.edit {
            it[ACCESS_TOKEN] = access
            it[REFRESH_TOKEN] = refresh
            it[USER_ROLE] = role
            it[USER_EMAIL] = email
            it[USER_NAME] = name
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
