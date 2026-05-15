package com.ram.firechat.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.ram.firechat.UserSettings
import kotlinx.coroutines.flow.Flow

private val Context.protoDataStore : DataStore<UserSettings> by dataStore(
    fileName = "setting.pb",
    serializer = ProtoSerializer
)

class ProtoHandler(private val context: Context) {
    val savedData : Flow<UserSettings> = context.protoDataStore.data

    suspend fun saveData(userSettings: UserSettings) {
        context.protoDataStore.updateData { currentSetting ->
            currentSetting.toBuilder().setUserName(userSettings.userName)
                .setAccessLevel(userSettings.accessLevel)
                .setIsLoggedIn(userSettings.isLoggedIn)
                .build()
        }
    }

    suspend fun updateLoggedInStatus(context: Context, loggedIn: Boolean) {
        context.protoDataStore.updateData {
            it.toBuilder().setIsLoggedIn(loggedIn)
                .build()
        }
    }
}