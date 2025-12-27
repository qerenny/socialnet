package com.example.socialnet.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.socialnet.data.db.MessageEntity
import com.example.socialnet.data.repository.MessageRepository
import kotlinx.coroutines.launch

class FeedViewModel(private val repository: MessageRepository) : ViewModel() {

    private val _messages = MutableLiveData<List<MessageEntity>>()
    val messages: LiveData<List<MessageEntity>> = _messages

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
                _messages.value = repository.getMessages()
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleLike(messageId: Int) {
        val currentList = _messages.value ?: return
        val message = currentList.find { it.id == messageId } ?: return

        viewModelScope.launch {
            repository.toggleLike(messageId, message.isLiked)

            // Optimistically update the UI or reload
            // Ideally we would observe a DB stream, but for now let's update the live data manually or reload
            // To be simple and robust: reload from DB (which is fast) or update list locally

            // Local update for immediate feedback
            val updatedList = currentList.map {
                if (it.id == messageId) it.copy(isLiked = !message.isLiked) else it
            }
            _messages.value = updatedList
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
