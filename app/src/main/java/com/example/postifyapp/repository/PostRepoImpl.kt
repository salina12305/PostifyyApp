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

class PostRepoImpl: PostRepo {

    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref : DatabaseReference = database.getReference("posts")

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
        var id = ref.push().key.toString()
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
        ref.child(model.id).updateChildren(model.toMap()).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"post updated")
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
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var allPosts = mutableListOf<PostModel>()
                    for (data in snapshot.children) {
                        val post = data.getValue(PostModel::class.java)
                        if (post != null) {
                            allPosts.add(post)
                        }
                    }
                    callback(true, "post fetched", allPosts)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
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

                var imageUrl = response["url"] as String?

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

//    override fun addPostByImage(
//        categoryId: String,
//        callback: (Boolean, String, List<PostModel>) -> Unit
//    ) {
//        ref.orderByChild("categoryId").equalTo(categoryId)
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val categoryPosts = mutableListOf<PostModel>()
//                    if (snapshot.exists()) {
//                        for (data in snapshot.children) {
//                            val post = data.getValue(PostModel::class.java)
//                            if (post != null) {
//                                categoryPosts.add(post)
//                            }
//                        }
//                        callback(true, "Posts for category $categoryId fetched", categoryPosts)
//                    } else {
//                        callback(true, "No posts found for this category", emptyList())
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    callback(false, error.message, emptyList())
//                }
//            })
//    }
}