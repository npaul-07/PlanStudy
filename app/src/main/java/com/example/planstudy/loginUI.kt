//package com.example.studyplanner
//
//import android.widget.Toast
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Lock
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.planstudy.routes.Home
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun LoginScreen(navController: NavController) {
//    var username by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var isError by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(32.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        // Header
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier.padding(bottom = 40.dp)
//        ) {
//            Text(
//                text = "Welcome Back!",
//                style = MaterialTheme.typography.headlineMedium,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "Please sign in to continue",
//                style = MaterialTheme.typography.bodyLarge,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//        }
//
//        // Username Field
//        OutlinedTextField(
//            value = username,
//            onValueChange = {
//                username = it
//                isError = false
//            },
//            label = { Text("Username") },
//            modifier = Modifier.fillMaxWidth(),
//            singleLine = true,
//            isError = isError,
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
//            leadingIcon = {
//                Icon(
//                    imageVector = Icons.Default.Person,
//                    contentDescription = "Username"
//                )
//            },
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
//                focusedBorderColor = MaterialTheme.colorScheme.primary
//            )
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Password Field
//        OutlinedTextField(
//            value = password,
//            onValueChange = {
//                password = it
//                isError = false
//            },
//            label = { Text("Password") },
//            modifier = Modifier.fillMaxWidth(),
//            singleLine = true,
//            isError = isError,
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//            visualTransformation = PasswordVisualTransformation(),
//            leadingIcon = {
//                Icon(
//                    imageVector = Icons.Default.Lock,
//                    contentDescription = "Password"
//                )
//            },
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
//                focusedBorderColor = MaterialTheme.colorScheme.primary
//            )
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // Login Button
//        Button(
//            onClick = {
//                when {
//                    username.isEmpty() && password.isEmpty() -> {
//                        Toast.makeText(context, "Please enter credentials", Toast.LENGTH_SHORT).show()
//                        isError = true
//                    }
//                    username.isEmpty() -> {
//                        Toast.makeText(context, "Username is required", Toast.LENGTH_SHORT).show()
//                        isError = true
//                    }
//                    password.isEmpty() -> {
//                        Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show()
//                        isError = true
//                    }
//                    else -> {
//                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
//                        navController.navigate(Home(username)) {
//                            popUpTo(routes.Login) { inclusive = true }
//                        }
//                    }
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(50.dp)
//        ) {
//            Text("Sign In", fontSize = 16.sp)
//        }
//
//        // Register Link
//        Spacer(modifier = Modifier.height(16.dp))
//        TextButton(
//            onClick = { navController.navigate(routes.Register) }
//        ) {
//            Text(
//                text = "Don't have an account? Sign up",
//                color = MaterialTheme.colorScheme.primary
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    StudyPlannerTheme {
//        // Mock nav controller for preview
//        LoginScreen(navController = rememberNavController())
//    }
//}