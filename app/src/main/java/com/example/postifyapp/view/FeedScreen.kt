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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.postifyapp.model.PostModel
import com.example.postifyapp.repository.PostRepoImpl
import com.example.postifyapp.viewmodel.PostViewModel
import java.text.SimpleDateFormat
import java.util.*

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

    val loading by postViewModel.loading.observeAsState(initial = false)
    val allPosts by postViewModel.allPosts.observeAsState(initial = emptyList())
    val selectedPost by postViewModel.posts.observeAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var postToAction by remember { mutableStateOf<PostModel?>(null) }

    var showCommentDialog by remember { mutableStateOf(false) }
    var postIdForComment by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        postViewModel.getAllPost()
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            Text(
                "Trending Stories",
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
                            },
                            onLikeToggle = {
                                if (currentUserId != null) {
                                    postViewModel.toggleLike(post.id, currentUserId)
                                } else {
                                    Toast.makeText(context, "Please login to like posts", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onCommentClick = {
                                if (currentUserId != null) {
                                    postIdForComment = post.id
                                    showCommentDialog = true
                                } else {
                                    Toast.makeText(context, "Please login to comment", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
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
    if (showCommentDialog) {
        AddCommentDialog(
            onDismiss = { showCommentDialog = false },
            onConfirm = { commentText ->
                postViewModel.postComment(postIdForComment, commentText)
                showCommentDialog = false
            }
        )
    }
    // --- Place this at the bottom of FeedScreen.kt ---

    @Composable
    fun CommentSection(comments: Map<String, PostModel.CommentModel>?) {
        // We convert the Firebase Map to a List for the LazyColumn
        val commentList = comments?.values?.toList()?.sortedByDescending { it.timestamp } ?: emptyList()

        if (commentList.isEmpty()) {
            Text(
                "No comments yet.",
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        } else {
            // Use a weight or height limit so it doesn't push the text field off-screen
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(commentList) { comment ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(comment.userName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        Text(comment.text, style = MaterialTheme.typography.bodyMedium)
                        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), thickness = 0.5.dp, color = Color.LightGray)
                    }
                }
            }
        }
    }

    @Composable
    fun ViewCommentsDialog(
        post: PostModel,
        onDismiss: () -> Unit,
        onConfirm: (String) -> Unit
    ) {
        var text by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Discussion", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Here we call the List component we made above
                    CommentSection(post.comments)

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = { Text("Write a comment...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(onClick = { if (text.isNotBlank()) onConfirm(text) }) { Text("Post") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Close") }
            }
        )
    }
}

@Composable
fun PostCard(
    post: PostModel,
    currentUserId: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onLikeToggle: () -> Unit,
    onCommentClick: () -> Unit
) {
    val isAuthor = post.userId == currentUserId
    val isLiked = post.likedBy.contains(currentUserId)

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(onClick = onLikeToggle) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) Color.Red else Color.Gray
                        )
                    }
                    Text(
                        text = "${post.likedBy.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    IconButton(onClick = onCommentClick) {
                        Icon(Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comment", tint = Color.Gray)
                    }
                    Text(
//                        text = "${post.comments.size}",
                        text = "${post.comments?.size ?: 0}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

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
        title = { Text("Add Comment", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Write your thoughts...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
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