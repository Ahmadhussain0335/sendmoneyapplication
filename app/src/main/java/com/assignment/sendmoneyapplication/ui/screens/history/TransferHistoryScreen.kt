package com.assignment.sendmoneyapplication.ui.screens.history

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.assignment.sendmoneyapplication.R
import com.assignment.sendmoneyapplication.data.db.PreferenceManager
import com.assignment.sendmoneyapplication.data.entity.TransferEntity
import com.assignment.sendmoneyapplication.ui.screens.sendMoney.FormViewModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferHistoryScreen(
    vm: FormViewModel = viewModel(),        // ViewModel to manage data and state
    onBackClick: () -> Unit = {},           // Optional callback for back navigation
    navController: NavController            // NavController to navigate between screens
) {
    val context = LocalContext.current   // ✅ Get Context here
    // Collect state from the ViewModel
    val transfers by vm.transfers.collectAsState()           // List of saved transfers
    val services by vm.distinctServices.collectAsState()     // Distinct service names for filter
    val query by vm.query.collectAsState()                   // Search query
    val serviceFilter by vm.serviceFilter.collectAsState()   // Selected service filter

    // State for showing JSON dialog for a selected transfer
    var selectedTransfer by remember { mutableStateOf<TransferEntity?>(null) }
    var showJsonDialog by remember { mutableStateOf(false) }

    // Scaffold provides top bar and body layout
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(80.dp),
                title = {
                    Text(
                        text = stringResource(id = R.string.transaction_history),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp, top = 15.dp)
                    )
                },
                navigationIcon = {
                    // Back button in top bar
                    IconButton(
                        onClick = { navController.navigate("home") },  // Navigate to home screen
                        modifier = Modifier.padding(bottom = 2.dp, top = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            // Clear login session
                            PreferenceManager.clear(context)
                            // Navigate back to login screen
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true } // Clear back stack
                            }
                        }
                    ) {
                        Text(
                            text = "Logout",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF686F89)
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFEEF1F8))
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {

            // -------------------- Filters Section --------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search Field
                OutlinedTextField(
                    value = query,
                    onValueChange = vm::setQuery,       // Update query in ViewModel
                    modifier = Modifier
                        .weight(1f)
                        .height(65.dp),
                    label = { Text("Search") },
                    placeholder = { Text("Type to search...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color(0xFF1E88E5)
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,                  // Force single-line display
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF1E88E5),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        cursorColor = Color(0xFF1E88E5),
                        focusedLabelColor = Color(0xFF1E88E5)
                    )
                )

                // Service Filter Dropdown
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .weight(1f)
                        .height(65.dp)
                ) {
                    OutlinedTextField(
                        value = serviceFilter ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Service") },
                        placeholder = { Text("All") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1E88E5),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            cursorColor = Color(0xFF1E88E5),
                            focusedLabelColor = Color(0xFF1E88E5)
                        ),
                        modifier = Modifier.menuAnchor()
                    )

                    // Dropdown menu items
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All") },
                            onClick = {
                                vm.setServiceFilter(null)  // Reset filter
                                expanded = false
                            }
                        )
                        services.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    vm.setServiceFilter(s)  // Apply filter
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // -------------------- Transfer List --------------------
            if (transfers.isEmpty()) {
                // Show message when no transfers exist
                Text("No transfers yet.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(transfers) { t ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(6.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = Color(0xFFFFFFFF)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                // -------------------- Top Row: Service & Provider --------------------
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Money,
                                            contentDescription = "Service Icon",
                                            tint = Color(0xFF1E88E5),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${t.serviceLabel} • ${t.providerName}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF333333)
                                        )
                                    }
                                    Text(
                                        text = "ID: ${t.id}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // -------------------- Amount --------------------
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Money,
                                        contentDescription = "Amount Icon",
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Amount: ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("${t.amount}", fontSize = 14.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
                                }
                                Spacer(modifier = Modifier.height(6.dp)) // spacing between fields

                                // -------------------- Optional Fields --------------------
                                t.firstName?.takeIf { it.isNotEmpty() }?.let { firstName ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = "First Name", tint = Color(0xFFFB8C00), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("First Name: ", fontWeight = FontWeight.Bold)
                                        Text(firstName)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                t.lastName?.takeIf { it.isNotEmpty() }?.let { lastName ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = "Last Name", tint = Color(0xFFFB8C00), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Last Name: ", fontWeight = FontWeight.Bold)
                                        Text(lastName)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                t.accountOrMsisdn?.let { account ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ManageAccounts, contentDescription = "Account", tint = Color(0xFF8E24AA), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Account/MSISDN: ", fontWeight = FontWeight.Bold)
                                        Text(account)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                t.gender?.takeIf { it.isNotEmpty() }?.let { gender ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Wc, contentDescription = "Gender", tint = Color(0xFFE53935), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Gender: ", fontWeight = FontWeight.Bold)
                                        Text(if (gender == "F") "Female" else "Male")
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                t.provinceState?.takeIf { it.isNotEmpty() }?.let { province ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = "Province", tint = Color(0xFF43A047), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Province: ", fontWeight = FontWeight.Bold)
                                        Text(province)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                // -------------------- Request Date --------------------
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Request Date", tint = Color(0xFF1E88E5), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Request Date: ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(t.createdAt)), fontSize = 12.sp, color = Color.Gray)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // -------------------- Actions Row (View + Delete) --------------------
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    // View button
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF1E88E5))
                                            .clickable {
                                                selectedTransfer = t
                                                showJsonDialog = true
                                            }
                                            .padding(horizontal = 16.dp, vertical = 6.dp)
                                    ) {
                                        Text("View", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Delete button
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFE53935))
                                            .clickable {
                                                // Call delete in ViewModel
                                                vm.deleteTransfer(t.id)
                                            }
                                            .padding(horizontal = 16.dp, vertical = 6.dp)
                                    ) {
                                        Text("Delete", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        val context = LocalContext.current // Get context once

        // -------------------- JSON Dialog --------------------
        if (showJsonDialog && selectedTransfer != null) {
            val jsonString = Gson().toJson(selectedTransfer)
            AlertDialog(
                onDismissRequest = { showJsonDialog = false },
                confirmButton = {
                    TextButton(onClick = { showJsonDialog = false }) {
                        Text("Dismiss")
                    }
                },
                title = { Text("Request Details") },
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 400.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(jsonString, fontFamily = FontFamily.Monospace)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        // Share JSON via chooser
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/json"
                            putExtra(Intent.EXTRA_TEXT, jsonString)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share via"))
                    }) {
                        Text("Share")
                    }
                }
            )
        }
    }
}
