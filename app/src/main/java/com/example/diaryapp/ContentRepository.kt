package com.example.diaryapp

class ContentRepository(private val contentDao: ContentDao) {
    suspend fun insertContent(content: Content) {
        contentDao.insert(content)
    }

    suspend fun getContentsByUserId(userId: String): List<Content> {
        return contentDao.getContentsByUserId(userId)
    }
}
