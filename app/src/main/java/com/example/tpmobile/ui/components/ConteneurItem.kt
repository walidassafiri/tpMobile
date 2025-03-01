package com.example.tpmobile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tpmobile.utils.format
import com.example.tpmobile.model.Commande
import com.example.tpmobile.model.Conteneur
import com.example.tpmobile.business.tauxUtilisationConteneur

@Composable
fun ConteneurItem(conteneur: Conteneur, commandes: List<Commande>, onClick: () -> Unit) {
    val (tauxVolume, tauxPoids) = tauxUtilisationConteneur(conteneur, commandes)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Text(
            text = "Conteneur #${conteneur.id}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = "Poids max: ${conteneur.poidsMax.format(2)} kg")
        Text(text = "Volume max: ${conteneur.volumeMax.format(2)} m³")
        Text(text = "Nombre de commandes: ${commandes.size}")
        Text(text = "Prix total: ${commandes.sumOf { it.prix }.format(2)} €")
        Text(text = "Volume utilisé: %.2f%%".format(tauxVolume))
        Text(text = "Poids utilisé: %.2f%%".format(tauxPoids))
    }
}