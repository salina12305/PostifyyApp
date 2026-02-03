
package com.example.postifyapp.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.postifyapp.R
import com.example.postifyapp.model.PostModel
import com.example.postifyapp.repository.PostRepoImpl
import com.example.postifyapp.utils.ImageUtils
import com.example.postifyapp.viewmodel.PostViewModel
import java.util.UUID

/**
 * Activity responsible for creating a new post.
 * It integrates ImageUtils for gallery access and PostViewModel for data persistence.
 */
class PostCardActivity : ComponentActivity() {
    private lateinit var imageUtils: ImageUtils
    // Tracks the local URI of the image selected by the user before it's uploaded
    private var selectedImageUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize helper for image picking and register the callback for results
        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            selectedImageUri = uri
        }

        setContent {
            PostScreen(
                selectedImageUri = selectedImageUri,
                onPickImage = { imageUtils.launchImagePicker() }
            )
        }
    }
}

@Composable
fun PostScreen(
    selectedImageUri: Uri?,
    onPickImage: () -> Unit,
    viewModel: PostViewModel = PostViewModel(PostRepoImpl())
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // --- User Session ---
    val currentUser = remember {
        com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    }
//    val userId = currentUser?.uid ?: ""
//    val userEmail = currentUser?.email ?: "Unknown"

    val userId = currentUser?.uid ?: "test_uid_123"
    val userEmail = currentUser?.email ?: "testuser1@gmail.com"

    // --- UI State ---
    var title by remember { mutableStateOf("") }
    var snippet by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) } // Controls the loading spinner and button state

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                // --- Image Selection Area ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onPickImage() }
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        // Display the locally picked image using Coil
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Placeholder UI when no image is selected
                        Image(
                            painter = painterResource(R.drawable.bg),
                            contentDescription = "Placeholder",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            color = Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("Tap to select image", color = Color.White)
                            }
                        }
                    }
                }

                Text(
                    text = "Posting as: $userEmail",
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // --- Form Fields ---
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Post Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = snippet,
                    onValueChange = { snippet = it },
                    label = { Text("Post Snippet") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))
                // --- Submission Logic ---
                Button(
                    onClick = {
                        // 1. Validate that an image exists
                        if (selectedImageUri == null) {
                            Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isUploading = true
                        // 2. First Step: Upload the binary image to Firebase Storage
                        viewModel.uploadImage(context, selectedImageUri) { uploadedUrl ->
                            if (uploadedUrl != null) {
                                // 3. Second Step: Use the returned URL to create the Post object
                                val newPost = PostModel(
                                    id = UUID.randomUUID().toString(),

                                    userId = userId,
                                    author = userEmail,

                                    title = title,
                                    snippet = snippet,
                                    image = uploadedUrl // This is the public HTTPS URL from Firebase Storage
                                )
                                // 4. Third Step: Save the Post object to the Realtime Database
                                viewModel.addPost(newPost) { success, message ->
                                    isUploading = false
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) activity?.finish() // Close screen on success
                                }
                            } else {
                                isUploading = false
                                Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading // Disable button during network operations
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Publish Post")
                    }
                }
            }
        }
    }
}