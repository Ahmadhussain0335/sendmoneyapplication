package com.assignment.sendmoneyapplication.ui.screens.sendMoney


// -------------------- Form Schema --------------------
// Represents the entire form structure loaded from JSON
data class FormSchema(
    val title: Map<String, String>, // Form title in multiple languages, e.g., {"en": "Send Money", "ar": "إرسال الأموال"}
    val services: List<Service>     // List of available services in the form
)

// -------------------- Service --------------------
// Represents a single service in the form
data class Service(
    val label: Map<String, String>, // Display name of the service in multiple languages
    val name: String,               // Unique service key/id, e.g., "bank_transfer"
    val providers: List<Provider>   // List of providers associated with this service
)

// -------------------- Provider --------------------
// Represents a provider for a service
data class Provider(
    val name: String,               // Provider display name, e.g., "ABC Bank"
    val id: String,                 // Unique provider ID
    val required_fields: List<Field> // List of fields required for this provider
)

// -------------------- Field --------------------
// Represents a single input field required by a provider
data class Field(
    val label: Map<String, String>,             // Field label in multiple languages
    val name: String,                            // Field identifier/key, e.g., "amount"
    val placeholder: Any?,                       // Placeholder text (can be String or Map<String,String>)
    val type: String,                            // Field type, e.g., "text", "number", "dropdown"
    val options: List<Option>? = null,          // Optional list of dropdown options if type is "dropdown"
    val validation: String? = null,             // Optional validation rules, e.g., "required|numeric"
    val max_length: Any? = null,                // Maximum input length (Int or Map for multiple languages)
    val validation_error_message: Any? = null   // Error message to show if validation fails (String or Map)
)

// -------------------- Option --------------------
// Represents a selectable option for dropdown fields
data class Option(
    val label: String, // Display label of the option
    val name: String   // Internal key/value for the option
)

