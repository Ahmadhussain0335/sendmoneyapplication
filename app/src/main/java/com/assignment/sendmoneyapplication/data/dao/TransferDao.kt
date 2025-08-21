package com.assignment.sendmoneyapplication.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.assignment.sendmoneyapplication.data.entity.TransferEntity
import kotlinx.coroutines.flow.Flow

// DAO (Data Access Object) for handling TransferEntity operations in Room database
@Dao
interface TransferDao {

    /**
     * Insert a new transfer record into the database.
     * If a record with the same primary key exists, it will be replaced.
     *
     * @param transfer The transfer entity to insert
     * @return The row ID of the inserted item
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transfer: TransferEntity): Long

    /**
     * Delete a transfer record from the database by its ID.
     *
     * @param id The unique ID of the transfer to delete
     */
    @Query("DELETE FROM transfers WHERE id = :id")
    suspend fun delete(id: Long)

    /**
     * Observe all transfers as a Flow, ordered by creation time descending.
     * This is useful for reactive UI updates in Jetpack Compose.
     *
     * @return Flow emitting the list of all transfers
     */
    @Query("SELECT * FROM transfers ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TransferEntity>>

    /**
     * Observe a distinct list of service labels from all transfers.
     * Helps populate service filter dropdowns.
     *
     * @return Flow emitting the list of distinct service labels
     */
    @Query("SELECT DISTINCT serviceLabel FROM transfers ORDER BY serviceLabel")
    fun observeDistinctServices(): Flow<List<String>>

    /**
     * Observe filtered transfers based on service label and search query.
     * - If serviceFilter is null, all services are included.
     * - If query is empty, all transfers matching the service are returned.
     * - Matches are searched in providerName and accountOrMsisdn.
     *
     * @param serviceFilter The service label to filter by, or null for all
     * @param query The search query to filter by provider name or account/MSISDN
     * @return Flow emitting the filtered list of transfers
     */
    @Query("""
        SELECT * FROM transfers
        WHERE (:serviceFilter IS NULL OR serviceLabel = :serviceFilter)
          AND (:query == '' OR providerName LIKE '%' || :query || '%' OR accountOrMsisdn LIKE '%' || :query || '%')
        ORDER BY createdAt DESC
    """)
    fun observeFiltered(serviceFilter: String?, query: String): Flow<List<TransferEntity>>
}