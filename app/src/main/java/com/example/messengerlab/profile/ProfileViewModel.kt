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

    private val _isEditing = MutableLiveData(false)
    val isEditing: LiveData<Boolean> = _isEditing

    init {
        Log.d(TAG, "init")
        _name.value = userPreferences.profileName
        _bio.value = userPreferences.profileBio
        _statusIndex.value = userPreferences.profileStatusIndex
    }

    fun setEditing(editing: Boolean) {
        _isEditing.value = editing
    }

    fun saveProfile(newName: String, newBio: String, newStatusIndex: Int) {
        // Update LiveData
        _name.value = newName
        _bio.value = newBio
        _statusIndex.value = newStatusIndex

        // Persist to SharedPreferences
        userPreferences.profileName = newName
        userPreferences.profileBio = newBio
        userPreferences.profileStatusIndex = newStatusIndex

        Log.d(TAG, "Profile saved: Name=$newName, Bio=$newBio, Status=$newStatusIndex")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
