package com.example.postifyapp.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.postifyapp.model.PostModel
import com.example.postifyapp.repository.PostRepo

/**
 * PostViewModel: Orchestrates the state of the Feed and individual Posts.
 * It observes data from the Repository and exposes it as LiveData for the UI.
 */
class PostViewModel (val repo: PostRepo) : ViewModel(){

    // --- State Observables ---
    private val _posts = MutableLiveData<PostModel?>()
    val posts : MutableLiveData<PostModel?> get() = _posts

    private val _allPosts = MutableLiveData<List<PostModel>?>()
    val allPosts : MutableLiveData<List<PostModel>?> get() = _allPosts

    private val _loading = MutableLiveData<Boolean>()
    val loading : MutableLiveData<Boolean> get() = _loading

    // --- CRUD Operations ---
    fun addPost(model: PostModel, callback: (Boolean, String) -> Unit) {
        repo.addPost(model,callback)
    }

    fun updatePost(model: PostModel, callback: (Boolean, String) -> Unit) {
        repo.updatePost(model,callback)
    }

    fun deletePost(id: String, callback: (Boolean, String) -> Unit){
        repo.deletePost(id,callback)
    }

    /**
     * getAllPost: Triggers the repository to fetch data from Firebase.
     * Updates _allPosts when the data arrives.
     */
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

    /**
     * getPostById: Fetches details for a specific post.
     * Essential for the "Update Post" dialog logic.
     */
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
    // --- Social Engagement Logic ---
    /**
     * toggleLike: Logic for adding/removing a user's ID from the liked list.
     * It checks the current state locally before sending the toggle request to the Repo.
     */
    fun toggleLike(postId: String, currentUserId: String) {
        val post = allPosts.value?.find { it.id == postId } ?: return
        val alreadyLiked = post.likedBy.contains(currentUserId)

        repo.updatePostLikes(postId, currentUserId, !alreadyLiked) { success, msg ->
            if (success) {
                getAllPost()
            }
        }
    }

    /**
     * postComment: Formats a CommentModel using current user info and sends it to the Repo.
     */
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