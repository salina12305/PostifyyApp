////package com.example.postifyapp.viewmodel
////
////import androidx.lifecycle.ViewModel
////import com.example.postifyapp.model.PostModel
////import com.example.postifyapp.repository.PostRepo
////import com.example.postifyapp.repository.PostRepoImpl
////import kotlinx.coroutines.flow.MutableStateFlow
////import kotlinx.coroutines.flow.StateFlow
////
////class PostViewModel : ViewModel() {
////
////    private val repo: PostRepo = PostRepoImpl()
////
////    private val _posts = MutableStateFlow<List<PostModel>>(emptyList())
////    val posts: StateFlow<List<PostModel>> = _posts
////
////    init {
////        loadPosts()
////    }
////
////    private fun loadPosts() {
////        repo.getPosts {
////            _posts.value = it
////        }
////    }
////
////    fun addPost(author: String, title: String, content: String, imageResId: Int) {
////        val post = PostModel(
////            id = System.currentTimeMillis().toString(),
////            author = author.ifBlank { "Anonymous" },
////            title = title,
////            content = content,
////            date = System.currentTimeMillis(),
////            imageResId = imageResId
////        )
////        repo.addPost(post)
////    }
////
////    fun updatePost(post: PostModel) {
////        repo.updatePost(post)
////    }
////
////    fun deletePost(postId: String) {
////        repo.deletePost(postId)
////    }
////}
//
//package com.example.postifyapp.viewmodel
//
//import androidx.lifecycle.ViewModel
//import com.example.postifyapp.model.PostModel
//import com.example.postifyapp.repository.PostRepo
//import com.example.postifyapp.repository.PostRepoImpl
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import java.text.SimpleDateFormat
//import java.util.*
//
////class PostViewModel(private val repository: PostRepo = PostRepoImpl()) : ViewModel() {
////
////    private val _posts = MutableStateFlow<List<PostModel>>(emptyList())
////    val posts: StateFlow<List<PostModel>> = _posts
////
////    init {
////        fetchPosts()
////    }
////
////    private fun fetchPosts() {
////        repository.getPosts { _posts.value = it }
////    }
////
//////    fun uploadPost(author: String, title: String, snippet: String, imageUrl: String) {
//////        val currentDate = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date())
//////        val newPost = PostModel(
//////            author = author,
//////            title = title,
//////            snippet = snippet,
//////            date = currentDate,
//////            imageUrl = imageUrl
//////        )
//////        repository.addPost(newPost) { success ->
//////            // You can add logic here to handle success or failure toast
//////        }
//////    }
////fun uploadPost(author: String, title: String, snippet: String, imageUrl: String) {
////    val currentDate = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date())
////    val newPost = PostModel(
////        author = author,
////        title = title,
////        snippet = snippet,
////        date = currentDate,
////        imageUrl = imageUrl
////    )
////
////    repository.addPost(newPost) { success ->
////        if (success) {
////            android.util.Log.d("FirebaseDebug", "Post added successfully!")
////        } else {
////            android.util.Log.e("FirebaseDebug", "Failed to add post. Check Rules or Internet.")
////        }
////    }
////}
////}
//
//class PostViewModel(private val repository: PostRepo = PostRepoImpl()) : ViewModel() {
//
//    private val _posts = MutableStateFlow<List<PostModel>>(emptyList())
//    val posts: StateFlow<List<PostModel>> = _posts
//
//    init {
//        fetchPosts()
//    }
//
//    private fun fetchPosts() {
//        repository.getPosts { _posts.value = it }
//    }
//
//    fun uploadPost(author: String, title: String, snippet: String, imageUrl: String) {
//        val currentDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
//        val newPost = PostModel(
//            author = author,
//            title = title,
//            snippet = snippet,
//            date = currentDate,
//            imageUrl = imageUrl
//        )
//        repository.addPost(newPost) { success ->
//            // This callback is handled in the UI
//        }
//    }
//}
package com.example.postifyapp.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.postifyapp.model.CommentModel
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

    fun getAllProduct() {
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

//    fun toggleLike(post: PostModel, userId: String) {
//        if (userId.isEmpty() || post.userId == userId) return
//
//        repo.toggleLike(post.id, userId) { success, message ->
//            // The getAllPost() listener in the repo will automatically
//            // update the 'allPosts' state when the database changes.
//        }
//    }
//    fun addComment(postId: String, userId: String, text: String, userName: String) {
//        val comment = CommentModel(userName = userName, commentText = text)
//        repo.addComment(postId, comment) { success, msg ->
//            // Handle response if needed
//        }
//    fun addComment(postId: String, text: String, userName: String, callback: (Boolean, String) -> Unit) {
//    if (text.isBlank()) {
//        callback(false, "Comment cannot be empty")
//        return
//    }
//
//    val comment = CommentModel(userName = userName, commentText = text)
//    repo.addComment(postId, comment) { success, msg ->
//        callback(success, msg)
//    }
//    }

}