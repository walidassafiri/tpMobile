package com.example.tpmobile.ui.theme.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tpmobile.format
import com.example.tpmobile.model.Conteneur

@Composable
fun ConteneurConfigItem(conteneur: Conteneur, onSupprimer: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Conteneur #${conteneur.id}", fontWeight = FontWeight.Bold)
            Text(text = "Poids max: ${conteneur.poidsMax.format(2)} kg")
            Text(text = "Volume max: ${conteneur.volumeMax.format(2)} m³")
        }
        IconButton(onClick = onSupprimer) {
            Icon(Icons.Default.Delete, contentDescription = "Supprimer")
        }
    }
}