package com.example.postifyapp.repository

import com.example.postifyapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * UserRepoImpl: Handles the actual interaction with Firebase Auth and Database.
 */
class UserRepoImpl: UserRepo {
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref : DatabaseReference = database.getReference("Users")

    // --- Authentication ---
    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    callback(true,"Login success")
                }else{
                    callback(false,"${it.exception?.message}")

                }
            }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "A reset link has been sent to $email. Please check your inbox.")
                } else {
                    callback(false, task.exception?.message ?: "Failed to send reset email")
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        // Step 1: Create the entry in Firebase Auth
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    // Pass back the unique UID to be used for the database record
                    callback(true,"Registration success","${auth.currentUser?.uid}")
                }else{
                    callback(false,"${it.exception?.message}","")
                }
            }
    }

    // --- Database Operations ---
    override fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        // Step 2: Save detailed profile info using the UID as the key
        ref.child(userId).setValue(model).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"User data saved successfully")
            }else{
                callback(false,"${it.exception?.message}")
            }
        }
    }

    override fun getUserById(
        userId: String,
        callback: (Boolean, UserModel?) -> Unit
    ) {
        // Real-time listener: UI will update automatically if the profile changes in the cloud
        ref.child(userId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user= snapshot.getValue(UserModel::class.java)
                    if (user!=null){
                        callback(true,user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, null)
            }
        })
    }

    override fun getAllUser(callback: (Boolean, List<UserModel>?) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var allUsers = mutableListOf<UserModel>()
                    for (user in snapshot.children){
                        val model= user.getValue(UserModel::class.java)
                        if (model !=null){
                            allUsers.add(model)
                        }
                    }
                    callback(true,allUsers)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false,emptyList())
            }
        })
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun deleteUser(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                callback(true,"User deleted successfully")
            }else{
                callback(false,"${it.exception?.message}")
            }
        }
    }

    override fun updateProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        // use updateChildren and the toMap() helper to avoid overwriting the whole object
        ref.child(userId).updateChildren(model.toMap()).addOnCompleteListener {
            if (it.isSuccessful){
                callback(true,"Profile updated successfully")
            }else{
                callback(false,"${it.exception?.message}")
            }
        }
    }
}