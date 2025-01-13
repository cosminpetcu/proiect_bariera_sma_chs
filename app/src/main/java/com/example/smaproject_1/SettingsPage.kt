package com.example.smaproject_1

import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.launch

@Composable
fun SettingsPage(
    accessPoints: List<AccessPoint>, // Receive the list of access points
    onNavigateBack: () -> Unit,
    onNavigateToSwitchPage: () -> Unit,
    onNavigateToMapPage: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccessPoint: (AccessPoint) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        // Display access points with settings
        Spacer(modifier = Modifier.height(16.dp))
        accessPoints.forEach { accessPoint ->
            AccessPointSettings(accessPoint, onDeleteAccessPoint)
        }
    }

    // Navigation buttons at the bottom
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Log-out button
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Text("Log Out", color = Color.White)
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            // Navigation buttons
            IconButton(
                onClick = { onNavigateBack() },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "Home",
                    tint = Color.LightGray,
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
                onClick = {},
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
}

@Composable
fun AccessPointSettings(accessPoint: AccessPoint, onDeleteAccessPoint: (AccessPoint) -> Unit) {

    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = accessPoint.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = accessPoint.address,
                fontSize = 14.sp,
                color = Color.Gray
            )

            // Auto-Opener switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Auto-Opener", fontSize = 16.sp)
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = false, // Initial state
                    onCheckedChange = { /* Add functionality here */ },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Green,
                        uncheckedThumbColor = Color.Gray
                    )
                )
            }

            //Delete button
            Button(
                onClick = {
                    scope.launch {
                        onDeleteAccessPoint(accessPoint)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Delete", color = Color.White)
            }
        }
    }
}