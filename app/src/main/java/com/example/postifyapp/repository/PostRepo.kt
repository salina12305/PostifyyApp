package com.example.postifyapp.repository

import android.content.Context
import android.net.Uri
import com.example.postifyapp.model.PostModel

interface PostRepo {
    fun addPost(model: PostModel,
                callback: (Boolean, String)-> Unit)

    fun updatePost(model: PostModel,
                   callback: (Boolean, String) -> Unit)

    fun deletePost(productId: String,
                   callback: (Boolean, String) -> Unit)

    fun getAllPost(callback: (Boolean, String, List<PostModel>?) -> Unit)

    fun getPostById(productId: String,
                    callback: (Boolean, String, PostModel?) -> Unit)

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?)-> Unit)

    fun getFileNameFromUri(context: Context, uri: Uri):String?

    fun updatePostLikes(postId: String, userId: String, isLiked: Boolean,
                        callback: (Boolean, String) -> Unit)

    fun addComment(
        postId: String, comment: PostModel.CommentModel,
        callback: (Boolean, String) -> Unit)
}
