package com.example.postifyapp.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.postifyapp.model.PostModel
import com.example.postifyapp.repository.PostRepo

class PostViewModel (val repo: PostRepo) : ViewModel(){

    fun addPost(model: PostModel, callback: (Boolean, String) -> Unit) {
        repo.addPost(model,callback)
    }

    fun updatePost(model: PostModel, callback: (Boolean, String) -> Unit) {
        repo.updatePost(model,callback)
    }

    fun deletePost(id: String, callback: (Boolean, String) -> Unit){
        repo.deletePost(id,callback)
    }

    private val _posts = MutableLiveData<PostModel?>()
    val posts : MutableLiveData<PostModel?> get() = _posts

    private val _allPosts = MutableLiveData<List<PostModel>?>()
    val allPosts : MutableLiveData<List<PostModel>?> get() = _allPosts

    private val _loading = MutableLiveData<Boolean>()
    val loading : MutableLiveData<Boolean> get() = _loading

    fun getAllPost() {
        _loading.postValue(true)
        repo.getAllPost  {
                sucess,message,data->
            if(sucess){
                _loading.postValue(false)
                _allPosts.postValue(data)
            }
        }
    }

    fun getPostById(id :String) {
        repo.getPostById(id) {
                sucess,message,data->
            if(sucess){
                Log.d("checkpoint",data!!.id)
                _posts.postValue(data)
            }
        }
    }

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        repo.uploadImage(context,imageUri,callback)
    }

    fun toggleLike(postId: String, currentUserId: String) {
        val post = allPosts.value?.find { it.id == postId } ?: return
        val alreadyLiked = post.likedBy.contains(currentUserId)

        repo.updatePostLikes(postId, currentUserId, !alreadyLiked) { success, msg ->
            if (success) {
                getAllPost()
            }
        }
    }

    fun postComment(postId: String, text: String) {
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (currentUser == null) return

        val userName = if (!currentUser.displayName.isNullOrEmpty()) {
            currentUser.displayName!!
        } else {
            currentUser.email?.substringBefore("@") ?: "User"
        }

        val comment = PostModel.CommentModel(
            userId = currentUser.uid,
            userName = userName,
            text = text,
            timestamp = System.currentTimeMillis()
        )

        repo.addComment(postId, comment) { success, msg ->
            if (success) {
                getAllPost()
            }
        }
    }

    fun updateComment(postId: String, commentId: String, newText: String) {
        repo.editComment(postId, commentId, newText) { success, msg ->
            if (success) getAllPost()
        }
    }

    fun deleteComment(postId: String, commentId: String) {
        repo.deleteComment(postId, commentId) { success, msg ->
            if (success) {
                getAllPost()
            }
        }
    }
}