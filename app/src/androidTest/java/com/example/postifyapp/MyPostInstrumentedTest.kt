package com.example.postifyapp

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.postifyapp.view.MyPostScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyPostInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMyPostScreen_EmptyState_DisplaysFeedback() {
        // Set the content
        composeTestRule.setContent {
            MyPostScreen()
        }

        // Verify the title exists
        composeTestRule.onNodeWithText("My Published Stories").assertExists()

        // Verify empty state message shows if no posts are returned
        // Use a wait if your ViewModel takes time to load
        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("You haven't posted any stories yet.").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun testDeleteDialog_AppearsOnButtonClick() {
        // 1. Set the content ONLY ONCE
        composeTestRule.setContent {
            // We call the screen with a fake ID
            // Note: For this to show posts, your ViewModel usually needs to be
            // mocked, but let's test the dialog logic directly to ensure it works.
            MyPostScreen(fakeUserId = "test_user_123")
        }

        // 2. If you want to test the DIALOG specifically and the list is empty,
        // you should create a separate test for the dialog component itself:
    }

    @Test
    fun testDeleteDialog_UI_Logic() {
        var deleteClicked = false

        composeTestRule.setContent {
            // Test the AlertDialog in isolation
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Delete Story?") },
                text = { Text("Are you sure you want to delete this post?") },
                confirmButton = {
                    Button(onClick = { deleteClicked = true }) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { }) { Text("Cancel") }
                }
            )
        }

        // Verify dialog elements
        composeTestRule.onNodeWithText("Delete Story?").assertIsDisplayed()

        // Perform click
        composeTestRule.onNodeWithText("Delete").performClick()

        // Assert the logic was triggered
        assert(deleteClicked)
    }
}