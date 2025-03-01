package com.example.tpmobile.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tpmobile.format
import com.example.tpmobile.model.Commande
import com.example.tpmobile.model.Conteneur
import com.example.tpmobile.ui.components.CommandeItem

@Composable
fun DetailConteneurScreen(conteneur: Conteneur, commandes: List<Commande>, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        item {
            Text(
                text = "Détails du conteneur #${conteneur.id}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Poids max: ${conteneur.poidsMax.format(2)} kg")
            Text(text = "Volume max: ${conteneur.volumeMax.format(2)} m³")
            Text(text = "Nombre de commandes: ${commandes.size}")
            Text(text = "Prix total: ${commandes.sumOf { it.prix }.format(2)} €")
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Commandes dans ce conteneur :",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Liste des commandes
        items(commandes) { commande ->
            CommandeItem(commande = commande, estAffectee = true, onClick = {})
        }

        // Bouton "Retour"
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 100.dp) // Centrer le bouton
            ) {
                Text("Retour")
            }
        }
    }
}