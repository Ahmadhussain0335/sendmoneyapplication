package com.assignment.sendmoneyapplication.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

// ViewModel to manage login screen state and logic
class LoginViewModel : ViewModel() {

    // -------------------- State Variables --------------------
    var username by mutableStateOf("")   // Holds the current username/email input
        private set

    var password by mutableStateOf("")   // Holds the current password input
        private set

    var isLoggedIn by mutableStateOf(false)   // Tracks whether login was successful
        private set

    var errorMessage by mutableStateOf<String?>(null)   // Holds the current error message (if any)
        private set

    var showError by mutableStateOf(false)   // Controls whether error message should be displayed
        private set

    // -------------------- User Input Handlers --------------------
    // Update username state
    fun onUsernameChanged(value: String) {
        username = value
    }

    // Update password state
    fun onPasswordChanged(value: String) {
        password = value
    }

    // -------------------- Login Logic --------------------
    fun login() {
        // Validate empty username
        if (username.isBlank()) {
            setError("Email cannot be empty")
            return
        }

        // Validate empty password
        if (password.isBlank()) {
            setError("Password cannot be empty")
            return
        }

        // Dummy login logic (replace with real authentication)
        if (username.trim() == "testuser" && password.trim() == "password123") {
            isLoggedIn = true           // Mark login as successful
            errorMessage = null         // Clear previous errors
            showError = false           // Hide error UI
        } else {
            setError("Please enter valid credentials.")  // Show login failure message
        }
    }

    // -------------------- Error Handling --------------------
    fun setError(message: String) {
        errorMessage = message   // Set error message
        showError = true         // Trigger error display
    }
}