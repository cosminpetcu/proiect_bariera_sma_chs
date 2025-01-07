package com.example.smaproject_1

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition

@SuppressLint("MissingPermission")
@Composable
fun MapPage(
    onNavigateBack: () -> Unit,
    onNavigateToSwitchPage: () -> Unit,
    onNavigateToSettingsPage: () -> Unit
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    // Solicită permisiunea pentru locație și obține locația curentă
    RequestLocationPermission {
        getCurrentLocation(context) { latLng ->
            currentLocation = latLng
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Adaugă marker la locația curentă dacă există
            currentLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Locația mea",
                    snippet = "Aceasta este locația ta curentă"
                )
            }
        }

        // Butoanele din partea de jos
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
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
                    onClick = {},
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.map),
                        contentDescription = "Map",
                        tint = Color.Black,
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
                // Buton Settings
                IconButton(
                    onClick = { onNavigateToSettingsPage() },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings), // Imaginea pentru Settings
                        contentDescription = "Settings",
                        tint = Color.LightGray, // Deschis pentru celelalte butoane
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
