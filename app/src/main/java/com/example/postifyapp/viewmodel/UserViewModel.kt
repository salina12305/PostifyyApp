package com.example.postifyapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.postifyapp.model.UserModel
import com.example.postifyapp.repository.UserRepo
import com.google.firebase.auth.FirebaseUser

/**
 * UserViewModel: Manages user-related state and authentication logic.
 * Acts as the intermediary between the Compose UI and the UserRepo.
 */
class UserViewModel(val repo: UserRepo) : ViewModel() {

    // --- Profile State Management ---
    private val _users = MutableLiveData<UserModel?>()
    val users: MutableLiveData<UserModel?>
        get() = _users

    private val _allUsers = MutableLiveData<List<UserModel>?>()
    val allUsers: MutableLiveData<List<UserModel>?>
        get() = _allUsers

    // --- Authentication Actions ---
    fun login(
        email: String, password: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.login(email, password, callback)
    }

    fun register(
        email: String, password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        repo.register(email, password, callback)
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        repo.forgetPassword(email, callback)
    }

    
    fun addUserToDatabase(
        userId: String, model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addUserToDatabase(userId, model, callback)
    }

    /**
     * Fetch user profile details by ID and update the observable LiveData.
     */
    fun getUserById(userId: String) {
        repo.getUserById(userId) { success, user ->
            if (success) {
                _users.postValue(user)
            }
        }
    }

    fun getAllUser() {
        repo.getAllUser { success, data ->
            if (success) {
                _allUsers.postValue(data)
            }
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }

    fun deleteUser(userId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteUser(userId, callback)
    }

    fun updateProfile(
        userId: String, model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.updateProfile(userId, model, callback)
    }
}