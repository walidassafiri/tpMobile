package com.example.tpmobile.utils

import com.example.tpmobile.model.Commande
import kotlin.random.Random

fun genererCommandesAleatoires(nombreCommandes: Int): List<Commande> {
    val commandes = mutableListOf<Commande>()
    val random = Random

    for (i in 1..nombreCommandes) {
        val poids = Random.nextDouble() * 100
        val volume = Random.nextDouble() * 10
        val prix = Random.nextDouble() * 500
        val priorite = listOf("Haute", "Moyenne", "Basse").random()
        val fragile = Random.nextBoolean()
        commandes.add(Commande(i, poids, volume, prix, priorite, fragile))
    }

    return commandes
}