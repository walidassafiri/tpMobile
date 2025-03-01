package com.example.tpmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
            val resultatsOptimises =
                remember { mutableStateOf<Map<Conteneur, List<Commande>>>(emptyMap()) }
            val expedition =
                remember { mutableStateOf<List<Map<Conteneur, List<Commande>>>>(emptyList()) }
            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainScreen(
                        navController = navController,
                        commandes = commandes,
                        conteneurs = conteneurs,
                        commandesAffectees = commandesAffectees.value,
                        resultatsOptimises = resultatsOptimises,
                        expedition = expedition,
                        commandesAffecteesExpedition = commandesAffecteesExpedition.value,
                        onRegenerate = {
                            commandes = genererCommandesAleatoires(15)
                            commandesAffectees.value.clear()
                            resultatsOptimises.value = emptyMap()
                            expedition.value = emptyList()
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
                    val commandeId =
                        backStackEntry.arguments?.getString("commandeId")?.toIntOrNull()
                    val commande = commandes.firstOrNull { it.numero == commandeId }
                    if (commande != null) {
                        DetailScreen(commande = commande, navController = navController)
                    } else {
                        Text("Commande non trouvée")
                    }
                }
                composable("conteneur/{conteneurId}") { backStackEntry ->
                    val conteneurId =
                        backStackEntry.arguments?.getString("conteneurId")?.toIntOrNull()
                    val conteneur = conteneurs.firstOrNull { it.id == conteneurId }
                    if (conteneur != null) {
                        val commandesSelectionnees =
                            resultatsOptimises.value[conteneur] ?: emptyList()
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
