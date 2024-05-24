package com.example.diaryapp

class ContentRepository(private val contentDao: ContentDao) {
    suspend fun insertContent(content: Content) {
        contentDao.insert(content)
    }

    suspend fun getContentsByUserId(userId: String): List<Content> {
        return contentDao.getContentsByUserId(userId)
    }

    suspend fun deleteContent(userId: String, contentId: Int) {
        contentDao.deleteContent(userId, contentId)
    }

    suspend fun updateContent(content: Content) {
        contentDao.updateContent(content)
    }

    suspend fun deleteContent(contentId: Int) {
        contentDao.deleteContent(contentId)
    }


    suspend fun getContentByIdAndUserId(userId: String, contentId: Int): Content {
        return contentDao.getContentsByUserIdAndContentId(userId, contentId)
    }
}
