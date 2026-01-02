package com.example.postifyapp.model

data class PostModel(

    var id: String = "",
    val userId: String = "",
    var author: String = "",
    var title: String = "",
    var snippet: String = "",
    var image: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likedBy: List<String> = emptyList(),
    val comments: Map<String, CommentModel> = emptyMap(),

){
    fun toMap() : Map<String,Any?>{
        return mapOf(
            "id" to id,
            "userId" to userId,
            "author" to author,
            "title" to title,
            "snippet" to snippet,
            "image" to image,
            "timestamp" to timestamp,
            "likedby" to likedBy,
            "comments" to comments,
        )
    }
    data class CommentModel(
        val commentId: String = "",
        val userId: String = "",
        val userName: String = "",
        val text: String = "",
        val timestamp: Long = System.currentTimeMillis()
    )
}

