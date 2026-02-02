package com.example.postifyapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.postifyapp.view.LoginActivity
import com.example.postifyapp.view.RegistrationActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<RegistrationActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testSuccessfulRegistrationNavigation() {
        // 1. Enter Form Data with explicit focus
        composeRule.onNodeWithTag("regEmail").performClick().performTextInput("testuser2@gmail.com")
        composeRule.onNodeWithTag("regUsername").performClick().performTextInput("testuser")

        // Ensure we are using the same password for both to pass the 'when' validation
        val testPass = "Password123"
        composeRule.onNodeWithTag("regPassword").performClick().performTextInput(testPass)
        composeRule.onNodeWithTag("regConfirmPassword").performClick().performTextInput(testPass)

        // 2. Click Checkbox (Crucial: the 'else' block won't run without this)
        composeRule.onNodeWithTag("regTerms").performClick()

        // 3. Click Sign Up
        composeRule.onNodeWithTag("regSignUpButton").performClick()

        // 4. Wait for Firebase & Database (Firebase Auth + Realtime DB is slow)
        // Using Thread.sleep here is okay for simple tests, but 5s is safer
        Thread.sleep(5000)

        // 5. Verify it navigated back to LoginActivity
        intended(hasComponent(LoginActivity::class.java.name))
    }
}