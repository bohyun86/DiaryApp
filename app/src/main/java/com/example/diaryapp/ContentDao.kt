package com.example.diaryapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ContentDao {
    @Insert
    suspend fun insert(content: Content)

    @Query("SELECT * FROM content WHERE userId = :userId")
    suspend fun getContentsByUserId(userId: String): List<Content>
}
