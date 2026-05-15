package com.ram.firechat.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PreferenceUtil {

    private fun getInstance(context: Context): SharedPreferences {
        return context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    }

    fun putString(context: Context, key: String, value: String) {
        getInstance(context).edit { putString(key, value) }
    }

    fun getString(context: Context, key: String) : String? {
        return getInstance(context).getString(key, "empty")
    }
}