package com.example.postifyapp.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.postifyapp.model.PostModel
import com.example.postifyapp.repository.PostRepoImpl
import com.example.postifyapp.viewmodel.PostViewModel

@Composable
fun MyPostScreen() {
    val context = LocalContext.current
    val postViewModel = remember { PostViewModel(PostRepoImpl()) }
    val currentUserId = remember {
        com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid }
    val loading by postViewModel.loading.observeAsState(initial = false)
    val allPosts by postViewModel.allPosts.observeAsState(initial = emptyList())
    val selectedPost by postViewModel.posts.observeAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var postToAction by remember { mutableStateOf<PostModel?>(null) }
//    var showCommentDialog by remember { mutableStateOf(false) }
//    var postIdForComment by remember { mutableStateOf("") }

    var selectedPostForComments by remember { mutableStateOf<PostModel?>(null) }

    LaunchedEffect(Unit) {
        postViewModel.getAllPost()
    }
    val myFilteredPosts = allPosts?.filter { it.userId == currentUserId } ?: emptyList()

    Scaffold(containerColor = Color(0xFFF8F9FA)) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                "My Published Stories",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            } else if (myFilteredPosts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("You haven't posted any stories yet.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(myFilteredPosts) { post ->
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
                                }
//                                else {
//                                    Toast.makeText(context, "Login to interact", Toast.LENGTH_SHORT).show()
//                                }
                            },
                            onCommentClick = {
                                if (currentUserId != null) {
                                    selectedPostForComments = post
                                }
//                                postIdForComment = post.id
//                                showCommentDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
//    if (showCommentDialog) {
//        AddCommentDialog(
//            onDismiss = { showCommentDialog = false },
//            onConfirm = { commentText ->
//                postViewModel.postComment(postIdForComment, commentText)
//                showCommentDialog = false
//                Toast.makeText(context, "Comment posted!", Toast.LENGTH_SHORT).show()
//            }
//        )
//    }

    if (selectedPostForComments != null) {
        ViewCommentsDialog(
            post = selectedPostForComments!!,
            currentUserId = currentUserId,
            onDismiss = { selectedPostForComments = null },
            onPostComment = { text ->
                postViewModel.postComment(selectedPostForComments!!.id, text)
                Toast.makeText(context, "Comment posted!", Toast.LENGTH_SHORT).show()
            },
            onUpdateComment = { commentId, newText ->
                postViewModel.updateComment(selectedPostForComments!!.id, commentId, newText)
                Toast.makeText(context, "Comment updated!", Toast.LENGTH_SHORT).show()
            }
        )
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
            title = { Text("Delete Story?") },
            text = { Text("Are you sure you want to delete this post?") },
            confirmButton = {
                Button(
                    onClick = {
                        postViewModel.deletePost(postToAction!!.id) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            showDeleteDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}