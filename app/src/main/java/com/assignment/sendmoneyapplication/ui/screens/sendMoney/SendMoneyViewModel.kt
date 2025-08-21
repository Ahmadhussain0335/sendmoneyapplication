package com.assignment.sendmoneyapplication.ui.screens.sendMoney

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.assignment.sendmoneyapplication.data.db.AppDatabase
import com.assignment.sendmoneyapplication.data.entity.TransferEntity
import com.assignment.sendmoneyapplication.repository.TransferRepository
import kotlinx.coroutines.flow.*

/**
 * ViewModel for handling form schema, language state, filters, and transfer persistence.
 */
class FormViewModel(application: Application) : AndroidViewModel(application) {

    // Current language for labels (default: "en")
    var currentLanguage = mutableStateOf("en")
        private set

    // Holds the loaded form schema (title + services list)
    var schema by mutableStateOf<FormSchema>(
        FormSchema(title = mapOf("en" to "", "ar" to ""), services = emptyList())
    )

    // Repository & DAO for persisting transfer data
    private val dao = AppDatabase.get(application).transferDao()
    private val repo = TransferRepository(dao)

    // --- FILTERS & SEARCH ---
    // Service filter (nullable, can be null to show all)
    private val _serviceFilter = MutableStateFlow<String?>(null)
    val serviceFilter: StateFlow<String?> = _serviceFilter.asStateFlow()

    // Search query for filtering transfers
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // Distinct service names from the DB
    val distinctServices: StateFlow<List<String>> =
        repo.observeDistinctServices()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // List of transfers matching current filter & search query
    val transfers: StateFlow<List<TransferEntity>> =
        combine(_serviceFilter, _query) { s, q -> s to q }
            .flatMapLatest { (s, q) -> repo.observeFiltered(s, q) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())


    init {
        // Load schema JSON file on ViewModel initialization
        loadSchema()
    }

    /**
     * Load schema in background (IO thread) from assets JSON.
     */
    private fun loadSchema() {
        viewModelScope.launch(Dispatchers.IO) {
            val loadedSchema = loadFormSchema(getApplication())
            loadedSchema?.let {
                schema = it
            }
        }
    }

    /**
     * Reads `send_money.json` from assets and parses it into FormSchema using Gson.
     */
    private fun loadFormSchema(context: Context): FormSchema? {
        return try {
            val jsonString = context.assets.open("send_money.json")
                .bufferedReader()
                .use { it.readText() }
            Gson().fromJson(jsonString, FormSchema::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // --- STATE UPDATERS ---
    fun setLanguage(lang: String) {
        currentLanguage.value = lang
    }

    fun setServiceFilter(filter: String?) {
        _serviceFilter.value = filter
    }

    fun setQuery(q: String) {
        _query.value = q
    }

    /**
     * Saves a new TransferEntity into the database from form field values.
     *
     * @param serviceKey Internal identifier for the service
     * @param serviceLabel Display label for the service (localized)
     * @param providerName Provider handling the transfer
     * @param fieldValues Map of form input values keyed by field name
     */
    fun saveTransferFromForm(
        serviceKey: String,
        serviceLabel: String,
        providerName: String,
        fieldValues: Map<String, String>
    ) {
        viewModelScope.launch {
            // Extract fields from the form
            val amount = fieldValues["amount"]?.toDoubleOrNull() ?: 0.0
            val account = fieldValues["bank_account_number"] ?: fieldValues["msisdn"]
            val gender = fieldValues["gender"]
            val province = fieldValues["province_state"]
            val lastname = fieldValues["lastname"]
            val firstname = fieldValues["firstname"]

            // Create entity for database
            val entity = TransferEntity(
                serviceKey = serviceKey,
                serviceLabel = serviceLabel,
                providerName = providerName,
                amount = amount,
                accountOrMsisdn = account,
                firstName = firstname,
                lastName = lastname,
                gender = gender,
                provinceState = province
            )

            // Save in DB
            repo.add(entity)
        }
    }

    /**
     * Deletes a transfer from the database.
     */
    fun deleteTransfer(id: Long) {
        viewModelScope.launch {
            repo.delete(id)
        }
    }
}
