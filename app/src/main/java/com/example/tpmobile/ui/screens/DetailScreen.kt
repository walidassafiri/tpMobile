package com.example.tpmobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tpmobile.utils.format
import com.example.tpmobile.model.Commande

@Composable
fun DetailScreen(commande: Commande, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Détails de la commande #${commande.numero}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Poids: ${commande.poids.format(2)} kg")
        Text(text = "Volume: ${commande.volume.format(2)} m³")
        Text(text = "Prix: ${commande.prix.format(2)} €")
        Text(text = "Priorité: ${commande.priorite}")
        Text(text = "Fragilité: ${if (commande.fragile) "Oui" else "Non"}")

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Retour")
        }
    }
}