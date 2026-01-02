
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

class PostCardActivity : ComponentActivity() {
    private lateinit var imageUtils: ImageUtils
    private var selectedImageUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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

    val currentUser = remember {
        com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    }
    val userId = currentUser?.uid ?: ""
    val userEmail = currentUser?.email ?: "Unknown"

    var title by remember { mutableStateOf("") }
    var snippet by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
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
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
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
//
                Spacer(modifier = Modifier.height(8.dp))

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

                Button(
                    onClick = {
                        if (selectedImageUri == null) {
                            Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isUploading = true
                        viewModel.uploadImage(context, selectedImageUri) { uploadedUrl ->
                            if (uploadedUrl != null) {
                                val newPost = PostModel(
                                    id = UUID.randomUUID().toString(),

                                    userId = userId,
                                    author = userEmail,

                                    title = title,
                                    snippet = snippet,
                                    image = uploadedUrl
                                )
                                viewModel.addPost(newPost) { success, message ->
                                    isUploading = false
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) activity?.finish()
                                }
                            } else {
                                isUploading = false
                                Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
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