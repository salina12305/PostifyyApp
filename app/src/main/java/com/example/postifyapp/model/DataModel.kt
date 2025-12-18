package com.example.postifyapp.model

import android.R.attr.title
import kotlin.String
// --- Data Models ---
data class DataModel(
    val id: Int,
    val author: String,
    val title: String,
    val snippet: String,
    val date: String,
    val imageResId: Int
)
fun toMap(): Map<String, Any?> {
    return mapOf(
        "title" to title,

    )
}

