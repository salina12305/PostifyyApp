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

class MyPostUnitTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun fetch_my_posts_logic_test() {
        // 1. Setup
        val repo = mock<PostRepo>()
        val viewModel = PostViewModel(repo)

        val currentUserId = "user_123"
        val mockPosts = listOf(
            PostModel(id = "p1", userId = "user_123", title = "My Post"),
            PostModel(id = "p2", userId = "user_456", title = "Other Post")
        )

        // 2. Mock getAllPost to return a mix of posts
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String?, List<PostModel>?) -> Unit>(0)
            callback(true, "Success", mockPosts)
            null
        }.`when`(repo).getAllPost(any())

        // 3. Execution
        viewModel.getAllPost()

        // 4. Verification of logic (Filtering)
        // In your MyPostScreen, you do: allPosts?.filter { it.userId == currentUserId }
        val allFetched = viewModel.allPosts.value
        val filtered = allFetched?.filter { it.userId == currentUserId }

        assertEquals(2, allFetched?.size) // Total posts
        assertEquals(1, filtered?.size)   // Only my posts
        assertEquals("My Post", filtered?.first()?.title)
    }

    @Test
    fun update_post_success_test() {
        // 1. Setup
        val repo = mock<PostRepo>()
        val viewModel = PostViewModel(repo)
        val updatedPost = PostModel(id = "p1", title = "Updated Title")

        // 2. Mock updatePost
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Post Updated")
            null
        }.`when`(repo).updatePost(eq(updatedPost), any())

        // 3. Execution
        var resultSuccess = false
        viewModel.updatePost(updatedPost) { success, _ ->
            resultSuccess = success
        }

        // 4. Assert
        assertTrue(resultSuccess)
        verify(repo).updatePost(eq(updatedPost), any())
    }
}