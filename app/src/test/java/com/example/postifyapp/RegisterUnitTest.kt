package com.example.postifyapp


import com.example.postifyapp.model.UserModel
import com.example.postifyapp.repository.UserRepo
import com.example.postifyapp.viewmodel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class RegisterUnitTest {

    @Test
    fun register_success_test() {
        // 1. Setup Mocks
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        val testEmail = "test@example.com"
        val testPass = "password123"
        val testUserId = "user_abc_123"

        // 2. Mock the Auth Registration call
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, String) -> Unit>(2)
            callback(true, "Registration Successful!", testUserId)
            null
        }.`when`(repo).register(eq(testEmail), eq(testPass), any())

        // 3. Execution & Verification
        var successResult = false
        var messageResult = ""
        var userIdResult = ""

        viewModel.register(testEmail, testPass) { success, msg, userId ->
            successResult = success
            messageResult = msg
            userIdResult = userId
        }

        // 4. Assertions
        assertTrue(successResult)
        assertEquals("Registration Successful!", messageResult)
        assertEquals(testUserId, userIdResult)

        verify(repo).register(eq(testEmail), eq(testPass), any())
    }

    @Test
    fun add_user_to_database_success_test() {
        // 1. Setup
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)
        val testId = "12345"
        val userModel = UserModel(userId = testId, email = "test@test.com")

        // 2. Mock Database call
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Data Saved")
            null
        }.`when`(repo).addUserToDatabase(eq(testId), any(), any())

        // 3. Execution
        var dbSuccess = false
        viewModel.addUserToDatabase(testId, userModel) { success, _ ->
            dbSuccess = success
        }

        // 4. Assertions
        assertTrue(dbSuccess)
        verify(repo).addUserToDatabase(eq(testId), eq(userModel), any())
    }
}