package com.example.tpmobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tpmobile.model.Conteneur
import com.example.tpmobile.ui.components.ConteneurConfigItem

@Composable
fun ConfigurerConteneursScreen(
    conteneurs: List<Conteneur>,
    onAjouterConteneur: (Conteneur) -> Unit,
    onSupprimerConteneur: (Conteneur) -> Unit,
    navController: NavController
) {
    var poidsMax by remember { mutableStateOf("") }
    var volumeMax by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Configurer les conteneurs",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Formulaire pour ajouter un conteneur
        OutlinedTextField(
            value = poidsMax,
            onValueChange = { poidsMax = it },
            label = { Text("Poids max (kg)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = volumeMax,
            onValueChange = { volumeMax = it },
            label = { Text("Volume max (m³)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                val poids = poidsMax.toDoubleOrNull() ?: 0.0
                val volume = volumeMax.toDoubleOrNull() ?: 0.0
                if (poids > 0 && volume > 0) {
                    val nouveauConteneur = Conteneur(
                        id = conteneurs.size + 1,
                        poidsMax = poids,
                        volumeMax = volume
                    )
                    onAjouterConteneur(nouveauConteneur)
                    poidsMax = ""
                    volumeMax = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ajouter un conteneur")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Liste des conteneurs configurés
        Text(
            text = "Conteneurs configurés :",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn {
            items(conteneurs) { conteneur ->
                ConteneurConfigItem(
                    conteneur = conteneur,
                    onSupprimer = { onSupprimerConteneur(conteneur) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 100.dp)
        ) {
            Text("Retour")
        }
    }
}
