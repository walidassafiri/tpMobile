package com.example.tpmobile.business

import androidx.compose.runtime.MutableState
import com.example.tpmobile.model.Commande
import com.example.tpmobile.model.Conteneur

fun resetAndOptimizeConteneurs(
    conteneurs: List<Conteneur>,
    commandes: List<Commande>,
    commandesAffectees: MutableSet<Int>,
    resultatsOptimises: MutableState<Map<Conteneur, List<Commande>>>
): Map<Conteneur, List<Commande>> {
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
