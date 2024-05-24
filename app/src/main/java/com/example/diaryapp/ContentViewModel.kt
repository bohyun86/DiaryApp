package com.example.diaryapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class DiaryViewModel(
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

    fun changeDateFormat(dateString: String): String {
        // 입력 형식 지정
        val inputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        // 출력 형식 지정
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // 입력 문자열을 Date 객체로 변환
        val date: Date? = inputFormat.parse(dateString)
        // Date 객체를 출력 형식의 문자열로 변환
        return outputFormat.format(date)
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
