package com.example.diaryapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ContentViewModel(
    private val contentRepository: ContentRepository
) : ViewModel() {
    private val _contents = MutableStateFlow<List<Content>>(emptyList())
    val contents: StateFlow<List<Content>> get() = _contents
    val currentContent = MutableStateFlow<Content?>(null)

    fun getContentsByUserId(userId: String) {
        viewModelScope.launch {
            val data = contentRepository.getContentsByUserId(userId)
            _contents.value = data
        }
    }

    fun getContentsById(contentId: Int) {
        viewModelScope.launch {
            val data = contentRepository.getContentsById(contentId)
            currentContent.value = data
        }
    }

    fun updateContent(contentId: Int, contentDate: String, contentDetail: String, userId: String) {
            viewModelScope.launch {
                currentContent.value = Content(
                    contentId = contentId,
                    contentDate = contentDate,
                    contentDetail = contentDetail,
                    userId = userId
                )
                contentRepository.updateContent(currentContent.value!!)
            }
    }

    fun insertContent(contentDate: String, contentDetail: String, userId: String) {
        viewModelScope.launch {
            currentContent.value = Content(
                contentId = 0,
                contentDate = contentDate,
                contentDetail = contentDetail,
                userId = userId
            )
            contentRepository.insertContent(currentContent.value!!)
            getContentsByUserId(userId)
        }
    }

    fun deleteContent() {
        viewModelScope.launch {
            currentContent.value?.let { contentRepository.deleteContent(it.contentId) }
        }
    }
}

class DiaryViewModelFactory(
    private val contentRepository: ContentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContentViewModel(contentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
