package com.xsc.oneapp.config.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.xsc.oneapp.config.model.RemoteAppConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.configDataStore: DataStore<Preferences> by preferencesDataStore(name = "oneapp_configuration")

@Singleton
class ConfigurationDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val CONFIG_KEY = stringPreferencesKey("remote_app_config_json")

    val appConfigFlow: Flow<RemoteAppConfig> = context.configDataStore.data.map { preferences ->
        val jsonString = preferences[CONFIG_KEY]
        if (!jsonString.isNullOrBlank()) {
            try {
                gson.fromJson(jsonString, RemoteAppConfig::class.java)
            } catch (e: Exception) {
                RemoteAppConfig()
            }
        } else {
            RemoteAppConfig()
        }
    }

    suspend fun saveAppConfig(config: RemoteAppConfig) {
        val jsonString = gson.toJson(config)
        context.configDataStore.edit { preferences ->
            preferences[CONFIG_KEY] = jsonString
        }
    }

    suspend fun clearConfig() {
        context.configDataStore.edit { preferences ->
            preferences.remove(CONFIG_KEY)
        }
    }
}
