package com.example.diaryapp

class ContentRepository(private val contentDao: ContentDao) {
    suspend fun insertContent(content: Content) {
        contentDao.insert(content)
    }

    suspend fun getContentsByUserId(userId: String): List<Content> {
        return contentDao.getContentsByUserId(userId)
    }


    suspend fun updateContent(content: Content) {
        contentDao.updateContent(content)
    }

    suspend fun deleteContent(contentId: Int) {
        contentDao.deleteContent(contentId)
    }


    suspend fun getContentsById(contentId: Int): Content {
        return contentDao.getContentsByContentId(contentId)
    }
}
