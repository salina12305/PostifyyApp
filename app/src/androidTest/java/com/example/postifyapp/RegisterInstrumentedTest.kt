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
        // 1. Enter Form Data
        composeRule.onNodeWithTag("regEmail").performTextInput("testuser1@gmail.com")
        composeRule.onNodeWithTag("regUsername").performTextInput("testuser")
        composeRule.onNodeWithTag("regPassword").performTextInput("Password123")
        composeRule.onNodeWithTag("regConfirmPassword").performTextInput("Password123")

        // 2. Click Checkbox
        composeRule.onNodeWithTag("regTerms").performClick()

        // 3. Click Sign Up
        composeRule.onNodeWithTag("regSignUpButton").performClick()

        // 4. Wait for Firebase/Network (3 seconds)
        Thread.sleep(3000)

        // 5. Verify it navigated back to LoginActivity
        intended(hasComponent(LoginActivity::class.java.name))
    }
}