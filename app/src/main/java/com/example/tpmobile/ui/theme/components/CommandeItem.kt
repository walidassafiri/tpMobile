package com.example.tpmobile.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tpmobile.format
import com.example.tpmobile.model.Commande

@Composable
fun CommandeItem(commande: Commande, estAffectee: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
            .background(if (estAffectee) Color.LightGray else Color.Transparent)
    ) {
        Text(
            text = "Commande #${commande.numero}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (estAffectee) Color.Gray else Color.Unspecified
        )
        Text(text = "Poids: ${commande.poids.format(2)} kg")
        Text(text = "Volume: ${commande.volume.format(2)} m³")
        Text(text = "Prix: ${commande.prix.format(2)} €")
        if (estAffectee) {
            Text(
                text = "Déjà affectée à un conteneur",
                color = Color.Red,
                fontSize = 14.sp
            )
        }
    }
}