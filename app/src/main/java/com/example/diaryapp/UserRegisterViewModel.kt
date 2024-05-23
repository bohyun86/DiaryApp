package com.example.diaryapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider

class UserRegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    var id by mutableStateOf("")
    var pw by mutableStateOf("")
    var pw2 by mutableStateOf("")
    var email by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    var currentUser: User? = null

    fun onIdChange(newId: String) {
        id = newId
    }

    fun onPwChange(newPw: String) {
        pw = newPw
    }

    fun onPw2Change(newPw2: String) {
        pw2 = newPw2
    }

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun login(userId: String, userPw: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.login(userId, userPw)
            onResult(result)
            if (!result) {
                errorMessage = when {
                    userId.isEmpty() -> "아이디를 입력해주세요."
                    userPw.isEmpty() -> "비밀번호를 입력해주세요."
                    else -> "아이디 또는 비밀번호가 올바르지 않습니다."
                }
            }
        }
    }

    fun getUserByUserEmail(userEmail: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getUserByEmail(userEmail)
            onResult(user)
        }
    }

    fun isExitingUserEmail(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)
            val isTrue = user != null
            if (isTrue) {
                currentUser = user
            } else {
                errorMessage = "등록되지 않은 이메일입니다."
            }
            onResult(isTrue)
        }
    }

    fun registerUser(onResult: (Boolean) -> Unit) {
        if (pwEqualPw2() && validateId() && validatePw() && validateEmail()) {
            viewModelScope.launch {
                try {
                    val newUser = User(id, pw, email)
                    userRepository.insertUser(newUser)
                    currentUser = newUser
                    onResult(true)
                    Log.d("registerUser", "User registration successful")
                } catch (e: Exception) {
                    Log.e("registerUser", "Error during user registration: ${e.message}")
                    onResult(false)
                }
            }
        } else {
            errorMessage = ""
            when {
                !validateId() -> errorMessage = "아이디는 8자 이상 15자 이하, !@#$ 특수문자를 포함한 문자숫자만 가능합니다.\n"
                !validatePw() -> errorMessage = "비밀번호는 6자 이상 15자 이하, !@#$ 특수문자를 포함한 문자숫자만 가능합니다.\n"
                !pwEqualPw2() -> errorMessage = "비밀번호가 일치하지 않습니다.\n"
                !validateEmail() -> errorMessage = "이메일 형식이 올바르지 않습니다.\n"
            }
            onResult(false)
        }
    }

    fun updateUser(onResult: (Boolean) -> Unit) {
        if (pwEqualPw2() && validatePw()) {
            viewModelScope.launch {
                currentUser?.let {
                    userRepository.updateUser(it)
                    onResult(true)
                } ?: run {
                    onResult(false)
                }
            }
        } else {
            errorMessage = ""
            when {
                !validatePw() -> errorMessage += "비밀번호는 6자 이상 15자 이하, !@#$ 특수문자를 포함한 문자숫자만 가능합니다.\n"
                !pwEqualPw2() -> errorMessage += "비밀번호가 일치하지 않습니다.\n"
            }
            onResult(false)
        }
    }

    fun validateId(): Boolean {
        val regex = Regex("^[a-zA-Z0-9!@#$]{8,15}")
        return regex.matches(id)
    }

    fun validatePw(): Boolean {
        val regex = Regex("^[a-zA-Z0-9!@#$]{6,15}")
        return regex.matches(pw)
    }

    fun pwEqualPw2(): Boolean {
        return pw == pw2
    }

    fun validateEmail(): Boolean {
        val regex = Regex(".*@.*")
        return regex.matches(email)
    }
}

class UserRegisterViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserRegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserRegisterViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}