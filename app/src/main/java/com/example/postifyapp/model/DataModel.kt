package com.example.postifyapp.model

import android.R.attr.title
import kotlin.String

/**
 * DataModel: Represents a post entity.
 * Includes a toMap function to simplify writing data to Firebase Realtime Database.
 */
data class DataModel(
    val id: Int,
    val author: String,
    val title: String,
    val snippet: String,
    val date: String,
    val imageResId: Int
)
// Converts the object to a Map for Firebase Realtime Database
fun toMap(): Map<String, Any?> {
    return mapOf(
        "title" to title,
    )
}

