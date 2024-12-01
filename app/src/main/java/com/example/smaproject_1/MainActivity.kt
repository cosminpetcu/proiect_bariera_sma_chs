package com.example.smaproject_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

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
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            // Verificăm dacă utilizatorul este deja autentificat
            if (currentUser != null) {
                // Dacă utilizatorul este deja autentificat, redirecționăm direct la Home
                currentScreen = "home"
            }

            when (currentScreen) {
                "signUp" -> SignUpPage(onNavigateToHomePage = { currentScreen = "home" })
                "home" -> HomePage(
                    onNavigateToMapPage = { currentScreen = "map" },
                    onNavigateToSwitchPage = { currentScreen = "switch" },
                    onNavigateToWalletPage = { currentScreen = "wallet" },
                    onLogout = {
                        // Deconectează utilizatorul și redirecționează-l pe pagina de logare
                        auth.signOut()
                        currentScreen = "signUp"
                    }
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
