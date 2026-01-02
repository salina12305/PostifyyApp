package com.example.postifyapp.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {
    val context = LocalContext.current
    val activity = context as Activity

    val email = activity.intent.getStringExtra("email")
    val password = activity.intent.getStringExtra("password")

    data class NavItem(val label: String, val icon: Int)

    var selectedIndex by remember { mutableStateOf(value = 0) }

    var listNav = listOf(
        NavItem(
            label = "Feed",
            icon = com.example.postifyapp.R.drawable.baseline_home_24,
        ),
        NavItem(
            label = "Explore",
            icon = com.example.postifyapp.R.drawable.baseline_search_24,
        ),
        NavItem(
            label = "My Post",
            icon =  com.example.postifyapp.R.drawable.baseline_add_to_photos_24,
        ),
        NavItem(
            label = "Profile",
            icon =  com.example.postifyapp.R.drawable.baseline_person_outline_24

            ),
    )
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val intent= Intent(context, PostCardActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null
                )
            }
        },
        topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Postify", color = Color(0xFF1A365D), fontWeight = FontWeight.Bold)
                    }
                )
        },
        bottomBar = {
            NavigationBar {
                listNav.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Image(
                                painter = painterResource(item.icon),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(item.label)
                        },
                        onClick = {
                            selectedIndex = index
                        },
                        selected = selectedIndex == index
                    )
                }
            }
        }
    ) { padding ->
//            Text(text = "Email:$email")
//            Text(text = "password:$password")
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedIndex) {
                0 -> FeedScreen()
                1 -> ExploreScreen()
                2 -> MyPostScreen()
                3 -> ProfileScreen()
                else -> FeedScreen()

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    DashboardBody()
}
