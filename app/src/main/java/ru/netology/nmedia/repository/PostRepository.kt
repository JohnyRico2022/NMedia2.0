package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<PagingData<FeedItem>>
    suspend fun getUnreadPosts()
    suspend fun makePostShowed()
    suspend fun likeById(id: Long)
    suspend fun disLikeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post:Post, upload:MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun singIn(login: String, pass: String)
    suspend fun signUp(name: String, login: String, pass: String)
}



