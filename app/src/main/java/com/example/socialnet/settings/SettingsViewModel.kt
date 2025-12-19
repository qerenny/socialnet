package com.example.messengerlab.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.messengerlab.data.UserPreferences

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    init {
        Log.d(TAG, "init")
        _isDarkMode.value = userPreferences.isDarkMode
    }

    fun setDarkMode(enabled: Boolean) {
        if (_isDarkMode.value != enabled) {
            _isDarkMode.value = enabled
            userPreferences.isDarkMode = enabled
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}
