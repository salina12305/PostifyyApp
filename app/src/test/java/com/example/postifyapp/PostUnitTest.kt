package com.example.postifyapp

import android.content.Context
import android.net.Uri
import com.example.postifyapp.model.PostModel
import com.example.postifyapp.repository.PostRepo
import com.example.postifyapp.viewmodel.PostViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class PostUnitTest {

    @Test
    fun publish_post_success_test() {
        // 1. Setup Mocks
        val repo = mock<PostRepo>()
        val viewModel = PostViewModel(repo)
        val mockContext = mock<Context>()
        val mockUri = mock<Uri>()

        val testImageUrl = "https://firebase.storage/post_image.jpg"
        val testPost = PostModel(
            id = "post_123",
            userId = "user_456",
            author = "test@test.com",
            title = "My Test Post",
            snippet = "This is a test snippet",
            image = testImageUrl
        )

        // 2. Mock Step 1: Image Upload
        doAnswer { invocation ->
            val callback = invocation.getArgument<(String?) -> Unit>(2)
            callback(testImageUrl) // Simulate successful upload returning a URL
            null
        }.`when`(repo).uploadImage(eq(mockContext), eq(mockUri), any())

        // 3. Mock Step 2: Add Post to Database
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Post Published Successfully")
            null
        }.`when`(repo).addPost(any(), any())

        // 4. Execution
        var uploadUrlResult: String? = ""
        var dbSuccess = false
        var dbMessage = ""

        // Simulate the flow in your Button onClick
        viewModel.uploadImage(mockContext, mockUri) { url ->
            uploadUrlResult = url
            if (url != null) {
                viewModel.addPost(testPost) { success, msg ->
                    dbSuccess = success
                    dbMessage = msg
                }
            }
        }

        // 5. Assertions
        assertEquals(testImageUrl, uploadUrlResult)
        assertTrue(dbSuccess)
        assertEquals("Post Published Successfully", dbMessage)

        // 6. Verify repository interactions
        verify(repo).uploadImage(eq(mockContext), eq(mockUri), any())
        verify(repo).addPost(eq(testPost), any())
    }
}