package com.example.diaryapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DiaryViewModel(
    private val contentRepository: ContentRepository
) : ViewModel() {
    private val _contents = MutableStateFlow<List<Content>>(emptyList())
    val contents: StateFlow<List<Content>> get() = _contents

    fun getContentsByUserId(userId: String) {
        viewModelScope.launch {
            val data = contentRepository.getContentsByUserId(userId)
            _contents.value = data
        }
    }

    fun insertContent(contentDetail: String, userId: String) {
        viewModelScope.launch {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val content = Content(
                contentId = 0,
                contentDate = LocalDate.now().format(formatter),
                contentDetail = contentDetail,
                userId = userId
            )
            contentRepository.insertContent(content)
            getContentsByUserId(userId)
        }
    }
}

class DiaryViewModelFactory(
    private val contentRepository: ContentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiaryViewModel(contentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
