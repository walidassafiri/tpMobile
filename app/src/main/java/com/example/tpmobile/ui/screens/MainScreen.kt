package com.example.tpmobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tpmobile.business.optimiserConteneur
import com.example.tpmobile.business.resetAndOptimizeConteneurs
import com.example.tpmobile.business.sommeTotalPrixCommandes
import com.example.tpmobile.business.tauxUtilisationConteneur
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
    expedition: MutableState<List<Map<Conteneur, List<Commande>>>>,
    commandesAffecteesExpedition: MutableSet<Int>,
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
            val messageErreur = remember { mutableStateOf("") }

            Column {
                Button(
                    onClick = {
                        if ((conteneurs.sumOf { it.poidsMax } >= commandes.sumOf { it.poids }) &&
                            (conteneurs.sumOf { it.volumeMax } >= commandes.sumOf { it.volume })
                        ) {


                            messageErreur.value = ""


                            expedition.value = expedition.value + resetAndOptimizeConteneurs(
                                conteneurs,
                                commandes,
                                commandesAffecteesExpedition,
                                resultatsOptimises
                            )

                        } else {

                            messageErreur.value =
                                "⚠️ Il n'y a pas assez de conteneurs pour expédier toutes les commandes."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text("Plan d'expedition")
                }


                if (messageErreur.value.isNotEmpty()) {
                    Text(
                        text = messageErreur.value,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
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
        /*  item {
              val sum =sommeTotalPrixCommandes(resultatsOptimises)
              Text(
                  text = "Recette Total: ${sum} €",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
              )
          } */
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

        itemsIndexed(expedition.value) { index, plan ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "🚚 Plan d'expédition n°${index + 1}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1976D2)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    val sum = sommeTotalPrixCommandes(plan)
                    Text(
                        text = "Recette Total: ${sum} €",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    var conteneurIndex = 0
                    val conteneurCount = plan.size

                    plan.entries.forEach { (conteneur, commandes) ->
                        Column {
                            Text(
                                text = "📦 Conteneur : ${conteneur.id}",
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Color(0xFF388E3C)
                            )
                            val (tauxVolume, tauxPoids) = tauxUtilisationConteneur(
                                conteneur,
                                commandes
                            )
                            Text(text = "Volume utilisé: %.2f%%".format(tauxVolume))
                            Text(text = "Poids utilisé: %.2f%%".format(tauxPoids))
                            commandes.forEach { commande ->
                                Text(
                                    text = "   ➜ Commande : ${commande.numero}",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )
                            }

                            if (conteneurIndex < conteneurCount - 1) {
                                Spacer(modifier = Modifier.height(6.dp))
                                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            conteneurIndex++
                        }
                    }
                }
            }
        }

    }
}
