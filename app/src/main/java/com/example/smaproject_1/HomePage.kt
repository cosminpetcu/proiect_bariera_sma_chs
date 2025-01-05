package com.example.smaproject_1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomePage(
    onLogout: () -> Unit,
    onNavigateToMapPage: () -> Unit,
    onNavigateToSwitchPage: () -> Unit,
    onNavigateToWalletPage: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val displayName = user?.displayName ?: user?.email?.split("@")?.get(0) ?: "User"

    // State pentru dialog și lista de puncte de acces
    var showDialog by remember { mutableStateOf(false) }
    var locationName by remember { mutableStateOf("") }
    var signalCode by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val accessPoints = remember { mutableStateListOf<AccessPoint>() }

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
                            onClick = { /* Funcționalitatea butonului Play */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                        ) {
                            Text("Play", color = Color.White)
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
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Add New Access Point", color = Color.White)
            }

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Out", color = Color.White)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Butoane de navigare
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
                    onClick = { onNavigateToWalletPage() },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.wallet),
                        contentDescription = "Wallet",
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
                            accessPoints.add(AccessPoint(locationName, signalCode, address))
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

// Structură pentru punctele de acces
data class AccessPoint(val name: String, val code: String, val address: String)
