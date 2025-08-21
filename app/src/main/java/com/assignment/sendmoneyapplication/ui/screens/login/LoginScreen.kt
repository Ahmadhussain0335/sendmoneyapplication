package com.assignment.sendmoneyapplication.ui.screens.login

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.assignment.sendmoneyapplication.R
import com.assignment.sendmoneyapplication.data.db.PreferenceManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),   // ViewModel to manage login state
    navController: NavController
) {
    val context = LocalContext.current   // Get Context here
    val scope = rememberCoroutineScope()

    // Collect state from ViewModel
    val username = viewModel.username           // Current username/email input
    val password = viewModel.password           // Current password input
    val isLoggedIn = viewModel.isLoggedIn       // Boolean flag for login success
    val errorMessage = viewModel.errorMessage   // Error message string

    // Local UI state
    var showError by remember { mutableStateOf(false) }        // Controls visibility of error bottom sheet
    var passwordVisible by remember { mutableStateOf(false) }  // Toggles password visibility

    // Error strings from resources
    val emailEmptyError = stringResource(id = R.string.error_email_empty)
    val passwordEmptyError = stringResource(id = R.string.error_password_empty)

    // -------------------- Error Bottom Sheet --------------------
    if (errorMessage != null && showError && errorMessage.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { showError = false },
            containerColor = Color(0xFF686F89),
            tonalElevation = 6.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Error icon
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error Icon",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Error title
                Text(
                    text = stringResource(id = R.string.error_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Error message
                Text(
                    text = errorMessage,
                    fontSize = 14.sp,
                    color = Color(0xFFEFEFEF),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Dismiss button
                Button(
                    onClick = { showError = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        stringResource(id = R.string.dismiss),
                        color = Color(0xFF686F89),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // -------------------- Scaffold --------------------
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(80.dp),
                title = {
                    Text(
                        text = stringResource(id = R.string.sign_in),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp, top = 15.dp)
                    )
                },
                navigationIcon = {
                    // Back button in top app bar
                    IconButton(
                        onClick = {   },
                        modifier = Modifier.padding(bottom = 2.dp, top = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF686F89))
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFEEF1F8))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(110.dp))

            // App Name
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 18.sp,
                color = Color(0xFF40444F)
            )
            Spacer(modifier = Modifier.height(30.dp))

            // Welcome message
            Text(
                text = stringResource(id = R.string.welcome_message),
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(70.dp))

            // -------------------- Email TextField --------------------
            TextField(
                value = username,
                onValueChange = { viewModel.onUsernameChanged(it) },   // Update username in ViewModel
                label = { Text(stringResource(id = R.string.email_hint), fontSize = 14.sp, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(4.dp))

            // -------------------- Password TextField --------------------
            TextField(
                value = password,
                onValueChange = { viewModel.onPasswordChanged(it) },   // Update password in ViewModel
                label = { Text(stringResource(id = R.string.password_hint), fontSize = 14.sp, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = @Composable {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {  // Toggle password visibility
                        Icon(
                            painter = painterResource(
                                id = if (passwordVisible) R.drawable.ic_open_eye else R.drawable.ic_eyes_off
                            ),
                            contentDescription = stringResource(id = R.string.toggle_password_description),
                            tint = Color.Gray
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(90.dp))

            val scope = rememberCoroutineScope()
            // -------------------- Sign In Button --------------------
            Button(
                onClick = {
                    when {
                        username.isBlank() -> {
                            viewModel.setError(emailEmptyError)   // Show email empty error
                            showError = true
                        }
                        password.isBlank() -> {
                            viewModel.setError(passwordEmptyError)  // Show password empty error
                            showError = true
                        }
                        else -> {
                            viewModel.login()   // Attempt login
                            if (!isLoggedIn) {
                                showError = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF686F89)),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(stringResource(id = R.string.sign_in), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(90.dp))

            // -------------------- Terms Text --------------------
            Text(
                text = stringResource(id = R.string.terms_text),
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Trigger login success callback if logged in
            if (isLoggedIn) {
                LaunchedEffect(Unit) {
                    scope.launch {
                        PreferenceManager.setLoggedIn(context, true)
                    }
                    navController.navigate("home")
                }
            }
        }
    }
}