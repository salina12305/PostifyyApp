package com.example.postifyapp.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.postifyapp.R
import com.example.postifyapp.ui.theme.Blue
import com.example.postifyapp.viewmodel.UserViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginBody()
        }
    }
}

/**
 * LoginActivity: Handles user authentication.
 * It provides entry points for existing users, password recovery, and new user registration.
 */
@Composable
fun LoginBody() {
    // --- State Management ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as Activity
    val userViewModel = remember { UserViewModel(com.example.postifyapp.repository.UserRepoImpl()) }
    val fieldModifier = Modifier.fillMaxWidth()
    val inputColors = TextFieldDefaults.colors(

        unfocusedContainerColor = com.example.postifyapp.ui.theme.LightGrayBackground,
        focusedContainerColor = com.example.postifyapp.ui.theme.LightGrayBackground,
        focusedIndicatorColor = Blue,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        // Brand Identity
        Text(
            text = "Postify",
            style = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Blue)
        )
        Spacer(modifier = Modifier.height(40.dp))
        // --- Email Input ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email", fontWeight = FontWeight.Light) },
            modifier = fieldModifier,
            shape = RoundedCornerShape(12.dp),
            colors = inputColors,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        // --- Password Input with Visibility Toggle ---
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password", fontWeight = FontWeight.Light) },
            modifier = fieldModifier,
            visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { visibility = !visibility }) {
                    Icon(
                        painter = painterResource(
                            if (!visibility) R.drawable.baseline_visibility_off_24
                            else R.drawable.baseline_visibility_24
                        ),
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = inputColors,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        // --- Login Action ---
        Button(
            onClick = {
                // 1. Basic validation
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else {
                    // 2. Call ViewModel to perform Firebase Authentication
                    userViewModel.login(email, password) { success, message ->
                        if (success) {
                            // 3. Post-login Security Check: Verify if email is confirmed
                            val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                            if (firebaseUser?.isEmailVerified == true) {
                                // Navigate to main application hub
                                context.startActivity(Intent(context, DashboardActivity::class.java))
                                activity.finish()
                            } else {
                                // Prevent access if email isn't verified yet
                                Toast.makeText(context, "Please verify your email via Gmail first.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            // Display Firebase error messages (e.g., "Invalid password")
                            Toast.makeText(context, "Login Failed: $message", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue)
        ) {
            Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(20.dp))
        // --- Password Reset ---
        Text(
            text = "Forgotten your login details? Reset your password.",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable {
                context.startActivity(Intent(context, ForgetPasswordActivity::class.java))
            }
        )
        Spacer(modifier = Modifier.height(30.dp))

        // Visual Divider for alternative actions
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text("  OR  ", color = Color.Gray, fontSize = 12.sp)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(30.dp))
        // --- Registration Redirect ---
        Row(modifier = Modifier.padding(bottom = 30.dp)) {
            Text("Don't have an account? ", color = Color.Gray)
            Text(
                "SignUp",
                color = Blue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context,
                        RegistrationActivity::class.java))
                    activity.finish()  // Closes Login so back button doesn't loop
                }
            )
        }
    }
}
@Preview
@Composable
fun PreviewLogin(){
    LoginBody()
}
