package com.example.diaryapp

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
    var currentUser = User(id, pw, email)

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

    fun getContentsByUserEmail(userEmail: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val users = userRepository.getUserByEmail(userEmail)
            onResult(users)
        }
    }

    fun isExitingUserEmail(email: String): Boolean {
        var isTrue = false
        getContentsByUserEmail(email) { user ->
            if (user != null) {
                isTrue = true
                currentUser = user
            } else {
                errorMessage = "등록되지 않은 이메일입니다."
                isTrue = false
            }
        }
        return isTrue
    }
    fun registerUser(): Boolean {
        return if (pwEqualPw2() && validateId() && validatePw() && validateEmail()) {

            viewModelScope.launch {
                userRepository.insertUser(currentUser)
            }
            true

        } else {
            when {
                !validateId() -> errorMessage += "아이디는 8자 이상 15자 이하, !@#$ 특수문자를 포함한 문자숫자만 가능합니다.\n"
                !validatePw() -> errorMessage += "비밀번호는 6자 이상 15자 이하, !@#$ 특수문자를 포함한 문자숫자만 가능합니다.\n"
                !pwEqualPw2() -> errorMessage += "비밀번호가 일치하지 않습니다.\n"
                !validateEmail() -> errorMessage += "이메일 형식이 올바르지 않습니다.\n"
            }
            false
        }
    }

    fun updateUser() {
        if (pwEqualPw2() && validatePw() ) {
            viewModelScope.launch {
                userRepository.updateUser(currentUser)
            }
        } else {
            when {
                !validatePw() -> errorMessage += "비밀번호는 6자 이상 15자 이하, !@#$ 특수문자를 포함한 문자숫자만 가능합니다.\n"
                !pwEqualPw2() -> errorMessage += "비밀번호가 일치하지 않습니다.\n"
            }
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