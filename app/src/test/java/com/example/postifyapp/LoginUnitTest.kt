package com.example.postifyapp

import com.example.postifyapp.repository.UserRepo
import com.example.postifyapp.viewmodel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class LoginUnitTest {

    @Test
    fun login_success_test() {
        // 1. Setup Mocks
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        val testEmail = "test@gmail.com"
        val testPass = "123456"

        // 2. Mock the Auth Login call
        // Note: Login usually takes (Boolean, String) -> Unit
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Login success")
            null
        }.`when`(repo).login(eq(testEmail), eq(testPass), any())

        // 3. Execution
        var successResult = false
        var messageResult = ""

        viewModel.login(testEmail, testPass) { success, msg ->
            successResult = success
            messageResult = msg
        }

        // 4. Assertions
        assertTrue(successResult)
        assertEquals("Login success", messageResult)

        // 5. Verify the repo was actually called
        verify(repo).login(eq(testEmail), eq(testPass), any())
    }

    @Test
    fun login_fail_test() {
        // 1. Setup
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        val testEmail = "wrong@gmail.com"
        val testPass = "wrongpass"

        // 2. Mock a failed Login
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Invalid credentials")
            null
        }.`when`(repo).login(eq(testEmail), eq(testPass), any())

        // 3. Execution
        var successResult = true // Start with true to ensure it changes to false
        var messageResult = ""

        viewModel.login(testEmail, testPass) { success, msg ->
            successResult = success
            messageResult = msg
        }

        // 4. Assertions
        assertTrue(!successResult)
        assertEquals("Invalid credentials", messageResult)
    }
}