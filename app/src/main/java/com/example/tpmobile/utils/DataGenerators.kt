package com.example.tpmobile.utils

import com.example.tpmobile.model.Commande
import kotlin.random.Random

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
