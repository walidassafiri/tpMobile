package com.example.tpmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlin.random.Random
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.tpmobile.model.Commande
import com.example.tpmobile.model.Conteneur
import com.example.tpmobile.ui.components.CommandeItem
import com.example.tpmobile.ui.components.ConteneurConfigItem
import com.example.tpmobile.ui.components.ConteneurItem


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


fun genererCommandesAleatoires(nombreCommandes: Int): List<Commande> {
    val commandes = mutableListOf<Commande>()
    val random = Random

    for (i in 1..nombreCommandes) {
        val poids = random.nextDouble() * 100
        val volume = random.nextDouble() * 10
        val prix = random.nextDouble() * 500
        val priorite = listOf("Haute", "Moyenne", "Basse").random()
        val fragile = random.nextBoolean()
        commandes.add(Commande(i, poids, volume, prix, priorite, fragile))
    }

    return commandes
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
                            resultatsOptimises.value = resultatsOptimises.value + (conteneur to optimiserConteneur(conteneur, commandes, commandesAffectees))
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
                            (conteneurs.sumOf { it.volumeMax } >= commandes.sumOf { it.volume })) {


                            messageErreur.value = ""


                            expedition.value = expedition.value + resetAndOptimizeConteneurs(
                                conteneurs,
                                commandes,
                                commandesAffecteesExpedition,
                                resultatsOptimises
                            )

                        } else {

                            messageErreur.value = "⚠️ Il n'y a pas assez de conteneurs pour expédier toutes les commandes."
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
                    val sum =sommeTotalPrixCommandes(plan)
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
                            val (tauxVolume, tauxPoids) = tauxUtilisationConteneur(conteneur, commandes)
                            Text(text="Volume utilisé: %.2f%%".format(tauxVolume))
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
                                Divider(color = Color.LightGray, thickness = 1.dp)
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

// Fonction pour formater les nombres avec 2 décimales
fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)