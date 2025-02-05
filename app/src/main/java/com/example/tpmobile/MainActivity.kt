package com.example.tpmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tpmobile.ui.theme.TpMobileTheme
import com.example.tpmobile.R
import kotlin.random.Random
import android.util.Log
import com.example.tpmobile.model.Commande
import com.example.tpmobile.model.Conteneur


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            var commandes by remember { mutableStateOf(genererCommandesAleatoires(15)) }
            var conteneurs by remember { mutableStateOf<List<Conteneur>>(emptyList()) }
            val commandesAffectees = remember { mutableStateOf(mutableSetOf<Int>()) }

            val resultatsOptimises = remember { mutableStateOf<Map<Conteneur, List<Commande>>>(emptyMap()) }

            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainScreen(
                        navController = navController,
                        commandes = commandes,
                        conteneurs = conteneurs,
                        commandesAffectees = commandesAffectees.value,
                        resultatsOptimises = resultatsOptimises,
                        onRegenerate = {
                            commandes = genererCommandesAleatoires(15)
                            commandesAffectees.value.clear()
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
                        id = conteneurs.size + 1, // Générer un ID unique
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

fun optimiserConteneur(
    conteneur: Conteneur,
    commandes: List<Commande>,
    commandesAffectees: MutableSet<Int>
): List<Commande> {
    val commandesDisponibles = commandes.filter { it.numero !in commandesAffectees }
    val commandesTriees = commandesDisponibles.sortedByDescending { it.prix }
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
            commandesAffectees.add(commande.numero)
        }
    }

    return commandesSelectionnees
}

@Composable
fun ConteneurItem(conteneur: Conteneur, commandes: List<Commande>, onClick: () -> Unit) {
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
    }
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