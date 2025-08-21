package com.assignment.sendmoneyapplication

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.assignment.sendmoneyapplication.data.db.PreferenceManager
import com.assignment.sendmoneyapplication.ui.navigation.AppNavHost
import com.assignment.sendmoneyapplication.ui.theme.SendMoneyApplicationTheme

// MainActivity is the entry point of the application
// It extends ComponentActivity, which is the base class for Jetpack Compose apps.
class MainActivity : ComponentActivity() {

    // Lifecycle method called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables drawing behind system bars (status/navigation bar) for edge-to-edge UI
        enableEdgeToEdge()

        // Force portrait for this Activity
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Sets the UI content of the activity using Jetpack Compose
        setContent {
            // Creates and remembers a NavController to manage app navigation
            val navController = rememberNavController()

            // Check if user is logged in
            val startDestination = if (PreferenceManager.isLoggedIn(this)) {
                "home"   // user logged in, go directly to SendMoneyScreen
            } else {
                "login"       // otherwise go to login screen
            }

            // Calls the AppNavHost composable, which defines navigation routes (login, home, history)
            AppNavHost(navController, startDestination)
        }
    }
}