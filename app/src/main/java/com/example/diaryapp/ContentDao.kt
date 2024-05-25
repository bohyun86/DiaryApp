package com.example.diaryapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ContentDao {
    @Insert
    suspend fun insert(content: Content)

    @Query("SELECT * FROM content WHERE userId = :userId ORDER BY date DESC")
    suspend fun getContentsByUserId(userId: String): List<Content>

    @Query("SELECT * FROM content WHERE contentId = :contentId")
    suspend fun getContentsByContentId(contentId: Int): Content

    @Query("DELETE FROM content WHERE userId = :userId AND contentId = :contentId")
    suspend fun deleteContent(userId: String, contentId: Int)

    @Update
    suspend fun updateContent(content: Content)

    @Query("DELETE FROM content WHERE contentId = :contentId")
    suspend fun deleteContent(contentId: Int)
}

