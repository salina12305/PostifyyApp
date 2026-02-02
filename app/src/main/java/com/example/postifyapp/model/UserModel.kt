package com.example.postifyapp.model

import kotlin.String

/**
 * UserModel: Represents the user profile data stored in Firebase Realtime Database.
 * Default values are provided to ensure a no-argument constructor for Firebase.
 */
data class UserModel(
    var userId: String = "",
    var email: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var dob: String = "",
    var contact: String = ""
) {
    /**
     * Converts the user object into a Map for Firebase operations.
     * This is useful for initial registration and profile updates.
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "contact" to contact,
        )
    }
}
