package com.example.socialnet.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialnet.SocialNetApplication
import com.example.socialnet.data.UserPreferences
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val repository = (application as SocialNetApplication).repository

    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    private val _refreshStatus = MutableLiveData<String?>()
    val refreshStatus: LiveData<String?> = _refreshStatus

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

    fun refreshFeed() {
        viewModelScope.launch {
            try {
                repository.getMessages() // This triggers API call and DB update
                _refreshStatus.value = "Feed refreshed successfully"
            } catch (e: Exception) {
                _refreshStatus.value = "Failed to refresh feed: ${e.message}"
            }
        }
    }

    fun clearRefreshStatus() {
        _refreshStatus.value = null
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}
