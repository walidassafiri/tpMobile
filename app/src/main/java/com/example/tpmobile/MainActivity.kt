package com.example.tpmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            var commandes by remember { mutableStateOf(genererCommandesAleatoires(5)) }

            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainScreen(navController = navController, commandes = commandes, onRegenerate = {
                        commandes = genererCommandesAleatoires(5)
                    })
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
            }
        }
    }
}

data class Commande(
    val numero: Int,
    val poids: Double,
    val volume: Double,
    val prix: Double,
    val priorite: String,
    val fragile: Boolean
)

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
    onRegenerate: () -> Unit // Callback pour régénérer la liste
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = onRegenerate, // Appeler le callback
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Régénérer la liste")
        }

        Spacer(modifier = Modifier.height(16.dp))

        ListeCommandes(commandes = commandes, navController = navController)
    }
}

@Composable
fun ListeCommandes(commandes: List<Commande>, navController: NavController) {
    LazyColumn {
        items(commandes) { commande ->
            CommandeItem(commande = commande, onClick = {
                navController.navigate("detail/${commande.numero}")
            })
        }
    }
}

@Composable
fun CommandeItem(commande: Commande, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Text(
            text = "Commande #${commande.numero}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = "Poids: ${commande.poids.format(2)} kg")
        Text(text = "Volume: ${commande.volume.format(2)} m³")
        Text(text = "Prix: ${commande.prix.format(2)} €")
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


// Fonction pour formater les nombres avec 2 décimales
fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)
















/*data class Message(val author: String, val body: String)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier.padding(16.dp)
    )
}

@Composable
fun MessageCard(msg: Message) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.profile_picture),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(text = msg.author, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = msg.body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}


//@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TpMobileTheme {
        Greeting("Android")
    }
}

//@Preview(showBackground = true)
@Composable
fun PreviewMessageCard() {
    TpMobileTheme {
        MessageCard(Message("Android", "Jetpack Compose!"))
    }
}
*/
