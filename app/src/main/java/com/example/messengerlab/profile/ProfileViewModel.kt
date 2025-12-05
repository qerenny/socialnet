package com.example.messengerlab.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.messengerlab.data.UserPreferences

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _bio = MutableLiveData<String>()
    val bio: LiveData<String> = _bio

    private val _statusIndex = MutableLiveData<Int>()
    val statusIndex: LiveData<Int> = _statusIndex

    init {
        Log.d(TAG, "init")
        _name.value = userPreferences.profileName
        _bio.value = userPreferences.profileBio
        _statusIndex.value = userPreferences.profileStatusIndex
    }

    fun updateName(newName: String) {
        if (_name.value != newName) {
            _name.value = newName
            userPreferences.profileName = newName
        }
    }

    fun updateBio(newBio: String) {
        if (_bio.value != newBio) {
            _bio.value = newBio
            userPreferences.profileBio = newBio
        }
    }

    fun updateStatus(newIndex: Int) {
        if (_statusIndex.value != newIndex) {
            _statusIndex.value = newIndex
            userPreferences.profileStatusIndex = newIndex
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
