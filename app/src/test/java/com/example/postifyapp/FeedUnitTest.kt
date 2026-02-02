package com.example.postifyapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.postifyapp.model.PostModel
import com.example.postifyapp.repository.PostRepo
import com.example.postifyapp.viewmodel.PostViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class FeedUnitTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    @Test
    fun toggle_like_success_test() {
        // 1. Setup
        val repo = mock<PostRepo>()
        val viewModel = PostViewModel(repo)
        val testPostId = "post_123"
        val testUserId = "user_abc"

        // Create a fake list so viewModel.allPosts.value isn't null
        val fakePosts = listOf(
            PostModel(id = testPostId, title = "Test", likedBy = mutableListOf())
        )
        viewModel.allPosts.value = fakePosts

        // 2. Mock the Repo call (Note: it's updatePostLikes in your repo)
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(3)
            callback(true, "Liked")
            null
        }.`when`(repo).updatePostLikes(eq(testPostId), eq(testUserId), any(), any())

        // 3. Execution
        viewModel.toggleLike(testPostId, testUserId)

        // 4. Verify (Match the function name in your Repo)
        verify(repo).updatePostLikes(eq(testPostId), eq(testUserId), eq(true), any())
    }

    @Test
    fun delete_post_success_test() {
        val repo = mock<PostRepo>()
        val viewModel = PostViewModel(repo)
        val testPostId = "post_to_delete"

        // Mocking repo.deletePost
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Post Deleted")
            null
        }.`when`(repo).deletePost(eq(testPostId), any())

        var deleteSuccess = false
        var deleteMsg = ""

        viewModel.deletePost(testPostId) { success, msg ->
            deleteSuccess = success
            deleteMsg = msg
        }

        assertTrue(deleteSuccess)
        assertEquals("Post Deleted", deleteMsg)
        verify(repo).deletePost(eq(testPostId), any())
    }
}