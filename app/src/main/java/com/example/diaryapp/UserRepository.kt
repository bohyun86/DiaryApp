package com.example.diaryapp

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User) {
        userDao.insert(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun getUserByEmail(userEmail: String): User? {
        return userDao.getUserByEmail(userEmail)
    }

    suspend fun login(userId: String): User? {
        return userDao.getUserById(userId)
    }
}


