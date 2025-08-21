package com.assignment.sendmoneyapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.assignment.sendmoneyapplication.ui.screens.login.LoginScreen
import com.assignment.sendmoneyapplication.ui.screens.sendMoney.SendMoneyScreen
import com.assignment.sendmoneyapplication.ui.screens.history.TransferHistoryScreen

// Composable function that sets up the Navigation Graph for the app
// It manages navigation between different screens using a NavController.
@Composable
fun AppNavHost(navController: NavHostController, startDestination: String) {
    // NavHost defines the navigation graph with a start destination
    NavHost(navController = navController, startDestination = startDestination) {

        // Login screen route
        composable("login") {
            // Displays LoginScreen and navigates to "home" on successful login
            LoginScreen(navController = navController)
        }

        // Home screen route
        composable("home") {
            // Displays the main SendMoneyScreen
            // Pass the NavController to allow navigation from this screen
            SendMoneyScreen(navController = navController)
        }

        // Transfer history screen route
        composable("history") {
            // Displays the TransferHistoryScreen
            // NavController is passed to allow navigation back or to other screens
            TransferHistoryScreen(navController = navController)
        }
    }
}
