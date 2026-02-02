package com.example.postifyapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.postifyapp.view.DashboardActivity
import com.example.postifyapp.view.LoginActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    // Logic: Use LoginActivity as the starting point for this test
    @get:Rule
    val composeRule = createAndroidComposeRule<LoginActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testLoginSuccessNavigation() {
        // 1. Wait for the activity to settle
        composeRule.waitUntil(10000) { // Increased to 10s due to the frame skips in logs
            composeRule.onAllNodesWithTag("emailInput").fetchSemanticsNodes().isNotEmpty()
        }

        // 2. Input Email and close keyboard to prevent overlap
        composeRule.onNodeWithTag("emailInput")
            .performClick()
            .performTextInput("testuser2@gmail.com")

        // 3. Input Password
        composeRule.onNodeWithTag("passwordInput")
            .performClick()
            .performTextInput("Password123")

        // 4. Click the button - use performScrollTo() because
        // the logs show the keyboard is active and might be blocking the view
        composeRule.onNodeWithTag("loginButton")
            .performScrollTo()
            .performClick()

        // 5. Firebase takes time to verify credentials over the network
        Thread.sleep(5000)

        // 6. Verify Intent
        intended(hasComponent(DashboardActivity::class.java.name))
    }
}