package com.example.postifyapp.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.postifyapp.R
import com.example.postifyapp.ui.theme.White
import kotlinx.coroutines.delay

/**
 * Entry point of the application. Displays the brand logo and
 * handles the initial delay before navigating to the Login screen.
 */
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Extends the layout to use the full screen, including status/navigation bars
        enableEdgeToEdge()
        setContent {
            SplashBody()
        }
    }
}
@Composable
fun SplashBody() {
    val context= LocalContext.current
    val activity= context as Activity
    // LaunchedEffect triggers once when the composable enters the Composition
    LaunchedEffect(Unit) {
        // Hold the splash screen for 3 seconds to show branding
        delay(3000)
        // Navigate to LoginActivity
        val intent= Intent(context,
            LoginActivity::class.java)
        context.startActivity(intent)
        // Remove SplashActivity from the backstack so the user can't navigate back to it
        activity.finish()
    }
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(350.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(1.dp))
            // Visual feedback that the app is loading
            CircularProgressIndicator(
            )
        }
    }
}


