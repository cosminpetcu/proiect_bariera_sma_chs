package com.example.smaproject_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

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
            var currentScreen by remember { mutableStateOf("signUp") }

            when (currentScreen) {
                "signUp" -> SignUpPage(onNavigateToHomePage = { currentScreen = "home" })
                "home" -> HomePage(
                    onNavigateToMapPage = { currentScreen = "map" },
                    onNavigateToSwitchPage = { currentScreen = "switch" },
                    onNavigateToWalletPage = { currentScreen = "wallet" }
                )
                "map" -> MapPage(
                    onNavigateBack = { currentScreen = "home" },
                    onNavigateToSwitchPage = { currentScreen = "switch" },
                    onNavigateToWalletPage = { currentScreen = "wallet" }
                )
                "switch" -> SwitchPage(
                    onNavigateBack = { currentScreen = "home" },
                    onNavigateToMapPage = { currentScreen = "map" },
                    onNavigateToWalletPage = { currentScreen = "wallet" }
                )
                "wallet" -> WalletPage(
                    onNavigateBack = { currentScreen = "home" },
                    onNavigateToMapPage = { currentScreen = "map" },
                    onNavigateToSwitchPage = { currentScreen = "switch" }
                )
            }
        }
    }
}
