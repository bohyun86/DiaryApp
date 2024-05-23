package com.example.diaryapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM user WHERE userId = :userId")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM user WHERE userEmail = :userEmail")
    suspend fun getUserByEmail(userEmail: String): User?

    @Query("UPDATE user SET userPw = :userPw WHERE userId = :userId")
    suspend fun updateUserPassword(userId: String, userPw: String)

    @Update
    suspend fun updateUser(user: User)
}