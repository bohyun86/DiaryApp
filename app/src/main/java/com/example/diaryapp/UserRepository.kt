package com.example.diaryapp

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User) {
        userDao.insert(user)
    }

    suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)
    }
}

