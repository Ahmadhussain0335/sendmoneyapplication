package com.assignment.sendmoneyapplication.data.db

import android.content.Context

// A singleton object to manage app preferences (like login state)
object PreferenceManager {
    // Name of the SharedPreferences file
    private const val PREF_NAME = "my_prefs"

    // Key to store and retrieve login state
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    /**
     * Save the login state in SharedPreferences.
     *
     * @param context - Application or Activity context
     * @param isLoggedIn - true if the user is logged in, false otherwise
     */
    fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
        // Get SharedPreferences instance
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        // Save the boolean value and apply changes asynchronously
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    /**
     * Check whether the user is logged in.
     *
     * @param context - Application or Activity context
     * @return true if the user is logged in, false otherwise
     */
    fun isLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        // Return stored value (default is false if not set)
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Clear all saved preferences.
     * This will remove every key-value pair in this preferences file.
     *
     * @param context - Application or Activity context
     */
    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        // Clear all data and apply changes asynchronously
        prefs.edit().clear().apply()
    }
}
