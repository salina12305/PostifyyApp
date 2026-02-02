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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.postifyapp.R
import com.example.postifyapp.model.UserModel
import com.example.postifyapp.repository.UserRepoImpl
import com.example.postifyapp.ui.theme.Black
import com.example.postifyapp.ui.theme.Blue
import com.example.postifyapp.ui.theme.DarkGreen
import com.example.postifyapp.ui.theme.LightGrayBackground
import com.example.postifyapp.ui.theme.White
import com.example.postifyapp.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegisterBody()
        }
    }
}

@Composable
fun RegisterBody() {
    // --- State Management ---
    // Tracks user input for form fields
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var terms by remember { mutableStateOf(false) }

    val context = LocalContext.current
    // Initializing ViewModel with its Repository implementation
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }
    val activity = context as? Activity

    // Controls toggling between password hidden (***) and visible text
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

    val fieldModifier = Modifier.fillMaxWidth()

    val inputColors = TextFieldDefaults.colors(
        unfocusedContainerColor = LightGrayBackground,
        focusedContainerColor = LightGrayBackground,
        focusedIndicatorColor = DarkGreen,
        unfocusedIndicatorColor = Color.Transparent
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())  // Ensures small screens can scroll the form
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Brand Header
            Text(
                "Positfy",
                style = TextStyle(fontSize = 35.sp, color = Blue),
                fontWeight = FontWeight.Bold)


            Text("Create a new account", color = Color.Gray,
                modifier = Modifier.padding(bottom = 30.dp))

            // --- Input Fields ---
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = fieldModifier.testTag("regEmail"),
                placeholder = { Text("Email Address") },
                colors = inputColors,
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                modifier = fieldModifier.testTag("regUsername"),
                placeholder = { Text("Username") },
                colors = inputColors,
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field with Visibility Toggle
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = fieldModifier.testTag("regPassword"),
                placeholder = { Text("Password") },
                trailingIcon = {
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(
                            painter = painterResource(if (!passwordVisibility) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24),
                            contentDescription = null,
                            tint = Black
                        )
                    }
                },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                colors = inputColors,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it},
                modifier = fieldModifier.testTag("regConfirmPassword"),
                placeholder = { Text("Confirm Password") },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
                        Icon(
                            painter = painterResource(if (!confirmPasswordVisibility) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24),
                            contentDescription = null,
                            tint = Black
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                colors = inputColors,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Agreement Section ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = terms,
                    onCheckedChange = { terms = it },
                    modifier = Modifier.testTag("regTerms"),
                    colors = CheckboxDefaults.colors(checkedColor = DarkGreen)
                )
                Text("I agree to the Terms & Conditions", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Registration Logic ---
            Button(
                    onClick = {
                        when {
                            email.isBlank() || username.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                                Toast.makeText(
                                    context,
                                    "Please fill all fields.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            password != confirmPassword -> {
                                Toast.makeText(
                                    context,
                                    "Passwords do not match.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            !terms -> {
                                Toast.makeText(
                                    context,
                                    "Please agree to the Terms & Conditions.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                // Step A: Register User in Firebase Auth
                                userViewModel.register(
                                    email,
                                    password
                                ) { success, message, userId ->
                                    if (success) {
                                        // Step B: Create a User Profile in Database upon successful Auth
                                        val model = UserModel(
                                            userId = userId,
                                            email = email,
                                            firstName = "", lastName = "", dob = "", contact = ""
                                        )
                                        userViewModel.addUserToDatabase(
                                            userId,
                                            model
                                        ) { dbSuccess, dbMessage ->
                                            if (dbSuccess) {
                                                // Step C: Send Email Verification for security
                                                val firebaseUser =
                                                    FirebaseAuth.getInstance().currentUser
                                                firebaseUser?.sendEmailVerification()
                                                    ?.addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            Toast.makeText(
                                                                context,
                                                                "Verification email sent to $email",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    }

                                                Toast.makeText(
                                                    context,
                                                    "Registration Successful!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                // Step D: Redirect to Login
                                                val intent =
                                                    Intent(context, LoginActivity::class.java)
                                                context.startActivity(intent)
                                                activity?.finish()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    dbMessage,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .testTag("regSignUpButton"),
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                Text("Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = White)
            }

            Spacer(modifier = Modifier.height(24.dp))
            // --- Footer Navigation ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text("  OR  ", color = Color.Gray)
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(30.dp))
            // Styled text for navigation back to Login
            Text(
                text = buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(SpanStyle(color = Blue, fontWeight = FontWeight.Bold)) {
                        append("Sign In")
                    }
                },
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .clickable {
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                        activity?.finish()
                    },
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister(){
    RegisterBody()
}