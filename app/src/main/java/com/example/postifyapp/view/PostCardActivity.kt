//package com.example.postifyapp.view
//
//import android.R.attr.padding
//import android.app.Activity
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import coil3.compose.AsyncImage
//import com.example.postifyapp.R
//import com.example.postifyapp.model.PostModel
//import com.example.postifyapp.repository.PostRepoImpl
//import com.example.postifyapp.utils.ImageUtils
//import com.example.postifyapp.viewmodel.PostViewModel
//
//import java.util.UUID
//
//class PostCardActivity : ComponentActivity() {
//    lateinit var imageUtils: ImageUtils
//    var selectedImageUri by mutableStateOf<Uri?>(null)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        imageUtils = ImageUtils(this, this)
//        imageUtils.registerLaunchers { uri ->
//            selectedImageUri = uri
//        }
//        setContent {
//            PostScreen(
//            selectedImageUri = selectedImageUri,
//            onPickImage = { imageUtils.launchImagePicker() }
//            )
//        }
//    }
//}
//@Composable
//fun PostScreen(
//    selectedImageUri: Uri?,
//    onPickImage: () -> Unit,
//    viewModel: PostViewModel = PostViewModel(PostRepoImpl ())
//
//) {
//    val context = LocalContext.current
//    val activity = context as? Activity
//
//    val repo = remember { PostRepoImpl() }
//    val viewModel = remember { PostViewModel(repo) }
//
//    var author by remember { mutableStateOf("") }
//    var title by remember { mutableStateOf("") }
//    var snippet by remember { mutableStateOf("") }
//    var image by remember { mutableStateOf("") }
//
//    Scaffold { padding ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.White)
//                .padding(paddingValues = padding)
//                .padding(16.dp)
//
//        ) {
//            item {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .clickable(
//                            indication = null,
//                            interactionSource = remember { MutableInteractionSource() }
//                        ) {
//                            onPickImage()
//                        }
//                        .padding(10.dp)
//                ) {
//                    if (selectedImageUri != null) {
//                        AsyncImage(
//                            model = selectedImageUri,
//                            contentDescription = "Selected Image",
//                            modifier = Modifier.fillMaxSize(),
//                            contentScale = ContentScale.Crop
//                        )
//                    } else {
//                        Image(
//                            painterResource(R.drawable.bg),
//                            contentDescription = null,
//                            modifier = Modifier.fillMaxSize(),
//                            contentScale = ContentScale.Crop
//                        )
//                    }
//                }
//                OutlinedTextField(
//                    value = author,
//                    onValueChange = { author = it },
//                    label = { Text("Author Name") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                OutlinedTextField(
//                    value = title,
//                    onValueChange = { title = it },
//                    label = { Text("Post Title") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//                OutlinedTextField(
//                    value = snippet,
//                    onValueChange = { snippet = it },
//                    label = { Text("Post snippet") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(modifier = Modifier.height(12.dp))
////        topBar = {
////        }
////    ) { padding->
////        Column(
////            modifier = Modifier.fillMaxSize()
////                .background(Color.White)
////                .padding(paddingValues = padding)
////                .padding(16.dp)
////
////        ) {
////            OutlinedTextField(
////                value = author,
////                onValueChange = { author = it },
////                label = { Text("Author Name") },
////                modifier = Modifier.fillMaxWidth()
////            )
////
////            OutlinedTextField(
////                value = title,
////                onValueChange = { title = it },
////                label = { Text("Post Title") },
////                modifier = Modifier.fillMaxWidth()
////            )
////            OutlinedTextField(
////                value = snippet,
////                onValueChange = { snippet = it },
////                label = { Text("Post snippet") },
////                modifier = Modifier.fillMaxWidth()
////            )
////            OutlinedTextField(
////                value = image,
////                onValueChange = { image = it },
////                label = { Text("image") },
////                modifier = Modifier.fillMaxWidth()
////            )
////
////            Spacer(modifier = Modifier.height(12.dp))
//
////            Button(
////                onClick = {
////                    Log.d("PRODUCT", "Add Product button clicked")
////                    if (author.isBlank() || title.isBlank() || snippet.isBlank() || image.isBlank()) {
////                        Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
////                        return@Button
////                    }
////                    val id = UUID.randomUUID().toString()
////                    val post = PostModel(
////                        id = id,
////                        author = author,
////                        title = title,
////                        snippet = snippet,
////                        image=image,
////                        )
////                    viewModel.addPost(post) {success, message->
////                        if (success){
////                            Toast.makeText(context, "Product added successfully", Toast.LENGTH_SHORT).show()
////                        }
////                    }
////
////                    author = ""
////                    title = ""
////                    snippet = ""
////                    image=""
////                },
////                shape = RoundedCornerShape(10.dp),
////                modifier = Modifier.align(Alignment.CenterHorizontally)
////            ) {
////                Text("Add post")
////
////        }
//
//                Button(
//                    onClick = {
//                        if (selectedImageUri != null) {
//                            viewModel.uploadImage(context, selectedImageUri) { imageUrl ->
//                                if (imageUrl != null) {
//                                    val model = PostModel(
//                                        "",
//                                        author = author,
//                                        title = title,
//                                        snippet = snippet,
//                                        image = image,
//
//                                        )
//                                    viewModel.addPost(model) { success, message ->
//                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
//                                        if (success) activity?.finish()
//                                    }
//                                } else {
//                                    Log.e("Upload Error", "Failed to upload image to Cloudinary")
//                                }
//                            }
//                        } else {
//                            Toast.makeText(
//                                context,
//                                "Please select an image first",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("Add Product")
//                }
//
//
//                Spacer(modifier = Modifier.height(20.dp))
//
//
//            }
//
//        }
//    }
//}
////@Composable
////fun PostItem(post: PostModel) {
////    Card (
////        modifier = Modifier
////            .fillMaxWidth()
////            .padding(vertical = 6.dp),
////        elevation = CardDefaults.cardElevation(4.dp)
////    ) {
////        Column(modifier = Modifier.padding(16.dp)) {
////            Text(
////                text = post.author,
////                style = MaterialTheme.typography.titleMedium
////            )
////            Text(text = post.title)
////            Text(text = post.snippet)
////            Text(text = post.image)
////        }
////    }
////}



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

//    val userId = remember {
//        com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
//    }
    val currentUser = remember {
        com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    }
    val userId = currentUser?.uid ?: ""
    val userEmail = currentUser?.email ?: "Unknown"

//    var author by remember { mutableStateOf("") }
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
                // Image Picker Box
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
                        // Placeholder when no image is selected
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
//                OutlinedTextField(
//                    value = author,
//                    onValueChange = { author = it },
//                    label = { Text("Author Name") },
//                    modifier = Modifier.fillMaxWidth()
//                )
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
//                        if (author.isBlank() || title.isBlank()) {
//                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
//                            return@Button
//                        }

                        isUploading = true
                        viewModel.uploadImage(context, selectedImageUri) { uploadedUrl ->
                            if (uploadedUrl != null) {
                                val newPost = PostModel(
                                    id = UUID.randomUUID().toString(),

//                                    userId = userId,
//
//                                    author = author,

                                    userId = userId,       // Hidden UID
                                    author = userEmail,

                                    title = title,
                                    snippet = snippet,
                                    image = uploadedUrl // SUCCESS: The URL from the cloud is saved here
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