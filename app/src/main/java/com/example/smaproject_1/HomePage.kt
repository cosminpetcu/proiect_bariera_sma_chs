package com.example.smaproject_1

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomePage(
    onNavigateToMapPage: () -> Unit,
    onNavigateToSwitchPage: () -> Unit,
    onNavigateToWalletPage: () -> Unit
) { // Adăugat parametrul pentru navigare
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top, // Modificăm pentru a nu folosi SpaceBetween
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titlu sus
        Text(
            text = "AccessHub",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Buton jos
        Button(
            onClick = { /* Aici poți adăuga acțiunea pentru adăugarea unui punct de acces */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Add New Access Point", color = Color.White)
        }

        Spacer(modifier = Modifier.height(50.dp))

        // Row pentru butoanele icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Buton Home - Gri
            IconButton(
                onClick = { /* Rămâne pe pagina curentă */ },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.home), // Imaginea pentru Home
                    contentDescription = "Home",
                    tint = Color.Black,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Buton Map
            IconButton(
                onClick = { onNavigateToMapPage() }, // Navighează la MapPage
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.map), // Imaginea pentru Map
                    contentDescription = "Map",
                    tint = Color.LightGray, // Deschis pentru celelalte butoane
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Buton Switch
            IconButton(
                onClick = { onNavigateToSwitchPage() },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.sageti), // Imaginea pentru Switch
                    contentDescription = "Switch",
                    tint = Color.LightGray, // Deschis pentru celelalte butoane
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Buton Wallet
            IconButton(
                onClick = { onNavigateToWalletPage()},
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.wallet), // Imaginea pentru Wallet
                    contentDescription = "Wallet",
                    tint = Color.LightGray, // Deschis pentru celelalte butoane
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

