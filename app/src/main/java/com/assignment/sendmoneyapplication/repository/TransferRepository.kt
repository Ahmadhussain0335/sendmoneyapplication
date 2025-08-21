package com.assignment.sendmoneyapplication.repository

import com.assignment.sendmoneyapplication.data.dao.TransferDao
import com.assignment.sendmoneyapplication.data.entity.TransferEntity

// Repository class to manage data operations for transfers.
// Acts as a single source of truth between ViewModel and Room database (DAO).
class TransferRepository(private val dao: TransferDao) {

    // Inserts a new transfer into the database.
    // Returns the row ID of the inserted transfer.
    suspend fun add(transfer: TransferEntity) = dao.insert(transfer)

    // Deletes a transfer from the database by its ID.
    suspend fun delete(id: Long) = dao.delete(id)

    // Returns a Flow of all transfer records, ordered by creation date (descending).
    // Useful for observing the list of transfers in real-time.
    fun observeAll() = dao.observeAll()

    // Returns a Flow of filtered transfers based on service and search query.
    // service: Optional filter by service label.
    // query: Optional search term for provider name or account/MSISDN.
    fun observeFiltered(service: String?, query: String) = dao.observeFiltered(service, query)

    // Returns a Flow of distinct service labels from the transfers.
    // Useful for populating filter dropdowns.
    fun observeDistinctServices() = dao.observeDistinctServices()
}
