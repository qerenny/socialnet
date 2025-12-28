package com.example.socialnet.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialnet.data.db.MessageEntity
import com.example.socialnet.data.repository.MessageRepository
import kotlinx.coroutines.launch

class FeedViewModel(private val repository: MessageRepository) : ViewModel() {

    val messages: LiveData<List<MessageEntity>> = repository.messages.asLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadMessages()
    }

    fun loadMessages() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.refreshMessages()
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleLike(messageId: Int) {
        val currentList = messages.value ?: return
        val message = currentList.find { it.id == messageId } ?: return

        viewModelScope.launch {
            repository.toggleLike(messageId, message.isLiked)
            // No need to manually update _messages, the repository Flow will emit the change
        }
    }
}

class FeedViewModelFactory(private val repository: MessageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FeedViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
