package com.example.tpmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tpmobile.model.Commande
import com.example.tpmobile.model.Conteneur
import com.example.tpmobile.ui.screens.ConfigurerConteneursScreen
import com.example.tpmobile.ui.screens.DetailConteneurScreen
import com.example.tpmobile.ui.screens.DetailScreen
import com.example.tpmobile.ui.screens.MainScreen
import com.example.tpmobile.utils.genererCommandesAleatoires


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            var commandes by remember { mutableStateOf(genererCommandesAleatoires(15)) }
            var conteneurs by remember { mutableStateOf<List<Conteneur>>(emptyList()) }
            val commandesAffectees = remember { mutableStateOf(mutableSetOf<Int>()) }
            val commandesAffecteesExpedition = remember { mutableStateOf(mutableSetOf<Int>()) }
            val resultatsOptimises = remember { mutableStateOf<Map<Conteneur, List<Commande>>>(emptyMap()) }
            val expedition = remember { mutableStateOf<List<Map<Conteneur, List<Commande>>>>(emptyList()) }
            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainScreen(
                        navController = navController,
                        commandes = commandes,
                        conteneurs = conteneurs,
                        commandesAffectees = commandesAffectees.value,
                        resultatsOptimises = resultatsOptimises,
                        expedition = expedition,
                        commandesAffecteesExpedition = commandesAffecteesExpedition.value ,
                        onRegenerate = {
                            commandes = genererCommandesAleatoires(15)
                            commandesAffectees.value.clear()
                            resultatsOptimises.value = emptyMap()
                            expedition.value= emptyList()
                        },
                        onConfigurerConteneurs = {
                            navController.navigate("configurerConteneurs")
                        }
                    )
                }
                composable("configurerConteneurs") {
                    ConfigurerConteneursScreen(
                        conteneurs = conteneurs,
                        onAjouterConteneur = { nouveauConteneur ->
                            conteneurs = conteneurs + nouveauConteneur
                        },
                        onSupprimerConteneur = { conteneurASupprimer ->

                            conteneurs = conteneurs.filter { it != conteneurASupprimer }
                        },
                        navController = navController
                    )
                }
                composable("detail/{commandeId}") { backStackEntry ->
                    val commandeId = backStackEntry.arguments?.getString("commandeId")?.toIntOrNull()
                    val commande = commandes.firstOrNull { it.numero == commandeId }
                    if (commande != null) {
                        DetailScreen(commande = commande, navController = navController)
                    } else {
                        Text("Commande non trouvée")
                    }
                }
                composable("conteneur/{conteneurId}") { backStackEntry ->
                    val conteneurId = backStackEntry.arguments?.getString("conteneurId")?.toIntOrNull()
                    val conteneur = conteneurs.firstOrNull { it.id == conteneurId }
                    if (conteneur != null) {
                        val commandesSelectionnees = resultatsOptimises.value[conteneur] ?: emptyList()
                        DetailConteneurScreen(
                            conteneur = conteneur,
                            commandes = commandesSelectionnees,
                            navController = navController
                        )
                    } else {
                        Text("Conteneur non trouvé")
                    }
                }
            }
        }
    }
}


fun resetAndOptimizeConteneurs(
    conteneurs: List<Conteneur>,
    commandes: List<Commande>,
    commandesAffectees: MutableSet<Int>,
    resultatsOptimises: MutableState<Map<Conteneur, List<Commande>>>
):Map<Conteneur, List<Commande>> {
    val conteneursMelangees = conteneurs.shuffled()
    commandesAffectees.clear()
    val resultats = mutableMapOf<Conteneur, List<Commande>>()
    conteneursMelangees.forEach { conteneur ->
       // if (!resultatsOptimises.value.containsKey(conteneur)) {

            resultats[conteneur] = optimiserConteneur(conteneur, commandes, commandesAffectees)

     //   }

    }
    return resultats

}


fun sommeTotalPrixCommandes(
    resultatsOptimises: Map<Conteneur, List<Commande>>
): Double {
    val sommeTotale = resultatsOptimises.values.flatten().sumOf { it.prix }

    // Arrondir à un certain nombre de décimales
    return sommeTotale.toBigDecimal().setScale(2, java.math.RoundingMode.HALF_UP).toDouble()
}
fun tauxUtilisationConteneur(
    conteneur: Conteneur,
    commandes: List<Commande>
): Pair<Double, Double> {
    // Calcul du volume et du poids utilisés par les commandes
    val volumeUtilise = commandes.sumOf { it.volume }
    val poidsUtilise = commandes.sumOf { it.poids }

    // Calcul des taux d'utilisation
    val tauxVolume = if (conteneur.volumeMax != 0.0) {
        (volumeUtilise / conteneur.volumeMax) * 100
    } else {
        0.0
    }

    val tauxPoids = if (conteneur.poidsMax != 0.0) {
        (poidsUtilise / conteneur.poidsMax) * 100
    } else {
        0.0
    }

    return Pair(tauxVolume, tauxPoids)
}

fun optimiserConteneur(
    conteneur: Conteneur,
    commandes: List<Commande>,
    commandesAffectees: MutableSet<Int>
): List<Commande> {
    // Filtre les commandes déjà affectées
    val commandesDisponibles = commandes.filter { it.numero !in commandesAffectees }
    
    // Trie les commandes par rapport d'efficacité décroissant
    val commandesTriees = commandesDisponibles.sortedByDescending { it.prix / (it.poids + it.volume) }

    val commandesSelectionnees = mutableListOf<Commande>()
    var poidsTotal = 0.0
    var volumeTotal = 0.0


    for (commande in commandesTriees) {
        if (poidsTotal + commande.poids <= conteneur.poidsMax &&
            volumeTotal + commande.volume <= conteneur.volumeMax
        ) {
            commandesSelectionnees.add(commande)
            poidsTotal += commande.poids
            volumeTotal += commande.volume

            // Ajoute la commande à l'ensemble des commandes affectées
            commandesAffectees.add(commande.numero)
        }
    }

    return commandesSelectionnees
}


