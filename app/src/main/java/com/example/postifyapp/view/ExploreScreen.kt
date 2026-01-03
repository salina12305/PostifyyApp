package com.example.postifyapp.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.example.postifyapp.model.PostModel
import com.example.postifyapp.repository.PostRepoImpl
import com.example.postifyapp.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen() {
    val context = LocalContext.current
    val postViewModel = remember { PostViewModel(PostRepoImpl()) }
    val currentUserId = remember { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid }

    var searchQuery by remember { mutableStateOf("") }
    val allPosts by postViewModel.allPosts.observeAsState(initial = emptyList())
    val loading by postViewModel.loading.observeAsState(initial = false)

    var selectedPostForComments by remember { mutableStateOf<PostModel?>(null) }

    LaunchedEffect(Unit) {
        postViewModel.getAllPost()
    }

    val filteredPosts = allPosts?.filter { post ->
        post.title.contains(searchQuery, ignoreCase = true) ||
                post.author.contains(searchQuery, ignoreCase = true)
    } ?: emptyList()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by title or author...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (filteredPosts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No stories found matching '$searchQuery'", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filteredPosts) { post ->
                    PostCard(
                        post = post,
                        currentUserId = currentUserId,
                        onEdit = {  },
                        onDelete = {  },
                        onLikeToggle = {
                            if (currentUserId != null) {
                                postViewModel.toggleLike(post.id, currentUserId)
                            } else {
                            }
                        },

                        onCommentClick = {
                            if (currentUserId != null) {
                                selectedPostForComments = post
                            } else {
                                Toast.makeText(context, "Please login to comment", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
    if (selectedPostForComments != null) {
        ViewCommentsDialog(
            post = selectedPostForComments!!,
            currentUserId = currentUserId,
            onDismiss = { selectedPostForComments = null },
            onPostComment = { text ->
                postViewModel.postComment(selectedPostForComments!!.id, text)
            },
            onUpdateComment = { commentId, newText ->
                postViewModel.updateComment(selectedPostForComments!!.id, commentId, newText)
            },
            onDeleteComment = { commentId ->
                postViewModel.deleteComment(selectedPostForComments!!.id, commentId)
            }
        )
    }
}