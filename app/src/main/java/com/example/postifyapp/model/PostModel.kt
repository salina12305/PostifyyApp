package com.example.postifyapp.model

data class PostModel(

    var id: String = "",
    val userId: String = "",
    var author: String = "",
    var title: String = "",
    var snippet: String = "",
    var image: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likes: List<String> = emptyList(),
    val comments: List<CommentModel> = emptyList(),
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
            "likes" to likes,
            "comments" to comments.map { it.toMap() }
        )
    }
}

data class CommentModel(
    val userName: String = "",
    val commentText: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userName" to userName,
            "commentText" to commentText
        )
    }
}
