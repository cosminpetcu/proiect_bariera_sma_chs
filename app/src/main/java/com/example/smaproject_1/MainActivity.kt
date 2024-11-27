package com.example.smaproject_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

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
