package com.example.postifyapp.view
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import com.example.postifyapp.view.ui.theme.PostifyAppTheme
//
//class DashboardActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            PostifyAppTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    PostifyAppTheme {
//        Greeting("Android")
//    }
//}


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.postifyapp.R
import com.example.postifyapp.view.ui.theme.Blue

// --- Data Models ---
data class DataModel(
    val id: Int,
    val author: String,
    val title: String,
    val snippet: String,
    val date: String,
    val imageResId: Int
)

data class NavItem(val label: String, val icon: Int)

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
    var selectedIndex by remember { mutableIntStateOf(0) }

    val listNav = listOf(
        NavItem("Feed", R.drawable.baseline_home_24),
        NavItem("Explore", R.drawable.baseline_search_24),
        NavItem("My Posts", R.drawable.baseline_favorite_border_24),
        NavItem("Profile", R.drawable.baseline_person_outline_24)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Postify", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Blue
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { Toast.makeText(context, "Create New Blog", Toast.LENGTH_SHORT).show() },
                containerColor = Blue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Post")
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                listNav.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(painterResource(item.icon), contentDescription = item.label) },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1A73E8),
                            indicatorColor = Color(0xFFE8F0FE)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedIndex) {
                0 -> BlogFeedScreen()
                else -> PlaceholderScreen(listNav[selectedIndex].label)
            }
        }
    }
}

@Composable
fun BlogFeedScreen() {
    // Dummy Data
    val posts = listOf(
        DataModel(1, "Alex Rivera", "Top 10 Kotlin Tips", "Kotlin is evolving fast. Here are some tips to keep your code clean...", "Oct 12", R.drawable.logoo),
        DataModel(2, "Sarah Chen", "The Future of AI", "Generative AI is changing the landscape of software engineering forever.", "Oct 11", R.drawable.sun),
        DataModel(3, "Jordan Lee", "Traveling on a Budget", "You don't need a fortune to see the world. Start with these five steps...", "Oct 09", R.drawable.logoo)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Trending Stories", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        }
        items(posts) { post ->
            BlogCard(post)
        }
    }
}

@Composable
fun BlogCard(post: DataModel) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Image(
                painter = painterResource(post.imageResId),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(160.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(24.dp).background(Color.LightGray, CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Text(post.author, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                    Spacer(Modifier.width(8.dp))
                    Text("•", color = Color.Gray)
                    Spacer(Modifier.width(8.dp))
                    Text(post.date, fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(Modifier.height(8.dp))
                Text(post.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    post.snippet,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$name Screen coming soon!", color = Color.Gray)
    }
}