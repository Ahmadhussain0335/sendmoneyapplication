package com.assignment.sendmoneyapplication.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Room entity representing a transfer request.
// This class maps to the "transfers" table in the database.
@Entity(
    tableName = "transfers", // Name of the table in the database
    indices = [
        Index(value = ["createdAt"]),     // Index for faster sorting/filtering by date
        Index(value = ["serviceLabel"]),  // Index for filtering by service type
        Index(value = ["providerName"])   // Index for filtering by provider
    ]
)
data class TransferEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,             // Unique identifier for each transfer (auto-generated)

    val serviceKey: String,         // Internal key for service, e.g., "bank_transfer"
    val serviceLabel: String,       // Display label for service, e.g., "Bank Transfer"
    val providerName: String,       // Name of the provider, e.g., "ABC Bank"
    val amount: Double,             // Transfer amount

    val accountOrMsisdn: String?,   // Target account or mobile number, depends on service
    val firstName: String?,         // Recipient's first name (optional)
    var lastName: String?,          // Recipient's last name (optional)
    val gender: String?,            // Gender of recipient, e.g., "M" or "F"
    val provinceState: String?,     // Province or state of recipient (optional)

    val createdAt: Long = System.currentTimeMillis()
    // Timestamp when the transfer was created, defaults to current time
)
