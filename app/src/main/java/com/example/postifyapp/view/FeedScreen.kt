//package com.example.postifyapp.view
//
//import android.net.Uri
//import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import coil.compose.AsyncImage
//import com.example.postifyapp.model.PostModel
//import com.example.postifyapp.repository.PostRepoImpl
//import com.example.postifyapp.viewmodel.PostViewModel
//import androidx.compose.material.icons.filled.Comment
////import androidx.compose.material.icons.outlined.ChatBubbleOutline
//import java.text.SimpleDateFormat
//import java.util.*
//
//fun getFormattedDate(timestamp: Long? = null): String {
//    val date = if (timestamp != null) Date(timestamp) else Date()
//    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
//    return formatter.format(date)
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FeedScreen() {
//    val context = LocalContext.current
//    val postViewModel = remember { PostViewModel(PostRepoImpl()) }
//
//    val currentUserId = remember {
//        com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
//    }
//
//    val loading by postViewModel.loading.observeAsState(initial = false)
//    val allPosts by postViewModel.allPosts.observeAsState(initial = emptyList())
//    val selectedPost by postViewModel.posts.observeAsState()
//
//    var showDialog by remember { mutableStateOf(false) }
//
//    var showDeleteDialog by remember { mutableStateOf(false) }
//    var postToDelete by remember { mutableStateOf<PostModel?>(null) }
//
//    var showCommentDialog by remember { mutableStateOf(false) }
//    var postForComment by remember { mutableStateOf<PostModel?>(null) }
//
//    LaunchedEffect(Unit) {
//        postViewModel.getAllProduct()
//    }
//
//    Scaffold(
//        containerColor = Color(0xFFF8F9FA)
//    ) { padding ->
//        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
//            Text(
//                "Trending Stories",
//                modifier = Modifier.padding(16.dp),
//                style = MaterialTheme.typography.headlineSmall,
//                fontWeight = FontWeight.ExtraBold,
//                color = Color.Black
//            )
//
//            if (loading && (allPosts?.isEmpty() == true)) {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator(color = Color.Black)
//                }
//            } else {
//                LazyColumn(
//                    modifier = Modifier.fillMaxSize(),
//                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
//                    verticalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    items(allPosts!!) { post ->
//                        PostCard(
//                            post = post,
//                            currentUserId = currentUserId,
//                            onEdit = {
//                                postViewModel.getPostById(post.id)
//                                showDialog = true
//                            },
//                            onDelete = {
//                                postToDelete = post
//                                showDeleteDialog = true
//                            },
//                            onLikeClick = {
//                                postViewModel.toggleLike(post, currentUserId ?: "")
//                            },
//                            onCommentClick = {
//                                // This will be used for your Full Post View later
//                                Toast.makeText(context, "Opening comments...", Toast.LENGTH_SHORT).show()
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    if (showDialog && selectedPost != null) {
//        UpdatePostDialog(
//            post = selectedPost!!,
//            onDismiss = { showDialog = false },
//            onUpdate = { updatedModel ->
//                postViewModel.updatePost(updatedModel) { success, msg ->
//                    if (success) showDialog = false
//                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//                }
//            }
//        )
//    }
//
//    if (showDeleteDialog && postToDelete != null) {
//        AlertDialog(
//            onDismissRequest = { showDeleteDialog = false },
//            title = { Text("Delete Post?") },
//            text = { Text("Are you sure you want to delete '${postToDelete?.title}'? This action cannot be undone.") },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        postViewModel.deletePost(postToDelete!!.id) { success, msg ->
//                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//                            showDeleteDialog = false
//                            postToDelete = null
//                        }
//                    },
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53E3E)) // Red color
//                ) {
//                    Text("Delete", color = Color.White)
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDeleteDialog = false }) {
//                    Text("Cancel", color = Color.Gray)
//                }
//            }
//        )
//    }
//}
//
////@Composable
////fun PostCard(post: PostModel,
////             currentUserId: String?,
////             onEdit: () -> Unit,
////             onDelete: () -> Unit,
////             onLikeClick: () -> Unit,
////             onCommentClick: () -> Unit ) {
////    val isLiked = post.likes.contains(currentUserId)
////    val isAuthor = post.userId == currentUserId
////    Card(
////        modifier = Modifier.fillMaxWidth(),
////        shape = RoundedCornerShape(20.dp),
////        colors = CardDefaults.cardColors(containerColor = Color.White),
////        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
////    ) {
////        Column {
////            AsyncImage(
////                model = post.image.ifEmpty { "https://via.placeholder.com/400x200" },
////                contentDescription = "Post Image",
////                modifier = Modifier.fillMaxWidth().height(220.dp),
////                contentScale = ContentScale.Crop
////            )
////
////            Column(modifier = Modifier.padding(16.dp)) {
////                Row(verticalAlignment = Alignment.CenterVertically) {
////                    Box(
////                        modifier = Modifier.size(28.dp).background(Color(0xFFE2E8F0), CircleShape),
////                        contentAlignment = Alignment.Center
////                    ) {
////                        Text(post.author.take(1).uppercase(), fontWeight = FontWeight.Bold)
////                    }
////                    Spacer(modifier = Modifier.width(10.dp))
////                    Text(
////                        text = "${post.author}  •  ${getFormattedDate()}",
////                        style = MaterialTheme.typography.labelMedium,
////                        color = Color.Gray
////                    )
////                }
////                Spacer(modifier = Modifier.height(12.dp))
////                Text(
////                    text = post.title,
////                    style = MaterialTheme.typography.titleLarge,
////                    fontWeight = FontWeight.Bold
////                )
////                Text(
////                    text = post.snippet,
////                    style = MaterialTheme.typography.bodyMedium,
////                    color = Color.DarkGray,
////                    maxLines = 3
////                )
////                Spacer(modifier = Modifier.height(6.dp))
////                Row(verticalAlignment = Alignment.CenterVertically) {
////                    IconButton(
////                        onClick = onLikeClick,
////                        enabled = !isAuthor // Author cannot like
////                    ) {
////                        Icon(
////                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
////                            contentDescription = "Like",
////                            tint = if (isAuthor) Color.LightGray else if (isLiked) Color.Red else Color.Gray
////                        )
////                    }
////                    Text(text = "${post.likes.size}", style = MaterialTheme.typography.bodySmall)
////                }
////
////                Spacer(modifier = Modifier.width(16.dp))
////
////                Row(verticalAlignment = Alignment.CenterVertically) {
////                    IconButton(
////                        onClick = onCommentClick,
////                        enabled = !isAuthor // Author cannot comment
////                    ) {
////                        Icon(
////                            imageVector = Icons.Outlined.ChatBubbleOutline,
////                            contentDescription = "Comment",
////                            tint = if (isAuthor) Color.LightGray else Color.Gray
////                        )
////                    }
////                    Text(text = "${post.comments.size}", style = MaterialTheme.typography.bodySmall)
////                }
////                if (isAuthor) {
////                    Spacer(modifier = Modifier.weight(1f))
////                    IconButton(onClick = onEdit) {
////                        Icon(Icons.Default.Edit, "Edit", tint = Color.Blue)
////                    }
////                    IconButton(onClick = onDelete) {
////                        Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
////                    }
////                }
////
////                if (post.userId == currentUserId) {
////                    Row(
////                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
////                        horizontalArrangement = Arrangement.End
////                    ) {
////                        IconButton(onClick = onEdit) {
////                            Icon(Icons.Default.Edit, "Edit", tint = Color.Blue)
////                        }
////                        IconButton(onClick = onDelete) {
////                            Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
////                        }
////                    }
////                }
////            }
////        }
////    }
////}
//
//@Composable
//fun PostCard(
//    post: PostModel,
//    currentUserId: String?,
//    onEdit: () -> Unit,
//    onDelete: () -> Unit,
//    onLikeClick: () -> Unit,
//    onCommentClick: () -> Unit
//) {
//    val isLiked = post.likes.contains(currentUserId)
//    val isAuthor = post.userId == currentUserId
//
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(20.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
//    ) {
//        Column {
//            AsyncImage(
//                model = post.image.ifEmpty { "https://via.placeholder.com/400x200" },
//                contentDescription = "Post Image",
//                modifier = Modifier.fillMaxWidth().height(220.dp),
//                contentScale = ContentScale.Crop
//            )
//
//            Column(modifier = Modifier.padding(16.dp)) {
//                // Author Header
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Box(
//                        modifier = Modifier.size(28.dp).background(Color(0xFFE2E8F0), CircleShape),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(post.author.take(1).uppercase(), fontWeight = FontWeight.Bold)
//                    }
//                    Spacer(modifier = Modifier.width(10.dp))
//                    Text(
//                        text = "${post.author}  •  ${getFormattedDate()}",
//                        style = MaterialTheme.typography.labelMedium,
//                        color = Color.Gray
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                Text(text = post.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
//                Text(text = post.snippet, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray, maxLines = 3)
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Interaction Row
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    // Like
//                    IconButton(onClick = onLikeClick, enabled = !isAuthor) {
//                        Icon(
//                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
//                            contentDescription = "Like",
//                            tint = if (isAuthor) Color.LightGray else if (isLiked) Color.Red else Color.Gray
//                        )
//                    }
//                    Text(text = "${post.likes.size}", style = MaterialTheme.typography.bodySmall)
//
//                    Spacer(modifier = Modifier.width(16.dp))
//
//                    // Comment
//                    IconButton(onClick = onCommentClick, enabled = !isAuthor) {
//                        // Use Icons.Default.Comment if ChatBubbleOutline gives errors
//                        Icon(
//                            imageVector = Icons.Default.Comment,
//                            contentDescription = "Comment",
//                            tint = if (isAuthor) Color.LightGray else Color.Gray
//                        )
//                    }
//                    Text(text = "${post.comments.size}", style = MaterialTheme.typography.bodySmall)
//
//                    // Edit/Delete (Only show if Author)
//                    if (isAuthor) {
//                        Spacer(modifier = Modifier.weight(1f))
//                        IconButton(onClick = onEdit) {
//                            Icon(Icons.Default.Edit, "Edit", tint = Color.Blue)
//                        }
//                        IconButton(onClick = onDelete) {
//                            Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//    @Composable
//    fun UpdatePostDialog(
//        post: PostModel,
//        onDismiss: () -> Unit,
//        onUpdate: (PostModel) -> Unit
//    ) {
//        var title by remember { mutableStateOf(post.title) }
//        var snippet by remember { mutableStateOf(post.snippet) }
//        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
//
//        val galleryLauncher = rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.GetContent()
//        ) { uri: Uri? -> selectedImageUri = uri }
//
//        AlertDialog(
//            onDismissRequest = onDismiss,
//            title = { Text("Update Post Details", fontWeight = FontWeight.ExtraBold) },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        val finalImage = selectedImageUri?.toString() ?: post.image
//                        onUpdate(
//                            post.copy(
//                                author = post.author,
//                                title = title,
//                                snippet = snippet,
//                                image = finalImage,
//                                userId = post.userId
//                            )
//                        )
//                    },
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
//                ) {
//                    Text("Update Now")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = onDismiss) { Text("Discard", color = Color.Gray) }
//            },
//            text = {
//                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
//                    Box(
//                        modifier = Modifier.fillMaxWidth().height(150.dp)
//                            .clip(RoundedCornerShape(12.dp)).background(Color(0xFFF7FAFC))
//                    ) {
//                        AsyncImage(
//                            model = selectedImageUri ?: post.image,
//                            contentDescription = null,
//                            modifier = Modifier.fillMaxSize(),
//                            contentScale = ContentScale.Crop
//                        )
//                        FilledIconButton(
//                            onClick = { galleryLauncher.launch("image/*") },
//                            modifier = Modifier.align(Alignment.Center).size(50.dp),
//                            colors = IconButtonDefaults.filledIconButtonColors(
//                                containerColor = Color.White.copy(
//                                    alpha = 0.9f
//                                )
//                            )
//                        ) {
//                            Icon(
//                                Icons.Default.Edit,
//                                contentDescription = null,
//                                tint = Color.White,
//                                modifier = Modifier.size(18.dp)
//                            )
//                        }
//                    }
//
//                    Text(
//                        text = "Editing as: ${post.author}",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = Color.Gray
//                    )
//
//                    OutlinedTextField(
//                        value = title,
//                        onValueChange = { title = it },
//                        label = { Text("Headline") },
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = RoundedCornerShape(12.dp)
//                    )
//                    OutlinedTextField(
//                        value = snippet,
//                        onValueChange = { snippet = it },
//                        label = { Text("Content Snippet") },
//                        modifier = Modifier.fillMaxWidth(),
//                        minLines = 3,
//                        shape = RoundedCornerShape(12.dp)
//                    )
//                }
//            }
//        )
//    }



package com.example.postifyapp.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.postifyapp.model.PostModel
import com.example.postifyapp.repository.PostRepoImpl
import com.example.postifyapp.viewmodel.PostViewModel
//import androidx.compose.material.icons.filled.Comment
import java.text.SimpleDateFormat
import java.util.*

// Formatter for post dates
fun getFormattedDate(timestamp: Long? = null): String {
    val date = if (timestamp != null) Date(timestamp) else Date()
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen() {
    val context = LocalContext.current
    val postViewModel = remember { PostViewModel(PostRepoImpl()) }

    val currentUserId = remember {
        com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
    }

    // State for Dialogs
    val loading by postViewModel.loading.observeAsState(initial = false)
    val allPosts by postViewModel.allPosts.observeAsState(initial = emptyList())
    val selectedPost by postViewModel.posts.observeAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
//    var showCommentDialog by remember { mutableStateOf(false) }

    var postToAction by remember { mutableStateOf<PostModel?>(null) }

    LaunchedEffect(Unit) {
        postViewModel.getAllProduct()
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
//        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // We only use the bottom padding from Scaffold.
                // We apply a custom small top padding (e.g., 8dp) instead of the large system one.
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            Text(
                "Trending Stories",
//                modifier = Modifier.padding(16.dp),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            if (loading && (allPosts?.isEmpty() == true)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(allPosts!!) { post ->
                        PostCard(
                            post = post,
                            currentUserId = currentUserId,
                            onEdit = {
                                postViewModel.getPostById(post.id)
                                showEditDialog = true
                            },
                            onDelete = {
                                postToAction = post
                                showDeleteDialog = true
                            }
//                            onLikeClick = {
//                                postViewModel.toggleLike(post, currentUserId ?: "")
//                            },
//                            onCommentClick = {
//                                postToAction = post
//                                showCommentDialog = true
//                            }
                        )
                    }
                }
            }
        }
    }

    // --- EDIT DIALOG ---
    if (showEditDialog && selectedPost != null) {
        UpdatePostDialog(
            post = selectedPost!!,
            onDismiss = { showEditDialog = false },
            onUpdate = { updatedModel ->
                postViewModel.updatePost(updatedModel) { success, msg ->
                    if (success) showEditDialog = false
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // --- DELETE DIALOG ---
    if (showDeleteDialog && postToAction != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Post?") },
            text = { Text("Are you sure you want to delete '${postToAction?.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        postViewModel.deletePost(postToAction!!.id) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            showDeleteDialog = false
                            postToAction = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

//    if (showCommentDialog && postToAction != null) {
//        AddCommentDialog(
//            onDismiss = { showCommentDialog = false },
//            onConfirm = { commentText ->
//                val userEmail = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email ?: "Anonymous"
//                postViewModel.addComment(
//                    postToAction!!.id,    // Pass ID, not the whole post object
//                    currentUserId ?: "",
//                    commentText,
//                    userName = userEmail,
//                )
//                showCommentDialog = false
//                postToAction = null
//            }
//        )
//    }
}

@Composable
fun PostCard(
    post: PostModel,
    currentUserId: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
//    onLikeClick: () -> Unit,
//    onCommentClick: () -> Unit
) {
    val isLiked = post.likes.contains(currentUserId)
    val isAuthor = post.userId == currentUserId

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column {
            AsyncImage(
                model = post.image.ifEmpty { "https://via.placeholder.com/400x200" },
                contentDescription = "Post Image",
                modifier = Modifier.fillMaxWidth().height(220.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Author Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(28.dp).background(Color(0xFFE2E8F0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(post.author.take(1).uppercase(), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${post.author}  •  ${getFormattedDate(post.timestamp)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = post.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = post.snippet, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray, maxLines = 3)

                Spacer(modifier = Modifier.height(16.dp))

                // Interaction Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like Button
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        IconButton(onClick = onLikeClick, enabled = !isAuthor) {
//                            Icon(
//                                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
//                                contentDescription = "Like",
//                                tint = if (isAuthor) Color.LightGray else if (isLiked) Color.Red else Color.Gray
//                            )
//                        }
//                        Text(text = "${post.likes.size}", style = MaterialTheme.typography.bodySmall)
//                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Comment Button
                    // Inside PostCard Interaction Row
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        IconButton(
//                            onClick = onCommentClick,
//                            enabled = !isAuthor // Author cannot comment on their own post
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Comment,
//                                contentDescription = "Comment",
//                                // If it's the author, make it light gray to show it's disabled
//                                tint = if (isAuthor) Color.LightGray else Color.Gray
//                            )
//                        }
//                        // Accessing the size of the comments list
//                        Text(
//                            text = "${post.comments.size}",
//                            style = MaterialTheme.typography.bodySmall
//                        )
//                    }

                    // Edit/Delete for Author
                    if (isAuthor) {
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, "Edit", tint = Color.Blue)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddCommentDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Comment") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Write something...") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { if (text.isNotBlank()) onConfirm(text) }) { Text("Post") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun UpdatePostDialog(
    post: PostModel,
    onDismiss: () -> Unit,
    onUpdate: (PostModel) -> Unit
) {
    var title by remember { mutableStateOf(post.title) }
    var snippet by remember { mutableStateOf(post.snippet) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Post Details", fontWeight = FontWeight.Bold) },
        confirmButton = {
            Button(onClick = {
                onUpdate(post.copy(title = title, snippet = snippet, image = selectedImageUri?.toString() ?: post.image))
            }) { Text("Update Now") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Discard", color = Color.Gray) }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Headline") })
                OutlinedTextField(value = snippet, onValueChange = { snippet = it }, label = { Text("Content") }, minLines = 3)
                Button(onClick = { galleryLauncher.launch("image/*") }) { Text("Change Image") }
            }
        }
    )
}