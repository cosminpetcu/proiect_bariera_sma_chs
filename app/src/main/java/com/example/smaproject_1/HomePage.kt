package com.example.smaproject_1

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun HomePage(
    accessPoints: List<AccessPoint>,
    onNavigateToMapPage: () -> Unit,
    onNavigateToSwitchPage: () -> Unit,
    onNavigateToSettingsPage: () -> Unit,
    onAddAccessPoint: (AccessPoint) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val displayName = user?.displayName ?: user?.email?.split("@")?.get(0) ?: "User"

    // Bluetooth setup
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter
    var bluetoothSocket by remember { mutableStateOf<BluetoothSocket?>(null) }
    var isConnected by remember { mutableStateOf(false) }

    // ESP32's UUID - you'll need to replace this with your ESP32's UUID
    val ESP32_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // State pentru dialog și lista de puncte de acces
    var showDialog by remember { mutableStateOf(false) }
    var locationName by remember { mutableStateOf("") }
    var signalCode by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            connectToESP32(
                bluetoothAdapter,
                ESP32_UUID,
                context,
                { socket -> bluetoothSocket = socket; isConnected = true },
                { isConnected = false }
            )
        } else {
            showToast(context, "Bluetooth permission is required.")
        }
    }

    // Check permission and connect
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            connectToESP32(
                bluetoothAdapter,
                ESP32_UUID,
                context,
                { socket -> bluetoothSocket = socket; isConnected = true },
                { isConnected = false }
            )
        }
    }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titlu și mesaj de bun venit
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "AccessHub",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Hi, $displayName",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }

        // Lista punctelor de acces
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(accessPoints.size) { index ->
                val point = accessPoints[index]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = point.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = point.address,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Button(
                            onClick = {
                                if (!isConnected) {
                                    connectToESP32(
                                        bluetoothAdapter,
                                        ESP32_UUID,
                                        context,
                                        { socket -> bluetoothSocket = socket; isConnected = true },
                                        { isConnected = false }
                                    )
                                } else {
                                    scope.launch {
                                        sendSignalToESP32(bluetoothSocket, point.code)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isConnected) Color.Green else Color.Gray
                            )
                        ) {
                            Text(
                                text = if (isConnected) "Play" else "Connect",
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }

        // Butoane de jos
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Add New Access Point", color = Color.White)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Navigation buttons
                IconButton(
                    onClick = { /* Rămâne pe pagina curentă */ },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.home),
                        contentDescription = "Home",
                        tint = Color.Black,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                IconButton(
                    onClick = { onNavigateToMapPage() },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.map),
                        contentDescription = "Map",
                        tint = Color.LightGray,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                IconButton(
                    onClick = { onNavigateToSwitchPage() },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.sageti),
                        contentDescription = "Switch",
                        tint = Color.LightGray,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                IconButton(
                    onClick = { onNavigateToSettingsPage() },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings),
                        contentDescription = "Settings",
                        tint = Color.LightGray,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Dialog pentru adăugarea unui punct de acces
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add Access Point") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = locationName,
                            onValueChange = { locationName = it },
                            label = { Text("Location Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = signalCode,
                            onValueChange = { signalCode = it },
                            label = { Text("Signal Code") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onAddAccessPoint(AccessPoint(locationName, signalCode, address))
                            showDialog = false
                            locationName = ""
                            signalCode = ""
                            address = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Add", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                }
            )
        }
    }
}

// Helper functions for Bluetooth operations
@SuppressLint("MissingPermission")
private fun connectToESP32(
    bluetoothAdapter: BluetoothAdapter?,
    uuid: UUID,
    context: Context,
    onSuccess: (BluetoothSocket) -> Unit,
    onFailure: () -> Unit
) {
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
        showToast(context, "Bluetooth is disabled.")
        onFailure()
        return
    }

    val espDevice = bluetoothAdapter.bondedDevices.find { it.name.contains("ESP32") }
    if (espDevice == null) {
        showToast(context, "ESP32 device not found. Pair it first.")
        onFailure()
        return
    }

    try {
        val socket = espDevice.createRfcommSocketToServiceRecord(uuid)
        socket.connect()
        showToast(context, "Connected to ESP32.")
        onSuccess(socket)
    } catch (e: Exception) {
        showToast(context, "Failed to connect: ${e.message}")
        onFailure()
    }
}

private fun sendSignalToESP32(socket: BluetoothSocket?, message: String) {
    try {
        socket?.outputStream?.write(message.toByteArray())
    } catch (e: Exception) {
        // Handle error
    }
}

private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

// Data class for access points
data class AccessPoint(val name: String, val code: String, val address: String)