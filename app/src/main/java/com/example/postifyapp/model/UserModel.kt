package com.example.postifyapp.model

import kotlin.String
data class UserModel(
    var userId: String = "",
    var email: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var dob: String = "",
    var contact: String = ""
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "contact" to contact,
        )
    }
}
