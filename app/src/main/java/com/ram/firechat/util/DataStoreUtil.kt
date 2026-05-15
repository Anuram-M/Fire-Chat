package com.ram.firechat.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore by preferencesDataStore(name = "my_datastore")

object MyDataStoreKeys {
    val USER_NAME = stringPreferencesKey("userName")
    val PASSWORD = stringPreferencesKey("passWord")
}

class DataStoreUtil(val context: Context) {
    val userName: Flow<String?> = context.datastore.data.map { item->
        item[MyDataStoreKeys.USER_NAME]
    }

        suspend fun saveDate(name: String, pass: String) {
        context.datastore.edit { datastore ->
            datastore[MyDataStoreKeys.USER_NAME] = name
            datastore[MyDataStoreKeys.PASSWORD] = pass
        }
    }
}