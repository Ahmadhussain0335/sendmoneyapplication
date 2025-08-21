package com.assignment.sendmoneyapplication.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.assignment.sendmoneyapplication.data.dao.TransferDao
import com.assignment.sendmoneyapplication.data.entity.TransferEntity

// Room database class for the application
// Holds the database and serves as the main access point for the underlying connection
@Database(
    entities = [TransferEntity::class], // Entities (tables) managed by this database
    version = 1,                        // Database version (increment when schema changes)
    exportSchema = true                  // Exports the schema into a folder for version control
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Abstract function to get the TransferDao instance.
     * Provides access to all database operations for TransferEntity.
     */
    abstract fun transferDao(): TransferDao

    companion object {
        // Volatile ensures that the INSTANCE value is always up-to-date and visible to all threads
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Get a singleton instance of the database.
         * If the database does not exist, it will be created.
         *
         * @param context The application context
         * @return Singleton instance of AppDatabase
         */
        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) { // Thread-safe initialization
                Room.databaseBuilder(
                    context.applicationContext, // Use application context to avoid leaks
                    AppDatabase::class.java,    // The Room database class
                    "send_money.db"             // Database file name
                )
                    .build()                     // Build the database
                    .also { INSTANCE = it }      // Assign to INSTANCE for future calls
            }
    }
}
