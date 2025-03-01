package com.example.tpmobile.business

import com.example.tpmobile.model.Commande
import com.example.tpmobile.model.Conteneur

fun optimiserConteneur(
    conteneur: Conteneur,
    commandes: List<Commande>,
    commandesAffectees: MutableSet<Int>
): List<Commande> {
    // Filtre les commandes déjà affectées
    val commandesDisponibles = commandes.filter { it.numero !in commandesAffectees }

    // Trie les commandes par rapport d'efficacité décroissant
    val commandesTriees =
        commandesDisponibles.sortedByDescending { it.prix / (it.poids + it.volume) }

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
