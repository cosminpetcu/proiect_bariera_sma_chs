package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Utilizarea stării în cadrul setContent
            var showAccessPointScreen by remember { mutableStateOf(false) }

            if (showAccessPointScreen) {
                // Afișează AccessPointScreen
                HomePage()
            } else {
                // Afișează AccessHubScreen
               SignUpPage(onNavigateToHomePage = { showAccessPointScreen = true })
            }
        }
    }
}
