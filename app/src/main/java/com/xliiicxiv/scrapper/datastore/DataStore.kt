package com.xliiicxiv.scrapper.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class DataStore(
    private val context: Context
) {

    companion object {
        val userId = stringPreferencesKey("userId")
    }

    val getUserId = context.dataStore.data.map {
        it[userId] ?: ""
    }

    suspend fun setUserId(id: String) {
        context.dataStore.edit {
            it[userId] = id
        }
    }
}