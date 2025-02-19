package com.example.tpmobile.business

import androidx.compose.runtime.MutableState
import com.example.tpmobile.model.Commande
import com.example.tpmobile.model.Conteneur


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


fun sommeTotalPrixCommandes(
    resultatsOptimises: MutableState<Map<Conteneur, List<Commande>>>
): Double {
    val sommeTotale = resultatsOptimises.value.values.flatten().sumOf { it.prix }

    // Arrondir à un certain nombre de décimales
    return sommeTotale.toBigDecimal().setScale(2, java.math.RoundingMode.HALF_UP).toDouble()
}
