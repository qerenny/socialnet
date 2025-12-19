package com.example.socialnet.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    var profileName: String
        get() = prefs.getString(KEY_PROFILE_NAME, "Иван Иванов") ?: "Иван Иванов"
        set(value) = prefs.edit().putString(KEY_PROFILE_NAME, value).apply()

    var profileBio: String
        get() = prefs.getString(KEY_PROFILE_BIO, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PROFILE_BIO, value).apply()

    var profileStatusIndex: Int
        get() = prefs.getInt(KEY_PROFILE_STATUS_INDEX, 0)
        set(value) = prefs.edit().putInt(KEY_PROFILE_STATUS_INDEX, value).apply()

    var profileUsername: String
        get() = prefs.getString(KEY_PROFILE_USERNAME, "@username") ?: "@username"
        set(value) = prefs.edit().putString(KEY_PROFILE_USERNAME, value).apply()

    var profileBirthday: Long
        get() = prefs.getLong(KEY_PROFILE_BIRTHDAY, 0L)
        set(value) = prefs.edit().putLong(KEY_PROFILE_BIRTHDAY, value).apply()

    companion object {
        private const val PREFS_NAME = "messenger_lab_prefs"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_PROFILE_NAME = "profile_name"
        private const val KEY_PROFILE_BIO = "profile_bio"
        private const val KEY_PROFILE_STATUS_INDEX = "profile_status_index"
        private const val KEY_PROFILE_USERNAME = "profile_username"
        private const val KEY_PROFILE_BIRTHDAY = "profile_birthday"
    }
}
