package com.example.tpmobile.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tpmobile.business.optimiserConteneur
import com.example.tpmobile.business.sommeTotalPrixCommandes
import com.example.tpmobile.model.Commande
import com.example.tpmobile.model.Conteneur
import com.example.tpmobile.ui.components.CommandeItem
import com.example.tpmobile.ui.components.ConteneurItem



@Composable
fun MainScreen(
    navController: NavController,
    commandes: List<Commande>,
    conteneurs: List<Conteneur>,
    commandesAffectees: MutableSet<Int>,
    resultatsOptimises: MutableState<Map<Conteneur, List<Commande>>>,
    onRegenerate: () -> Unit,
    onConfigurerConteneurs: () -> Unit
) {


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Button(
                onClick = onRegenerate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text("Régénérer les commandes")
            }

            Button(
                onClick = onConfigurerConteneurs,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Configurer les conteneurs")
            }


            Button(
                onClick = {
                    conteneurs.forEach { conteneur ->
                        if (!resultatsOptimises.value.containsKey(conteneur)) {
                            resultatsOptimises.value =
                                resultatsOptimises.value + (conteneur to optimiserConteneur(
                                    conteneur,
                                    commandes,
                                    commandesAffectees
                                ))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Optimiser les conteneurs")
            }
        }

        item {
            Text(
                text = "Liste des commandes :",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(commandes) { commande ->
            val estAffectee = commande.numero in commandesAffectees
            CommandeItem(
                commande = commande,
                estAffectee = estAffectee,
                onClick = { navController.navigate("detail/${commande.numero}") }
            )
        }

        item {
            Text(
                text = "Conteneurs et commandes optimisées :",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }
        item {
            val sum = sommeTotalPrixCommandes(resultatsOptimises)
            Text(
                text = "Recette Total: ${sum} €",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }
        items(conteneurs) { conteneur ->
            val commandesSelectionnees = resultatsOptimises.value[conteneur] ?: emptyList()
            ConteneurItem(
                conteneur = conteneur,
                commandes = commandesSelectionnees,
                onClick = {
                    navController.navigate("conteneur/${conteneur.id}")
                }
            )
        }
    }
}
