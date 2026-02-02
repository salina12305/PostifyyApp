package com.example.postifyapp.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.postifyapp.model.PostModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.InputStream
import java.util.concurrent.Executors

/**
 * PostRepoImpl: The concrete implementation of our data layer.
 * This class handles the nitty-gritty of network calls and data parsing.
 */
class PostRepoImpl: PostRepo {
    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref : DatabaseReference = database.getReference("posts")

    // Cloudinary setup for external image hosting
    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dwfc51vqa",
            "api_key" to "433791988788857",
            "api_secret" to "u9Qgd5h0Y-hmyxPFO1hsp01swEI"
        )
    )

    override fun addPost(
        model: PostModel,
        callback: (Boolean, String) -> Unit
    ) {
        // Generate a unique ID from Firebase before saving
        val id = ref.push().key.toString()
        model.id = id

        ref.child(id).setValue(model).addOnCompleteListener {
            if (it.isSuccessful){
                callback(true,"Post added Successful")
            }else{
                callback(false,"${it.exception?.message}")
            }
        }
    }

    override fun updatePost(
        model: PostModel,
        callback: (Boolean, String) -> Unit
    ) {
        // Use updateChildren to only modify specific fields provided in the Map
        ref.child(model.id).updateChildren(model.toMap()).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Post updated")
            }else{
                callback(false,"${it.exception?.message}")

            }
        }
    }

    override fun deletePost(
        id: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(id).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"post deleted")
            }else{
                callback(false,"${it.exception?.message}")

            }
        }
    }

    override fun getAllPost(callback: (Boolean, String, List<PostModel>?) -> Unit) {
        // addValueEventListener provides real-time updates whenever data changes
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allPosts = mutableListOf<PostModel>()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        try {
                            val post = data.getValue(PostModel::class.java)
                            if (post != null) {
                                // Ensure the post object carries its Firebase Key as its ID
                                allPosts.add(post.copy(id = data.key ?: ""))
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("FirebaseError", "Post failed to parse: ${e.message}")
                        }
                    }
                    callback(true, "Posts fetched", allPosts)
                } else {
                    callback(true, "No posts found", emptyList())
                }
            }
            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun getPostById(
        id: String,
        callback: (Boolean, String, PostModel?) -> Unit
    ) {
        ref.child(id).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(PostModel::class.java)
                    if(data != null){
                        callback(true, "post fetched",data)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                callback(false,error.message,null)
            }
        })
    }

    override fun uploadImage(
        context: Context,
        imageUri: Uri,
        callback: (String?) -> Unit
    ) {
        // Network operations MUST NOT run on the Main Thread.
        // Using an Executor ensures the UI stays responsive during upload.
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)

                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )
                // Cloudinary often returns http; we force https for Android security (Cleartext traffic)
                var imageUrl = response["url"] as String?
                // Switch back to the Main Looper to trigger UI updates/Toasts
                imageUrl = imageUrl?.replace("http://", "https://")
                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    override fun getFileNameFromUri(
        context: Context,
        uri: Uri
    ): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    override fun updatePostLikes(postId: String, userId: String, isLiked: Boolean, callback: (Boolean, String) -> Unit) {
        val db = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("posts")
        val postRef = db.child(postId).child("likedBy")

        // Transactional logic: Fetch current list, modify it, then push back
        postRef.get().addOnSuccessListener { snapshot ->
            val currentLikes = mutableListOf<String>()
            snapshot.children.forEach { child ->
                child.value?.toString()?.let { currentLikes.add(it) }
            }
            if (isLiked) {
                if (!currentLikes.contains(userId)) currentLikes.add(userId)
            } else {
                currentLikes.remove(userId)
            }
            postRef.setValue(currentLikes)
                .addOnSuccessListener {
                    callback(true, "Success")
                }
                .addOnFailureListener { e ->
                    callback(false, e.message ?: "Error updating likes")
                }
        }.addOnFailureListener { e ->
            callback(false, e.message ?: "Error fetching data")
        }
    }
    override fun addComment(postId: String, comment: PostModel.CommentModel, callback: (Boolean, String) -> Unit) {
    val db = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("posts")
        // Nest comments under the specific post ID
    val commentRef = db.child(postId).child("comments").push()
    val commentWithId = comment.copy(commentId = commentRef.key ?: "")

    commentRef.setValue(commentWithId)
        .addOnSuccessListener {
            callback(true, "Comment added successfully")
        }
        .addOnFailureListener { e ->
            callback(false, e.message ?: "Failed to add comment")
        }
    }

    override fun editComment(
        postId: String,
        commentId: String,
        newText: String,
        callback: (Boolean, String) -> Unit
    ) {
        val commentTextRef = ref.child(postId)
            .child("comments")
            .child(commentId)
            .child("text")

        commentTextRef.setValue(newText)
            .addOnSuccessListener {
                callback(true, "Comment updated successfully")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to update comment")
            }
    }

    override fun deleteComment(
        postId: String,
        commentId: String,
        callback: (Boolean, String) -> Unit
    ) {
        val commentRef = ref.child(postId)
            .child("comments")
            .child(commentId)

        commentRef.removeValue()
            .addOnSuccessListener {
                callback(true, "Comment deleted successfully")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to delete comment")
            }
    }
}