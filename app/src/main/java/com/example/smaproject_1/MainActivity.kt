package com.example.smaproject_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RequestLocationPermission(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            Toast.makeText(context, "Location permission is required to show your position.", Toast.LENGTH_LONG).show()
        }
    }

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED
    ) {
        onPermissionGranted()
    } else {
        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            var currentScreen by remember { mutableStateOf("signUp") }
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            var accessPoints by remember { mutableStateOf(listOf<AccessPoint>()) }
            var isLoading by remember { mutableStateOf(true) }

            // Fetch access points for the logged-in user
            fun fetchAccessPointsForUser(userId: String) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitInstance.apiService.getAccessPoints(userId)
                        if (response.isSuccessful) {
                            accessPoints = response.body() ?: listOf()
                        } else {
                            accessPoints = listOf() // Clear if error
                        }
                    } catch (e: Exception) {
                        accessPoints = listOf() // Handle errors
                    } finally {
                        isLoading = false
                    }
                }
            }

            // Check if the user is already authenticated
            if (currentUser != null) {
                currentScreen = "home"
                fetchAccessPointsForUser(currentUser.uid)
            }

            when (currentScreen) {
                "signUp" -> SignUpPage(onNavigateToHomePage = {
                        currentScreen = "home"
                    if (currentUser != null) {
                        fetchAccessPointsForUser(currentUser.uid)
                    }
                })
                "map" -> MapPage(
                    onNavigateBack = { currentScreen = "home" },
                    onNavigateToSwitchPage = { currentScreen = "switch" },
                    onNavigateToSettingsPage = { currentScreen = "settings" }
                )
                "home" -> HomePage(
                    onNavigateToMapPage = { currentScreen = "map" },
                    onNavigateToSwitchPage = { currentScreen = "switch" },
                    onNavigateToSettingsPage = { currentScreen = "settings" },
                    onAddAccessPoint = { point ->
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response =
                                    RetrofitInstance.apiService.addAccessPoint(point)
                                if (response.isSuccessful) {
                                    accessPoints = accessPoints + point
                                }
                            } catch (e: Exception) {
                                Log.e("SMA_Project_1", "Eroare la adăugarea punctului de acces: ${e.message}", e)
                            }
                        }
                    },
                    accessPoints = accessPoints
                )
                "switch" -> SwitchPage(
                    onNavigateBack = { currentScreen = "home" },
                    onNavigateToMapPage = { currentScreen = "map" },
                    onNavigateToSettingsPage = { currentScreen = "settings" }
                )
                "settings" -> SettingsPage(
                    onNavigateBack = { currentScreen = "home" },
                    onNavigateToMapPage = { currentScreen = "map" },
                    onNavigateToSwitchPage = { currentScreen = "switch" },
                    accessPoints = accessPoints,
                    onLogout = {
                        auth.signOut()
                        currentScreen = "signUp"
                    },
                    onDeleteAccessPoint = { accessPoint ->
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response =
                                    RetrofitInstance.apiService.deleteAccessPoint(accessPoint.pointId)
                                if (response.isSuccessful) {
                                    accessPoints =
                                        accessPoints.filter { it.pointId != accessPoint.pointId }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Couldn't delete access point.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }
        }
    }
}
