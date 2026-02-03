package com.example.postifyapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.postifyapp.model.PostModel
import com.example.postifyapp.view.FeedScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testFeedScreen_ContentDisplays() {
        composeTestRule.setContent {
            FeedScreen()
        }

        // Check if the main header is present
        composeTestRule.onNodeWithText("Trending Stories").assertIsDisplayed()
    }

    @Test
    fun testOpenFullPostView_OnClick() {
        composeTestRule.setContent {
            FeedScreen()
        }

        // 1. Wait for data to load
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithContentDescription("Post Image")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 2. CHANGE THIS: Use .onFirst() to pick one of the two images
        composeTestRule.onAllNodesWithContentDescription("Post Image")
            .onFirst()
            .performClick()

        // 3. Verify the BottomSheet opens by checking for the "Comments" text
        composeTestRule.onNodeWithText("Comments", substring = true).assertIsDisplayed()
    }

    @Test
    fun testDeleteDialog_Interaction() {
        val testUserId = "test_user_123"

        // Create a mock post that 'belongs' to our test user
        val mockPost = PostModel(
            id = "1",
            userId = testUserId, // Critical: must match fakeUserId
            author = "Test Author",
            title = "Test Story",
            snippet = "This is a test snippet content",
            image = ""
        )

        composeTestRule.setContent {
            // Inject the test state directly
            FeedScreen(
                fakeUserId = testUserId,
                fakePosts = listOf(mockPost)
            )
        }

        // Now the test will find exactly 1 node because we provided exactly 1 post
        composeTestRule.onNodeWithContentDescription("Delete")
            .assertExists()
            .performClick()

        // Verify Alert Dialog Title
        composeTestRule.onNodeWithText("Delete Post?").assertIsDisplayed()

        // Verify the Cancel button works
        composeTestRule.onNodeWithText("Cancel").performClick()

        // Assert the dialog is gone
        composeTestRule.onNodeWithText("Delete Post?").assertDoesNotExist()
    }
}