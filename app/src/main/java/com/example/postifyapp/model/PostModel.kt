package com.example.postifyapp.model

data class PostModel(

    var id: String = "",
    val userId: String = "",
    var author: String = "",
    var title: String = "",
    var snippet: String = "",
    var image: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likedBy: List<String> = emptyList()
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
            "likes" to likedBy,
        )
    }
}

