package com.example.diaryapp

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryViewModel(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository
) : ViewModel() {


    var currentUserId: String = "defaultUserId" // 실제로는 로그인한 사용자 ID를 설정

    fun insertUser(userId: String, userPw: String, userEmail: String) {
        val user = User(userId, userPw, userEmail)
        viewModelScope.launch {
            userRepository.insertUser(user)
        }
    }

    fun insertContent(contentDetail: String) {
        val contentId = System.currentTimeMillis().toString()
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val content = Content(contentId, currentDate, contentDetail, currentUserId)
        viewModelScope.launch {
            contentRepository.insertContent(content)
        }
    }

    fun getContentsByUserId(userId: String, onResult: (List<Content>) -> Unit) {
        viewModelScope.launch {
            val contents = contentRepository.getContentsByUserId(userId)
            onResult(contents)
        }
    }
}

class DiaryViewModelFactory(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiaryViewModel(userRepository, contentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
