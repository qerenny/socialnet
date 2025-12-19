package com.example.socialnet.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.socialnet.data.UserPreferences

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _bio = MutableLiveData<String>()
    val bio: LiveData<String> = _bio

    private val _statusIndex = MutableLiveData<Int>()
    val statusIndex: LiveData<Int> = _statusIndex

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _birthday = MutableLiveData<Long>()
    val birthday: LiveData<Long> = _birthday

    private val _isEditing = MutableLiveData(false)
    val isEditing: LiveData<Boolean> = _isEditing

    init {
        Log.d(TAG, "init")
        _name.value = userPreferences.profileName
        _bio.value = userPreferences.profileBio
        _statusIndex.value = userPreferences.profileStatusIndex
        _username.value = userPreferences.profileUsername
        _birthday.value = userPreferences.profileBirthday
    }

    fun setEditing(editing: Boolean) {
        _isEditing.value = editing
    }

    // Update Draft State
    fun onNameChanged(newName: String) {
        if (_name.value != newName) {
            _name.value = newName
        }
    }

    fun onBioChanged(newBio: String) {
        if (_bio.value != newBio) {
            _bio.value = newBio
        }
    }

    fun onStatusChanged(newIndex: Int) {
        if (_statusIndex.value != newIndex) {
            _statusIndex.value = newIndex
        }
    }

    fun onUsernameChanged(newUsername: String) {
        if (_username.value != newUsername) {
            _username.value = newUsername
        }
    }

    fun onBirthdayChanged(newBirthday: Long) {
        if (_birthday.value != newBirthday) {
            _birthday.value = newBirthday
        }
    }

    fun saveProfile() {
        val currentName = _name.value ?: ""
        val currentBio = _bio.value ?: ""
        val currentStatusIndex = _statusIndex.value ?: 0
        val currentUsername = _username.value ?: ""
        val currentBirthday = _birthday.value ?: 0L

        userPreferences.profileName = currentName
        userPreferences.profileBio = currentBio
        userPreferences.profileStatusIndex = currentStatusIndex
        userPreferences.profileUsername = currentUsername
        userPreferences.profileBirthday = currentBirthday

        Log.d(TAG, "Profile saved: Name=$currentName, Bio=$currentBio, Status=$currentStatusIndex, Username=$currentUsername, Birthday=$currentBirthday")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
