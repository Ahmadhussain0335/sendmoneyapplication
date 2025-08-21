package com.assignment.sendmoneyapplication.ui.screens.sendMoney

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.max
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.assignment.sendmoneyapplication.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMoneyScreen(
    viewModel: FormViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    navController: NavController
) {
    val schema = viewModel.schema
    val language by viewModel.currentLanguage
    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // ---------------- Selected Service & Provider ----------------
    var selectedService by remember { mutableStateOf<Service?>(null) } // Holds currently selected service
    var selectedProvider by remember { mutableStateOf<Provider?>(null) } // Holds currently selected provider
    val fieldValues = remember { mutableStateMapOf<String, String>() } // Stores user input for dynamic fields

    val fieldErrors = remember { mutableStateMapOf<String, Boolean>() } // field name -> isError

    // Initialize first service & provider when schema is loaded
    LaunchedEffect(schema) {
        if (schema.services.isNotEmpty() && selectedService == null) {
            selectedService = schema.services.first()
            selectedProvider = schema.services.first().providers.firstOrNull()

            // Pre-fill option fields with first available option
            selectedProvider?.required_fields?.forEach { field ->
                if (field.type == "option" && !field.options.isNullOrEmpty()) {
                    fieldValues[field.name] = field.options[0].name
                }
            }
        }
    }

    // Exit Confirmation Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit Application") },
            text = { Text("Are you sure you want to exit the application?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        // Exit the app
                        (context as? Activity)?.finish()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            // ---------------- Top App Bar ----------------
            CenterAlignedTopAppBar(
                modifier = Modifier.height(80.dp),
                title = {
                    Text(
                        text = stringResource(id = R.string.send_money),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp, top = 15.dp)
                    )
                },
                navigationIcon = {
                    // Back Button
                    IconButton(
                        onClick = { showExitDialog = true },
                        modifier = Modifier.padding(bottom = 2.dp, top = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF686F89))
            )
        }
    ) { padding ->

        // ---------------- Loader When Schema is Empty ----------------
        if (schema.services.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = Color(0xFF1E88E5),
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Loading services...",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFFEEF1F8))
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // ---------------- Service Dropdown ----------------
                selectedService?.let {
                    ServiceDropdown(
                        schema = schema,
                        language = language,
                        selectedService = selectedService,
                        onServiceSelected = { service ->
                            // Update selected service & reset provider/fields
                            selectedService = service
                            selectedProvider = service.providers.firstOrNull()
                            fieldValues.clear()
                            fieldErrors.clear()

                            // Pre-fill option fields for provider
                            selectedProvider?.required_fields?.forEach { field ->
                                if (field.type == "option" && !field.options.isNullOrEmpty()) {
                                    fieldValues[field.name] = field.options[0].name
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // ---------------- Provider Dropdown ----------------
                selectedProvider?.let { provider ->
                    ProviderDropdown(
                        service = selectedService!!,
                        selectedProvider = provider,
                        onProviderSelected = { newProvider ->
                            // Update selected provider & reset field values
                            selectedProvider = newProvider
                            fieldValues.clear()
                            fieldErrors.clear()

                            // Pre-fill option fields for new provider
                            newProvider.required_fields.forEach { field ->
                                if (field.type == "option" && !field.options.isNullOrEmpty()) {
                                    fieldValues[field.name] = field.options[0].name
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // ---------------- Dynamic Fields ----------------
                selectedProvider?.required_fields?.forEach { field ->
                    // Render dynamic input fields (text, number, dropdown etc.)
                    DynamicField(field, fieldValues, fieldErrors, language)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // ---------------- Language Switch ----------------
                LanguageToggleSwitch(
                    language = language,
                    onLanguageChange = { newLang -> viewModel.setLanguage(newLang) },
                    navController
                )
                Spacer(modifier = Modifier.height(10.dp))

                val transferVM: FormViewModel = viewModel()
                val scope = rememberCoroutineScope()

                // ---------------- Send Button ----------------
                SendButton(
                    selectedService = selectedService,
                    selectedProvider = selectedProvider,
                    fieldValues = fieldValues,
                    language = language,
                    transferVM = transferVM
                )

                Spacer(modifier = Modifier.height(1.dp))
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendButton(
    selectedService: Service?,
    selectedProvider: Provider?,
    fieldValues: Map<String, String>,
    language: String,
    transferVM: FormViewModel
) {
    // UI states for showing error/success dialogs
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                // ---------------- Validation ----------------
                val errors = mutableListOf<String>()

                // Check required fields for provider
                selectedProvider?.required_fields?.forEach { field ->
                    val value = fieldValues[field.name].orEmpty()
                    val isEmpty = value.isBlank()
                    val validatorRegex = field.validation?.toRegex()

                    if (isEmpty) {
                        // Pick localized error message (fallback to English or default)
                        val msg = when (field.validation_error_message) {
                            is Map<*, *> -> (field.validation_error_message as Map<String, String>)[language]
                                ?: (field.validation_error_message as Map<String, String>)["en"]
                                ?: "This field is required"
                            is String -> field.validation_error_message
                            else -> "This field is required"
                        }
                        errors.add(msg ?: "This field is required")
                    }

                    // Check regex match if not empty
                    else if (validatorRegex != null && !validatorRegex.matches(value)) {
                        val msg = "Invalid input for ${getLabel(field.label, language)}"
                        errors.add(msg)
                    }
                }

                // If validation fails → show error modal
                if (errors.isNotEmpty()) {
                    errorMessage = errors.joinToString("\n")
                    showError = true
                    return@Button
                }

                // ---------------- Save Transfer ----------------
                val serviceKey = selectedService?.name ?: ""
                val serviceLabel = selectedService?.label?.get(language)
                    ?: selectedService?.label?.get("en")
                    ?: selectedService?.name.orEmpty()
                val providerName = selectedProvider?.name ?: ""

                // Save form data in ViewModel (could persist to DB)
                transferVM.saveTransferFromForm(
                    serviceKey = serviceKey,
                    serviceLabel = serviceLabel,
                    providerName = providerName,
                    fieldValues = fieldValues
                )

                // Show success dialog if saved correctly
                showSuccess = true
            },
            modifier = Modifier
                .width(170.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "SEND", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }

    // ---------------- Error Bottom Sheet ----------------
    if (showError && errorMessage.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = { showError = false },
            containerColor = Color(0xFF686F89),
            tonalElevation = 6.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Error icon
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error Icon",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Text(
                    text = "Error",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Error details
                Text(
                    text = errorMessage,
                    fontSize = 14.sp,
                    color = Color(0xFFEFEFEF),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Dismiss button
                Button(
                    onClick = { showError = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Dismiss", color = Color(0xFF686F89), fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // ---------------- Success Dialog ----------------
    if (showSuccess) {
        SuccessDialog(
            serviceLabel = selectedService?.label?.get(language)
                ?: selectedService?.label?.get("en")
                ?: selectedService?.name.orEmpty(),
            providerName = selectedProvider?.name ?: "",
            amount = fieldValues["amount"]?.toDoubleOrNull() ?: 0.0,
            onDismiss = { showSuccess = false }
        )
    }
}

@Composable
fun SuccessDialog(
    onDismiss: () -> Unit,
    serviceLabel: String,
    providerName: String,
    amount: Double
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)), // Green background
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success icon
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success Icon",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "Success!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Success message (includes amount, service, provider)
                Text(
                    text = "Your request of ${"%.2f".format(amount)} for $serviceLabel ($providerName) has been sent successfully.",
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                // OK button to dismiss
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "OK",
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}



// ---------------- Language Switch ----------------
@Composable
fun LanguageToggleSwitch(
    language: String, // Current selected language ("en" or "ar")
    onLanguageChange: (String) -> Unit, // Callback when language changes
    navController: NavController // NavController for navigation
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp), // Padding for spacing
        horizontalArrangement = Arrangement.SpaceBetween, // Spread items: left (History), right (Toggle)
        verticalAlignment = Alignment.CenterVertically // Center items vertically
    ) {
        // ---------- Left Side: "History" clickable text ----------
        Text(
            text = "History", // Display label
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold, // Bold font
                color = Color(0xFF1E88E5), // Blue color
                textDecoration = TextDecoration.Underline // Underline for link style
            ),
            modifier = Modifier.clickable {
                // Navigate to the history screen when clicked
                navController.navigate("history")
            }
        )

        // ---------- Right Side: Language Text + Switch ----------
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Show language name based on current value
            Text(
                text = if (language == "en") "English" else "Arabic",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 8.dp) // Space between text and switch
            )

            // Toggle Switch for switching language
            Switch(
                checked = (language == "ar"), // Checked = Arabic selected
                onCheckedChange = { isChecked ->
                    if (isChecked) {
                        // Switch turned ON → Arabic
                        onLanguageChange("ar")
                    } else {
                        // Switch turned OFF → English
                        onLanguageChange("en")
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary, // Thumb when ON
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface, // Thumb when OFF
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), // Track ON
                    uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) // Track OFF
                ),
                modifier = Modifier.scale(0.7f) // Reduce switch size (70%)
            )
        }
    }
}



// ---------------- Service Dropdown ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDropdown(
    schema: FormSchema,                 // Full schema with services
    language: String,                   // Current language ("en", "ar", etc.)
    selectedService: Service?,          // Currently selected service (nullable)
    onServiceSelected: (Service) -> Unit // Callback when a service is chosen
) {
    var expanded by remember { mutableStateOf(false) } // Track whether dropdown is open
    val items = schema.services.map { it.label[language] ?: it.label["en"] ?: "" } // Labels in chosen language
    val selectedText = selectedService?.let { it.label[language] ?: it.label["en"] } ?: "" // Current selection

    Column(modifier = Modifier.fillMaxWidth()) {
        // Label above the input field
        Text(
            text = "Service",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Wrapper around TextField + dropdown menu
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            // Display selected service inside text field
            TextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true, // User cannot type manually
                trailingIcon = {
                    // Toggle arrow icon based on expanded state
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White, // Background color
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .menuAnchor() // Anchor for dropdown
                    .fillMaxWidth()
                    .height(50.dp) // Increase height
                    .border(
                        1.dp,
                        Color(0xFFEBEDF2),
                        shape = RoundedCornerShape(4.dp)
                    ) // Light gray border
                    .background(Color.White, shape = RoundedCornerShape(4.dp))
            )
            // Dropdown menu with all service options
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                items.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            onServiceSelected(schema.services[index]) // Send selected service
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}



// ---------------- Provider Dropdown ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDropdown(
    service: Service,                     // Current service (with list of providers)
    selectedProvider: Provider?,          // Currently selected provider
    onProviderSelected: (Provider) -> Unit // Callback when a provider is chosen
) {
    var expanded by remember { mutableStateOf(false) }
    val items = service.providers.map { it.name } // List provider names
    val selectedText = selectedProvider?.name ?: "" // Current selection

    Column(modifier = Modifier.fillMaxWidth()) {
        // Label above the input field
        Text(
            text = "Provider",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            // Text field showing currently selected provider
            TextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(1.dp, Color(0xFFEBEDF2), shape = RoundedCornerShape(4.dp))
                    .background(Color.White, shape = RoundedCornerShape(4.dp))
            )

            // Dropdown list of providers
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                items.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            onProviderSelected(service.providers[index]) // Send selected provider
                            expanded = false
                        },
                        modifier = Modifier.background(Color.White)
                    )
                }
            }
        }
    }
}



// ---------------- Dynamic Field ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicField(field: Field, fieldValues: MutableMap<String, String>, fieldErrors: MutableMap<String, Boolean>, language: String) {
    when (field.type) {
        // Text input (plain, number, or msisdn)
        "text", "number", "msisdn" -> {
            val maxLength = field.max_length.toSafeInt() // Max input length
            val validatorRegex = field.validation?.toRegex() // Optional regex validator

            val isError = fieldErrors[field.name] ?: false
            var inputValue by remember { mutableStateOf(fieldValues[field.name] ?: "") }
            var isValid by remember { mutableStateOf(true) }

            Column(modifier = Modifier.fillMaxWidth()) {
                // Field label above input
                Text(
                    text = getLabel(field.label, language),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                TextField(
                    value = fieldValues[field.name] ?: "", // Current input value
                    onValueChange = { input ->

                        // ---------------- Option 1: Without Validator ----------------
                        // ⚠️ Use this if you don’t need validation or length checks
                        // Stores raw input directly into fieldValues
                        // -------------------------------------------------------------
                        //fieldValues[field.name] = input


                        // ---------------- Option 2: With Validator -------------------
                        // ✅ Use this if you want to enforce max length and apply regex validation
                        // Steps:
                        // 1. Enforce maximum length (trim input if longer than allowed)
                        // 2. Validate input against regex (if validatorRegex is provided)
                        // 3. Save only if input is valid
                        // -------------------------------------------------------------

                        // Limit input to maxLength
                        val trimmed = input.take(maxLength)
                        inputValue = trimmed

                        // Validate input
                        isValid = validatorRegex?.matches(trimmed) ?: true

                        // Save current value
                        fieldValues[field.name] = trimmed

                        // Update fieldValues only if needed
                        fieldErrors[field.name] = !isValid
                    },
                    placeholder = {
                        Text(
                            getLabel(field.placeholder, language), // Placeholder text
                            style = LocalTextStyle.current.copy(
                                color = Color.Gray,
                                textAlign = TextAlign.Start,
                                lineHeight = 20.sp
                            )
                        )
                    },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Start,
                        lineHeight = 20.sp
                    ),
                    trailingIcon = {
                        if (inputValue.isNotEmpty()) {
                            if (!isValid) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Invalid",
                                    tint = Color.Red,
                                )
                            }
                        }
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (field.type == "number" || field.type == "msisdn")
                            KeyboardType.Number else KeyboardType.Text
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.White, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 1.dp)
                )

                // Show error message below TextField
                if (isError) {
                    Text(
                        text = "Invalid input. Please follow the correct format.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }

        // Dropdown option field
        "option" -> {
            var expanded by rememberSaveable(field.name) { mutableStateOf(false) }

            // Default to first option if none selected yet
            val selectedValue = fieldValues.getOrPut(field.name) {
                field.options?.firstOrNull()?.name ?: ""
            }

            // Show correct label in chosen language
            val displayLabel = field.options?.firstOrNull { it.name == selectedValue }
                ?.let { getLabel(it.label, language) } ?: ""

            Column(modifier = Modifier.fillMaxWidth()) {
                // Label above dropdown
                Text(
                    text = getLabel(field.label, language),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Text field with selected option
                    TextField(
                        value = displayLabel,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(1.dp, Color(0xFFEBEDF2), shape = RoundedCornerShape(4.dp))
                            .background(Color.White, shape = RoundedCornerShape(4.dp))
                    )
                    // Dropdown list of options
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        field.options?.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(getLabel(option.label, language)) },
                                onClick = {
                                    fieldValues[field.name] = option.name // Save selected option
                                    expanded = false
                                },
                                modifier = Modifier.background(Color.White)
                            )
                        }
                    }
                }
            }
        }
    }
}


// ---------------- Helper to get label ----------------
fun getLabel(label: Any?, language: String): String {
    return when (label) {
        is Map<*, *> -> (label as? Map<String, String>)?.get(language)
            ?: (label as? Map<String, String>)?.get("en") ?: "" // fallback to English
        is String -> label
        else -> ""
    }
}

fun Any?.toSafeInt(default: Int = 0): Int {
    return when (this) {
        is Int -> this
        is Double -> this.toInt()
        is Float -> this.toInt()
        is String -> this.toIntOrNull() ?: default
        else -> default
    }
}


